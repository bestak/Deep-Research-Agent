plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "cz.bestak.deepresearch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.aallam.openai:openai-client:4.0.1")
    implementation("io.ktor:ktor-client-apache5:3.3.1")

    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("io.ktor:ktor-client-core:${ktor_version}")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}