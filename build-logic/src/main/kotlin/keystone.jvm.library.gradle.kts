import org.gradle.accessors.dm.LibrariesForLibs

/**
 * Convention plugin for Keystone JVM modules: Kotlin/JVM, detekt (with formatting rules),
 * Kover with the project-wide minimum coverage rule, and the JUnit 5 platform.
 */
plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
}

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain(21)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

kover {
    reports {
        verify {
            rule {
                minBound(90)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    testImplementation(libs.kotlin.test.junit5)
}
