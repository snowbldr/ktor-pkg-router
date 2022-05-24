package io.github.snowbldr.ktor.pkg.router

import io.ktor.http.HttpMethod.Companion.DefaultMethods
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class AppIT {
    @Test
    fun checkHealthy() {
        Assertions.assertThat(getString("$appUrl/health")).isEqualTo("OK")
    }

    @Test
    fun indexResolves() {
        Assertions.assertThat(getString(appUrl)).isEqualTo("You found the root in app test app!")
    }

    @Test
    fun helloResolves() {
        Assertions.assertThat(getString("$appUrl/hello?name=world")).isEqualTo("hello world!")
    }

    @Test
    fun stuffIndexResolves() {
        Assertions.assertThat(getString("$appUrl/stuff")).isEqualTo("you found stuff")
    }

    @Test
    fun stuffParamResolves() {
        Assertions.assertThat(getString("$appUrl/stuff/foo")).isEqualTo("found stuff foo")
    }

    @Test
    fun allMethodsWork() {
        for (method in DefaultMethods) {
            val response = retrieveString(method.value, "$appUrl/stuff/foo")
            Assertions.assertThat(response).isNotNull
            if (method.value == "HEAD") {
                Assertions.assertThat(response.statusCode()).isEqualTo(200)
            } else {
                Assertions.assertThat(response.body()).isEqualTo("found stuff foo")
            }
        }
    }

    @Test
    fun nestedParamResolves() {
        Assertions.assertThat(getString("$appUrl/stuff/foo/bar")).isEqualTo("foo and bar")
    }

    @Test
    fun catchAllResolves() {
        Assertions.assertThat(getString("$appUrl/stuff/meow/catchAll/fizzle/bizzle/bazzle"))
            .isEqualTo("meow at /fizzle/bizzle/bazzle")
    }

    @Test
    fun multipleRoutesInAFileResolve() {
        Assertions.assertThat(getString("$appUrl/multiRouteOne")).isEqualTo("hello one!")
        Assertions.assertThat(getString("$appUrl/multiRouteTwo")).isEqualTo("hello two!")
        Assertions.assertThat(getString("$appUrl/multiRouteThree")).isEqualTo("hello three!")
    }

    companion object {
        private const val port = 44420
        private const val appUrl = "http://localhost:$port"
        private val client = HttpClient.newHttpClient()

        @BeforeAll
        @JvmStatic
        fun startApp() {
            Thread {
                embeddedServer(Netty, port) {
                    attributes.put(AttributeKey("appName"), "test app")
                    install(PackageRouter("io.github.snowbldr.ktor.pkg.router.www"))
                }.start(true)
            }.start()
            var response = try{ getString("$appUrl/health") } catch (e: Exception){null}
            val start = System.currentTimeMillis()
            while (response == null || response != "OK") {
                if (System.currentTimeMillis() - start > 30000) {
                    throw RuntimeException("Timed out waiting for app to start")
                }
                Thread.sleep(50)
                response = try{ getString("$appUrl/health") } catch (e: Exception){null}
            }
            println("App Started!")
        }

        fun getString(url: String): String? = try {
            retrieveString("GET", url).body()
        } catch (e: Exception) {
            throw e
        }

        fun retrieveString(method: String, url: String): HttpResponse<String> = client.send(
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }
}