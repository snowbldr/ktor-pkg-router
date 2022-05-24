package io.github.snowbldr.ktor.pkg.router.www.stuff

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class index : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("you found stuff")
    }
}