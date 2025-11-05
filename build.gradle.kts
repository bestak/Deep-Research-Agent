plugins {
    kotlin("jvm") version "2.2.20"
}

group = "cz.bestak.deepresearch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.aallam.openai:openai-client:4.0.1")
    implementation("io.ktor:ktor-client-apache5:3.3.1")

    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}