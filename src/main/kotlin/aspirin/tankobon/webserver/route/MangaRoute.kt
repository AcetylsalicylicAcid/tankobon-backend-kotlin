package aspirin.tankobon.webserver.route

import aspirin.tankobon.database.service.MangaService
import aspirin.tankobon.globalMangaPath
import aspirin.tankobon.globalThumbPath
import aspirin.tankobon.utils.callToFile
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.mangaRoute(mangaService: MangaService) {
    authenticate("auth-jwt") {

        get("/list") {
            call.respond(mangaService.getMangaList())
        }

        get("/manga/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, globalMangaPath))
        }

        get("/thumb/{uuid}/{volume}/{page}") {
            call.respondFile(callToFile(call, globalThumbPath))
        }
    }
}