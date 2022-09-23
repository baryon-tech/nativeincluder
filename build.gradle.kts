import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10" apply false
    kotlin("multiplatform") version "1.7.10" apply false
    id("maven-publish")
}

group = "io.github.baryontech.nativeincluder"
version = "1.0.0"

subprojects {
    group = "io.github.baryontech.nativeincluder"
    repositories {
        mavenCentral()
    }
    apply(plugin = "maven-publish")
}


