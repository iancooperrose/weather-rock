import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.30"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}
