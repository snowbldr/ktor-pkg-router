package io.github.snowbldr.ktor.pkg.router.www.stuff.`$param`

import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

class index : KtorRoute {
    suspend fun get(call: ApplicationCall) {
        call.respond("found stuff ${call.parameters["param"]}")
    }

    suspend fun head(call: ApplicationCall) {
        call.respond("found stuff ${call.parameters["param"]}")
    }

    suspend fun post(call: ApplicationCall) {
        call.respond("found stuff ${call.parameters["param"]}")
    }

    suspend fun put(call: ApplicationCall) {
        call.respond("found stuff ${call.parameters["param"]}")
    }

    suspend fun patch(context: PipelineContext<*, ApplicationCall>, call: ApplicationCall) {
        call.respond("found stuff ${context.call.parameters["param"]}")
    }

    suspend fun delete(context: PipelineContext<*, ApplicationCall>) {
        context.call.respond("found stuff ${context.call.parameters["param"]}")
    }

    suspend fun options(call: ApplicationCall, context: PipelineContext<*, ApplicationCall>) {
        call.respond("found stuff ${context.call.parameters["param"]}")
    }
}