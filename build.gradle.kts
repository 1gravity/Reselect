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

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
