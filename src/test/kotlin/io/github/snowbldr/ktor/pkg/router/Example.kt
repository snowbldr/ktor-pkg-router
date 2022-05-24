package io.github.snowbldr.ktor.pkg.router

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, 8181) {
        install(PackageRouter("io.github.snowbldr.ktor.pkg.router.www"))
    }.start(true)
}