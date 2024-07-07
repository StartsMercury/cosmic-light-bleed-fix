object Constants {
    const val GROUP = "io.github.startsmercury"
    const val MODID = "cosmic-light-bleed-fix"
    const val VERSION = "0.1.0"

    const val DISPLAY_NAME = "Cosmic Light Bleed Fix"

    const val VERSION_COSMIC_REACH = "0.1.39"
    const val VERSION_COSMIC_QUILT = "2.1.1"
    const val VERSION_MODMENU = "1.0.4"
}

plugins {
    application
    `java-library`
}

base {
    group = Constants.GROUP
    archivesName = Constants.MODID
    version = createVersionString()
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    // Config to provide the Cosmic Reach project
    val cosmicreach by creating

    // Allows cosmic reach to be used in the codebase
    compileOnly { extendsFrom(cosmicreach) }

    // Allows to include something without it being in the maven (recommended to be used when including mods)
    val internal by creating {
        isVisible = false
        isCanBeConsumed = false
        isCanBeResolved = false
    }
    compileClasspath { extendsFrom(internal) }
    runtimeClasspath { extendsFrom(internal) }
    testCompileClasspath { extendsFrom(internal) }
    testRuntimeClasspath { extendsFrom(internal) }
}

repositories {
    ivy {
        name = "Cosmic Reach"
        url = uri("https://github.com/CRModders/CosmicArchive/raw/main/")
        patternLayout {
            artifact("/Cosmic Reach-[revision].jar")
        }
        // This is required in Gradle 6.0+ as metadata file (ivy.xml) is mandatory
        metadataSources {
            artifact()
        }

        content {
            includeGroup("finalforeach")
        }
    }

    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }

    maven {
        name = "Quilt"
        url = uri("https://maven.quiltmc.org/repository/release")
    }

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }

    maven {
        name = "Sponge"
        url = uri("https://repo.spongepowered.org/maven/")
    }

    // CRM repos, you may or may not want it as it is also on jitpack.
    // maven {
    //     name = "CRM"
    //     url = uri("https://maven.crmodders.dev/releases")
    // }

    mavenCentral()
}

dependencies {
    // Cosmic Reach jar
    "cosmicreach"(
        group = "finalforeach",
        name = "cosmicreach",
        version = Constants.VERSION_COSMIC_REACH,
    )

    // Cosmic Quilt
    "internal"(
        group = "org.codeberg.CRModders",
        name = "cosmic-quilt",
        version = Constants.VERSION_COSMIC_QUILT,
    )

    // Mod Menu
    // internal(
    //     group = "org.codeberg.CRModders",
    //     name = "modmenu",
    //     version = Constants.VERSION_MODMENU,
    // )
}

tasks {
    withType<ProcessResources> {
        // Locations of where to inject the properties
        val resourceTargets = listOf(
            "quilt.mod.json",
        )

        // Left item is the name in the target, right is the variable name
        val replaceProperties = mutableMapOf<String, Any>(
            "group" to Constants.GROUP,
            "modid" to Constants.MODID,
            "display_name" to Constants.DISPLAY_NAME,
            "version" to Constants.VERSION,

            "cosmicreach_version" to Constants.VERSION_COSMIC_REACH,
        )

        inputs.properties(replaceProperties)
        replaceProperties["project"] = project

        filesMatching(resourceTargets) {
            expand(replaceProperties)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    javadoc {
        options {
            this as StandardJavadocDocletOptions

            source = "17"
            encoding = "UTF-8"
            charSet = "UTF-8"
            memberLevel = JavadocMemberLevel.PACKAGE
            addStringOption("Xdoclint:none", "-quiet")
            tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:",
            )
        }

        source(sourceSets.main.get().allJava)
        classpath = files(sourceSets.main.get().compileClasspath)
        include("**/api/**")
        isFailOnError = true
    }

    val run: Task by getting {
        this as JavaExec

        dependsOn("jar")

        // Change the run directory
        val runningDir = file("run/")
        if (!runningDir.exists()) {
            runningDir.mkdirs()
        }
        workingDir = runningDir
    }
}

application {
    // As Quilt is our loader, use its main class at:
    mainClass = "org.quiltmc.loader.impl.launch.knot.KnotClient"
    applicationDefaultJvmArgs = listOf(
        // Allows stuff to be found through the classpath
        "-Dloader.development=true",
        // Defines path to Cosmic Reach
        "-Dloader.gameJarPath=" + configurations.getByName("cosmicreach").asPath,
    )
}

fun createVersionString(): String {
    val builder = StringBuilder()

    val isReleaseBuild = project.hasProperty("build.release")
    val buildId = System.getenv("GITHUB_RUN_NUMBER")

    if (isReleaseBuild) {
        builder.append(Constants.VERSION)
    } else {
        builder.append(Constants.VERSION.substringBefore('-'))
        builder.append("-snapshot")
    }

    builder.append("+cr").append(Constants.VERSION_COSMIC_REACH)

    if (!isReleaseBuild) {
        if (buildId != null) {
            builder.append("-build.${buildId}")
        } else {
            builder.append("-local")
        }
    }

    return builder.toString()
}
