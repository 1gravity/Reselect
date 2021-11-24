
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        classpath(Plugins.kotlin)
        classpath(Plugins.dokka)
        classpath(Plugins.android)
    }
}

plugins {
    id("de.fayard.buildSrcVersions") version "0.4.2"
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    group = project.properties["GROUP"]!!
    version = project.properties["VERSION_NAME"]!!
    if (hasProperty("SNAPSHOT") || System.getenv("SNAPSHOT") != null) {
        version = "$version-SNAPSHOT"
    }
}
