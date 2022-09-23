plugins {
    kotlin("multiplatform")
}
version = "1.0.1"

kotlin {
    // shifty workaround to get this dependency working on ALL platforms xD
    jvm() {}
    mingwX64() {}
    mingwX86() {}
    linuxX64() {}
    macosX64() {}
    macosArm64() {}
    sourceSets {
        val commonMain by getting {

        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.sascha-t.de/public")
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("MVN_PUBLISH_SASCHA_USER")
                password = System.getenv("MVN_PUBLISH_SASCHA_TOKEN")
            }
        }
    }
}
