package io.github.snowbldr.ktor.pkg.router.www.stuff.`$param`

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class catchAll_ : KtorRoute {
    suspend fun get(context: PipelineContext<*, ApplicationCall>) {
        context.call.respond("${context.call.parameters["param"]} at /${context.call.parameters.getAll("restPath")!!.joinToString("/")}")
    }
}