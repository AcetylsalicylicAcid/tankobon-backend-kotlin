package com.tankobon.webserver

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tankobon.database.service.UtilService
import com.tankobon.globalIssuer
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.security(utilService: UtilService) {
    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.RSA256(utilService.getPublicKey(), null))
                    .withIssuer(globalIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("uuid").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
