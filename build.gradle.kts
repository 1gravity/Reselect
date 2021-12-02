import com.arkivanov.gradle.AndroidConfig
import com.arkivanov.gradle.PublicationConfig
import com.arkivanov.gradle.Target
import com.arkivanov.gradle.named
import com.arkivanov.gradle.nativeSet

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:_")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt")
    id("com.arkivanov.gradle.setup")
}

setupDefaults {
    multiplatformTargets(
        Target.Android,
        Target.Jvm,
        Target.Js(),
        Target.Linux,
        Target.Ios(),
        Target.WatchOs(),
        Target.TvOs(),
        Target.MacOs(),
    )

    multiplatformSourceSets {
        val nonNative by named(common)
        val nonAndroid by named(common)
        val native by named(nonAndroid)

        listOf(jvm, js).dependsOn(nonAndroid)
        listOf(android, jvm, js).dependsOn(nonNative)
        nativeSet.dependsOn(native)
    }

    androidConfig(
        AndroidConfig(
            minSdkVersion = 21,
            compileSdkVersion = 31,
            targetSdkVersion = 31
        )
    )

    fun Project.getProperty(key: String) =
        properties[key]?.toString() ?: throw IllegalStateException("missing property $key")

    fun getRepositoryUrl(): String {
        val isReleaseBuild = ! getProperty("POM_VERSION_NAME").contains("SNAPSHOT")
        val releaseRepoUrl = getProperty("RELEASE_REPOSITORY_URL")
        val snapshotRepoUrl = getProperty("SNAPSHOT_REPOSITORY_URL")
        return uri(if (isReleaseBuild) releaseRepoUrl else snapshotRepoUrl).toString()
    }

    publicationConfig(
        PublicationConfig(
            group = getProperty("POM_GROUP_ID"),
            version = getProperty("POM_VERSION_NAME"),
            projectName = getProperty("POM_DEVELOPER_EMAIL"),
            projectDescription = getProperty("POM_DESCRIPTION"),
            projectUrl = getProperty("POM_URL"),
            scmUrl = getProperty("POM_SCM_URL"),
            licenseName = getProperty("POM_LICENCE_NAME"),
            licenseUrl = getProperty("POM_LICENCE_URL"),
            developerId = getProperty("POM_DEVELOPER_ID"),
            developerName = getProperty("POM_DEVELOPER_NAME"),
            developerEmail = getProperty("POM_DEVELOPER_EMAIL"),
            signingKey = getProperty("signingKey"),
            signingPassword = getProperty("signingKeyPassword"),
            repositoryUrl = getRepositoryUrl(),
            repositoryUserName = getProperty("ossUsername"),
            repositoryPassword = getProperty("ossPassword"),
        )
    )
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }

    plugins.apply("io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.19.0"
        parallel = true
        config = files("$rootDir/detekt.yml")
        source = files(file("src").listFiles()?.find { it.isDirectory } ?: emptyArray<Any>())
    }
}
