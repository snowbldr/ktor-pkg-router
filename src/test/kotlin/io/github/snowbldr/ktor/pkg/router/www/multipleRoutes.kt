package io.github.snowbldr.ktor.pkg.router.www

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*

class multiRouteOne : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("hello one!")
    }
}

class multiRouteTwo : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("hello two!")
    }
}

class multiRouteThree : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("hello three!")
    }
}