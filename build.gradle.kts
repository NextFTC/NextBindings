plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    `java-library`
    alias(libs.plugins.deployer)
}

group = "dev.nextftc"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}