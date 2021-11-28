import de.fayard.refreshVersions.core.FeatureFlag

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.23.0"
}

refreshVersions {
    featureFlags {
        enable(FeatureFlag.LIBS)
        enable(FeatureFlag.GRADLE_UPDATES)
    }
    enableBuildSrcLibs()
}

rootProject.name = "redux-kotlin-select"

include(
    ":redux-kotlin-select",
    ":sample"
)
// this is an annoying workaround since the Maven plugin seems to use the Gradle project name as
// artifact id instead of the id defined in the plugin configuration so now the Gradle project name
// just matches the artifact id (see also: https://github.com/gradle/gradle/issues/15731
// -> issue is closed, why...?)
project(":redux-kotlin-select").projectDir = file("./select")
