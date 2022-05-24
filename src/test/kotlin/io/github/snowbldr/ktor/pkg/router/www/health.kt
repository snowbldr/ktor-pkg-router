package io.github.snowbldr.ktor.pkg.router.www

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class health : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("OK")
    }

    suspend fun head(call: ApplicationCall) {
        call.respond("")
    }
}