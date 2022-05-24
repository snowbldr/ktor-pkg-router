import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
group = "io.github.snowbldr.ktor"
description = "Package based routing for Ktor"
version = "1.0.0"
plugins {
    kotlin("jvm") version "1.6.20"
    `java-library`
    `maven-publish`
    signing
    id("com.diffplug.spotless") version "6.2.2"
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.0.1"

dependencies {
    api("org.reflections:reflections:0.10.2")
    api("io.ktor:ktor-server-core:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.assertj:assertj-core:3.22.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.register("lint") {
    dependsOn(tasks.spotlessCheck)
}

tasks.register("lintFix") {
    dependsOn(tasks.spotlessApply)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "ktor-pkg-router"
            from(components["java"])
            versionMapping {
                usage("java-api"){
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Ktor Package Router")
                description.set("Package based routing for Ktor")
                url.set("https://github.com/snowbldr/ktor-pkg-router")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://github.com/snowbldr/ktor-pkg-router/blob/main/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("snowbldr")
                        name.set("Snow Builder")
                        email.set("r@kmtn.me")
                        organization.set("snowbldr")
                        organizationUrl.set("https://github.com/snowbldr/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/snowbldr/ktor-pkg-router.git")
                    developerConnection.set("scm:git:ssh://github.com:snowbldr/ktor-pkg-router.git")
                    url.set("https://github.com/snowbldr/ktor-pkg-router")
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = properties["sonatypeUsername"].toString()
                password = properties["sonatypePassword"].toString()
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if(JavaVersion.current().isJava9Compatible){
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}