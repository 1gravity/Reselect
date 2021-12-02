pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.toString() == "com.arkivanov.gradle.setup") {
                useModule("com.github.arkivanov:gradle-setup-plugin:44ca9f629c")
            }
        }
    }

    plugins {
        id("com.arkivanov.gradle.setup")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.23.0"
}

include(":redux-kotlin-select")
include(":demo")

// this is an annoying workaround since the Maven plugin seems to use the Gradle project name as
// artifact id instead of the id defined in the plugin configuration so now the Gradle project name
// just matches the artifact id (see also: https://github.com/gradle/gradle/issues/15731
// -> issue is closed, why...?)
project(":redux-kotlin-select").projectDir = file("./select")
