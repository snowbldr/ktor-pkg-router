# ktor-pkg-router

Package based routing for the ktor web framework

## What is it

This library provides the ability to have routes defined by their package and class names, much like they're defined in
the apache web server and other static file servers.

Using package based routing makes finding and managing routes in a web application trivial. Look at the
url being used, and follow the directory path down to the handler.

This prevents hunting through a web app and trying to piece together bits of routes, or trying to match patterns to find
where the route is being handled.

It also makes route configuration automatic, create a new file in the right place and your route is configured and ready to go.

## How to use it

First install the dependency
Gradle kotlin:

```kotlin
implementation("io.github.snowbldr.ktor:ktor-pkg-router:1.0.0")
```

Maven:

```xml

<dependency>
    <groupId>io.github.snowbldr.ktor</groupId>
    <artifactId>ktor-pkg-router</artifactId>
    <version>1.0.0</version>
</dependency>
```

Make a package to put your routes in, and add route classes. Each class must implement KtorRoute.

This example assumes that each of these files has one class with the same name as the file.

The file name is actually ignored, and the class name is used for the last part of the route, meaning it's ok to put
multiple route classes in the same file. See [multipleRoutes.kt](https://github.com/snowbldr/ktor-pkg-router/tree/main/src/test/kotlin/io/github/snowbldr/ktor/pkg/router/www/multipleRoutes.kt) for an example.

- src/main/kotlin
    - com.mycompany.myapp
        - www
            - stuff
                - index.kt
                - secrets.kt
            - index.kt
            - hello.kt

These routes map to the following paths:
<pre>
- index.kt         -> /
- hello.kt         -> /hello
- stuff/index.kt   -> /stuff 
- stuff/secrets.kt -> /stuff/secrets
</pre>

Here's an example of one of index the routes:

```kotlin
import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.ApplicationCall

class index : KtorRoute {
    suspend fun get(call: ApplicationCall) = call.respond("Hello world!")
}
```

Install the PackageRouter plugin in your Ktor app, and pass the base package containing your routes.

```kotlin
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, 8181) {
        install(PackageRouter("com.mycompany.myapp.www"))
    }.start(true)
}
```

That's it!

See [Test Routes](https://github.com/snowbldr/ktor-pkg-router/tree/main/src/test/kotlin/io/github/snowbldr/ktor/pkg/router/www)
,
[Integration Tests](https://github.com/snowbldr/ktor-pkg-router/blob/main/src/test/kotlin/io/github/snowbldr/ktor/pkg/router/AppIT.kt)
,
and [Example app](https://github.com/snowbldr/ktor-pkg-router/blob/main/src/test/kotlin/io/github/snowbldr/ktor/pkg/router/Example.kt)
For more examples

## Request method functions

The function name is used as the http method. Any http method that ktor will accept is valid.

The functions are picked based on the parameters. You can either accept an instance of ApplicationCall, an instance of
PipelineContext<*, ApplicationCall>, or both in either order.

If a function is defined with any other parameters, it's ignored.

Here's are examples of valid function definitions

```kotlin
import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.ApplicationCall

class foo : KtorRoute {
    
    suspend fun get(call: ApplicationCall) = call.respond("Hello world!")
    
    suspend fun post(context: PipelineContext<*, ApplicationCall>) = context.call.respond("Hello world!")
    
    suspend fun put(context: PipelineContext<*, ApplicationCall>, call: ApplicationCall) =
        context.call.respond("Hello ${call.parameters["name"]}")
    
    suspend fun patch(call: ApplicationCall, context: PipelineContext<*, ApplicationCall>) =
        call.respond("Hello ${context.call.parameters["name"]}")
}
```

## Ktor Application instance access

If your route needs access to the Ktor Application object, add a constructor with Application as a parameter and it will
be injected when the Route is instantiated.

```kotlin
import io.github.snowbldr.ktor.pkg.router.KtorRoute
import io.ktor.server.application.ApplicationCall

class index(private val app: Application) : KtorRoute {

    suspend fun get(call: ApplicationCall) {
        return "Application $app";
    }
}
```

## Path Parameters

Single parameters are supported by prefixing the name of the class with a `$` (i.e. $userId.kt)

- src/main/kotlin
    - com.mycompany.myapp
        - www
            - users
                - $userId.kt

`www.users.$userId` maps to the path `/users/{userId}` in your ktor app

## CatchAll

Catchall paths are supported by suffixing the name of the class with a `_` (i.e. catch_.kt)

- src/main/kotlin
    - com.mycompany.myapp
        - www
            - images_.kt

`www.images_.kt` maps to the path `/images*path` in your Kotlin app.

The remainder of the path is always set to the variable name `restPath` and is accessible via `call.parameters.getAll("restPath")` in
the handler.

## How it works

When the Ktor application starts up, the PackageRouter scans the provided base package for all classes implementing the
KtorRoute interface.

The package of each class is converted to a valid Ktor route path string, and then added to the app.

Methods that accept valid parameters are added as a route with the function name as the method.