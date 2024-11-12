plugins {
    kotlin("jvm") version "1.9.23"
    `java-library`
}

group = "io.transferbot.core"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging:7.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.register("prepareKotlinBuildScriptModel")