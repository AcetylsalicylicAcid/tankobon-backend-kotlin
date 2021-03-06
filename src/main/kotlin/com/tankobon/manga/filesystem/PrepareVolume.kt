package com.tankobon.manga.filesystem

import com.tankobon.globalThumbPath
import com.tankobon.logger
import com.tankobon.utils.thumbnailGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

fun prepareVolume(volumePath: File) {
    logger.info("Volume preparation: ${volumePath.path}")

    runBlocking {
        coroutineScope {
            volumePath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
                ?.forEach {
                    print("prepareVolume archiveNavigator ${Thread.currentThread().name}")
                    withContext(Dispatchers.Default) { fileNavigator(it) }
                }
        }

        volumePath.listFiles()?.filter { e -> e.isDirectory }
            ?.forEach {
                fileLevelRecursion(it, volumePath.absolutePath)
                it.delete()
            }

        coroutineScope {
            volumePath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
                ?.forEach {
                    print("prepareVolume imageNavigator ${it.name} ${Thread.currentThread().name}")
                    withContext(Dispatchers.Default) { fileNavigator(it) }
                }
        }

        coroutineScope {
            val newThumb = File("${globalThumbPath.path}/${volumePath.parentFile.name}/${volumePath.name}")
            newThumb.mkdirs()

            if (newThumb.listFiles().isNullOrEmpty()) {
                volumePath.listFiles()?.filter { it.isFile && !it.name.contains(".DS_Store") }
                    ?.sortedBy { it.name.toString() }
                    ?.forEachIndexed { i, e ->
                        val path = File("${e.parentFile.path}/$i.${e.extension}")
                        e.renameTo(path)
                        withContext(Dispatchers.Default) {
                            print("prepareVolume thumbnailGenerator ${Thread.currentThread().name}")
                            thumbnailGenerator(path, newThumb)
                        }
                    }
            }
        }
    }
}
