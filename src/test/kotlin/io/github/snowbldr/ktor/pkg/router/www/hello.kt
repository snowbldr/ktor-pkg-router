package io.github.snowbldr.ktor.pkg.router.www

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class hello : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("hello ${call.request.queryParameters["name"]}!")
    }
}