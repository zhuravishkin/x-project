plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.detekt) apply false
}

group = "com.zhuravishkin"
version = "0.0.1"
description = "Demo project for Spring Boot"

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(
            files(
                "$rootDir/detekt-config.yml",
                "$projectDir/detekt-config.yml"
            )
        )
        buildUponDefaultConfig = true
        parallel = true
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }

        configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            compilerOptions {
                freeCompilerArgs.addAll("-Xjsr305=strict")
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
