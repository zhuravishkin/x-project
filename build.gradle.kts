plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.spring") version "2.0.21" apply false
    kotlin("plugin.jpa") version "2.0.21" apply false
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}

group = "com.zhuravishkin"
version = "0.0.1"
description = "Demo project for Spring Boot"

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
