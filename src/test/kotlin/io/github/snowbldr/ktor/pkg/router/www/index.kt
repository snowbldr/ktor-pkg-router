package io.github.snowbldr.ktor.pkg.router.www

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

class index(private val app: Application) : KtorRoute {

    private val appNameKey = AttributeKey<Any>("appName")

    init {
        if (!app.attributes.contains(appNameKey)) {
            app.attributes.put(appNameKey, "default app")
        }
    }

    suspend fun get(call: ApplicationCall) {
        call.respond("You found the root in app ${app.attributes.get(appNameKey)}!")
    }
}