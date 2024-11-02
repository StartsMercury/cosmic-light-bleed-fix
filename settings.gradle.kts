buildscript {
    repositories {
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
        mavenCentral()
    }

    dependencies {
        classpath(
            group = "org.codeberg.CRModders",
            name = "cosmic-loom",
            version = "PR7-SNAPSHOT",
        )
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
