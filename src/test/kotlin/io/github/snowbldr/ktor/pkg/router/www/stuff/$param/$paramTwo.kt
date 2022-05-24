package io.github.snowbldr.ktor.pkg.router.www.stuff.`$param`

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*

class `$paramTwo` : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("${call.parameters["param"]} and ${call.parameters["paramTwo"]}")
    }
}