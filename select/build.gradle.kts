import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

plugins {
    kotlin("multiplatform")
    id("com.android.library")

    id("publish")
}

val props: MutableMap<String, *> = project.properties
group = props["POM_GROUP_ID"].toString()
version = props["POM_VERSION_NAME"].toString()

enum class HostType {
    MAC_OS, LINUX
}

fun KotlinTarget.getHostType(): HostType? =
    when (platformType) {
        KotlinPlatformType.androidJvm,
        KotlinPlatformType.jvm,
        KotlinPlatformType.js -> HostType.LINUX
        KotlinPlatformType.native ->
            when {
                name.startsWith("ios") -> HostType.MAC_OS
                name.startsWith("macos") -> HostType.MAC_OS
                name.startsWith("watchos") -> HostType.MAC_OS
                name.startsWith("linux") -> HostType.LINUX
                else -> error("Unsupported native target: $this")
            }

        KotlinPlatformType.common -> null
    }

fun KotlinTarget.isCompilationAllowed(): Boolean {
    if (name == KotlinMultiplatformPlugin.METADATA_TARGET_NAME) {
        return true
    }

    val os = DefaultNativePlatform.getCurrentOperatingSystem()

    return when (getHostType()) {
        HostType.MAC_OS -> os.isMacOsX
        HostType.LINUX -> os.isLinux
        null -> true
    }
}

fun Project.disableCompilationsIfNeeded() {
    fun KotlinTarget.configureCompilations(enabled: Boolean) {
        compilations.configureEach {
            compileKotlinTask.enabled = enabled
        }
    }

    val targets = extensions.getByType<KotlinMultiplatformExtension>().targets
    val enabled = ArrayList<KotlinTarget>()
    val disabled = ArrayList<KotlinTarget>()
    targets.forEach {
        if (it.isCompilationAllowed()) enabled.add(it) else disabled.add(it)
    }
    println()
    println("ENABLED Targets\n----------------")
    enabled.forEach {
        println("+ ${it.name}")
        it.configureCompilations(true)
    }
    println("DISABLED Targets\n----------------")
    disabled.forEach {
        println("- ${it.name}")
        it.configureCompilations(false)
    }
}

fun Project.disablePublicationTasksIfNeeded(publicationsAllowed: List<String> = emptyList()) {
    fun AbstractPublishToMaven.isAllowed(targets: NamedDomainObjectCollection<KotlinTarget>): Boolean {
        val publicationName: String? = publication?.name
        return when {
            publicationName == this@disablePublicationTasksIfNeeded.name -> true
            publicationName == "kotlinMultiplatform" -> true
            publicationName != null -> targets
                .firstOrNull { it.name.startsWith(publicationName) }
                ?.isCompilationAllowed() == true
            else -> targets
                .firstOrNull { name.contains(other = it.name, ignoreCase = true) }
                ?.isCompilationAllowed() == true
        }
    }

    val targets = extensions.getByType<KotlinMultiplatformExtension>().targets
    tasks.withType<AbstractPublishToMaven>().configureEach {
        enabled = isAllowed(targets) && publicationsAllowed.contains(publication.name)
        println("Publication ${if (enabled) "ENABLED" else "DISABLED"}: ${publication.name}")
    }
}

kotlin {
    android()
    ios()
    js(BOTH) {
        browser()
        nodejs()

        listOf(compilations["main"], compilations["test"]).forEach {
            with(it.kotlinOptions) {
                moduleKind = "umd"
                sourceMap = true
                sourceMapEmbedSources = "always"
                metaInfo = true
            }
        }
    }
    jvm()
    linuxArm32Hfp()
    linuxArm64()
    linuxMips32()
    linuxMipsel32()
    linuxX64()
    macosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()

    sourceSets {
        commonMain {
            dependencies {
                implementation(Libs.redux_kotlin)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        android { dependencies { } }
        ios { dependencies { } }
        linuxArm32Hfp { dependencies {  } }
        linuxArm64 { dependencies {  } }
        linuxMips32 { dependencies {  } }
        linuxMipsel32 { dependencies {  } }
        linuxX64 { dependencies {  } }
        js { dependencies {  } }
        jvm { dependencies {  } }
        watchosArm32 { dependencies {  } }
        watchosArm64 { dependencies {  } }
        watchosX86 { dependencies {  } }
    }

    // only compile certain targets on certain platforms
    disableCompilationsIfNeeded()

    // disable publications if we're on the "wrong" platform or if they are not on our list
    val publicationsAllowed = listOf(project.name, "kotlinMultiplatform", "jvm", "iosX64", "iosArm64", "js", "android")
    disablePublicationTasksIfNeeded(publicationsAllowed)
}

android {
    buildToolsVersion = "31.0.0"
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
