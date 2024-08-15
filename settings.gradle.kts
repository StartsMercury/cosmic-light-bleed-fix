pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "cosmicloom" -> {
                    val version = requested.version!!
                    val jitpack = version.endsWith("-SNAPSHOT")
                        || version.startsWith("PR")
                        || version.contains('/')
                        || !version.contains('.')
                    if (jitpack) {
                        useModule("org.codeberg.CRModders:cosmic-loom:${requested.version}")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "CRModders"
            url = uri("https://maven.crmodders.dev/plugins")
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "CRModders"
                    url = uri("https://maven.crmodders.dev/plugins")
                }
            }
            filter {
                includeModule("cosmicloom", "cosmicloom.gradle.plugin")
                includeModule("dev.crmodders", "CosmicLoom")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "Jitpack"
                    url = uri("https://jitpack.io")
                }
            }
            filter {
                includeModule("org.codeberg.CRModders", "cosmic-loom")
                includeModule("org.codeberg.CRModders.cosmic-loom", "CosmicLoom")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
