package io.github.snowbldr.ktor.pkg.router

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.*

/**
 * A Ktor plugin that provides automated routing configuration based on packages and class names
 */
fun PackageRouter(routePackage: String) = createApplicationPlugin("PackageRouter") {
    val reflections = Reflections(routePackage)
    val routes = reflections.get(SubTypes.of(KtorRoute::class.java).asClass<Any>()).map { it.kotlin }
    application.routing {
        for (route in routes) {
            val routeInstance = constructHandler(route, application)
            route.declaredFunctions
                .filter {
                    it.parameters[0].type == route.starProjectedType
                            && ((it.parameters.size == 2 && isRouteCallType(it.parameters[1].type))
                            || (it.parameters.size == 3 && isRouteCallType(it.parameters[1].type) && isRouteCallType(it.parameters[2].type)))
                }.forEach { handlerFunc ->
                    val handler = createHandler(handlerFunc, routeInstance)
                    route(
                        toRoutePath(routePackage, route.qualifiedName!!),
                        HttpMethod.parse(handlerFunc.name.uppercase())
                    ) {
                        handle { handler(call, this) }
                    }
                }
        }
    }
}

private fun createHandler(
    handlerFunc: KFunction<*>,
    routeInstance: Any
): suspend (ApplicationCall, PipelineContext<*, ApplicationCall>) -> Unit = if (handlerFunc.parameters.size == 2) {
    if (handlerFunc.parameters[1].type == callType) {
        { call, _ -> handlerFunc.callSuspend(routeInstance, call) }
    } else {
        { _, ctx -> handlerFunc.callSuspend(routeInstance, ctx) }
    }
} else {
    if (handlerFunc.parameters[1].type == callType) {
        { call, ctx -> handlerFunc.callSuspend(routeInstance, call, ctx) }
    } else {
        { call, ctx -> handlerFunc.callSuspend(routeInstance, ctx, call) }
    }
}

private val contextType = PipelineContext::class.createType(
    listOf(
        KTypeProjection.STAR, KTypeProjection.invariant(ApplicationCall::class.createType())
    )
)
private val callType = ApplicationCall::class.createType()
private fun isRouteCallType(type: KType): Boolean = type == callType || type == contextType

private fun constructHandler(route: kotlin.reflect.KClass<*>, app: Application): Any {
    val primaryConstructor = route.primaryConstructor
        ?: throw java.lang.RuntimeException("Cannot construct instance of ${route.qualifiedName}, no primary constructor")
    return if (primaryConstructor.parameters.size == 1 && primaryConstructor.parameters[0].type.isSubtypeOf(Application::class.createType()))
        primaryConstructor.call(app)
    else if (primaryConstructor.parameters.isEmpty()) {
        primaryConstructor.call()
    } else {
        throw RuntimeException(
            "Cannot construct new instance of " + route.qualifiedName + ", no public constructor available. " +
                    "Must either provide a noArgs constructor, or a constructor that receives only an instance of io.jooby.Jooby"
        )
    }
}

private fun toRoutePath(routePackage: String, routeClass: String): String {
    var path: String = routeClass.substring(routePackage.length + 1).replace("\\.".toRegex(), "/")
    path = "/" + path.split("/")
        .map { part: String ->
            if (part.startsWith("$")) "{" + part.substring(1) + "}"
            else part
        }.joinToString("/") { part: String ->
            if (part.endsWith("_"))
                part.substring(0, part.length - 1) + "/{restPath...}"
            else part
        }
    if (path.endsWith("index")) {
        path = path.substring(0, path.length - "/index".length)
    }
    return path
}