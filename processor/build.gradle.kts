plugins {
    kotlin("jvm")
}
version = "1.0.0"

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
    implementation("com.squareup:kotlinpoet-ksp:1.11.0")
    implementation(project(":annotations"))
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"]);
        }
    }
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
