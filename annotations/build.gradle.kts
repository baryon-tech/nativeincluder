plugins {
    kotlin("jvm")
}
version = "1.0.0"

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
