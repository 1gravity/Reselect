pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Redux-Kotlin-Select"

include(
    ":select",
    ":sample"
)
