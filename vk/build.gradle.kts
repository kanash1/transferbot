plugins {
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.jetbrains.kotlin.plugin.jpa") version "2.0.20-Beta2"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	application
}

group = "io.transferbot.vk"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":core"))
	implementation(project(":shared"))
	implementation("io.github.oshai:kotlin-logging:7.0.0")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("com.vk.api:sdk:1.0.16")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.slf4j:slf4j-api:2.0.16")
	testImplementation("ch.qos.logback:logback-classic:1.5.6")
	testImplementation("ch.qos.logback:logback-core:1.5.6")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
	mainClass = "io.transferbot.vk.VkBotApplicationKt"
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel")
