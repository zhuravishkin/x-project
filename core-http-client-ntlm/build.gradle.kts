import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("jacoco")
}

group = "com.zhuravishkin.core"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("net.javacrumbs.shedlock:shedlock-spring:6.9.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:6.9.0")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.wiremock:wiremock-standalone:3.13.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

detekt {
    config.setFrom(
        files(
            "$rootDir/detekt-config.yml",
            "$projectDir/detekt-config.yml"
        )
    )
    buildUponDefaultConfig = true
    parallel = true

    tasks.withType<Detekt>().configureEach {
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    reports {
        csv.required.set(false)
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.initJacocoExcludes()

    val test: Test by tasks
    dependsOn(test)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = BigDecimal(0.75)
            }
            limit {
                counter = "BRANCH"
                minimum = BigDecimal(0.15)
            }
        }
    }

    classDirectories.initJacocoExcludes("**/repository/**")
}

private fun ConfigurableFileCollection.initJacocoExcludes(vararg additionalExcludes: String) {
    val excludes = listOf(
        "**/*ApplicationKt*"
    ) + additionalExcludes.toList()
    this.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(excludes)
        }
    )
}
