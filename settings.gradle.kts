
rootProject.name = "nativeincluder"

pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "1.7.10-1.0.6"
        kotlin("jvm") version "1.7.10"
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

include("processor")
include("annotations")
