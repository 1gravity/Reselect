import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import java.net.URI

plugins {
    kotlin("multiplatform")
    id("com.android.library")

    id("maven-publish")
    id("signing")
}

val props: MutableMap<String, *> = project.properties
group = props["POM_GROUP_ID"].toString()
// = props["POM_ARTIFACT_ID"].toString()
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
    if ((name == KotlinMultiplatformPlugin.METADATA_TARGET_NAME)) {
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
            publicationName == "kotlinMultiplatform" -> true
            publicationName != null -> {
                val target = targets.find { it.name.startsWith(publicationName) }
                target?.isCompilationAllowed() == true
            }
            else -> {
                val target = targets.find { name.contains(other = it.name, ignoreCase = true) }
                target?.isCompilationAllowed() == true
            }
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

    // must come before the publishing call
    disableCompilationsIfNeeded()

    publishing {
        fun Project.getRepositoryUrl(): URI {
            val isReleaseBuild = properties["POM_VERSION_NAME"]?.toString()?.contains("SNAPSHOT") == false
            val releaseRepoUrl = properties["RELEASE_REPOSITORY_URL"]?.toString() ?: "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotRepoUrl = properties["SNAPSHOT_REPOSITORY_URL"]?.toString() ?: "https://oss.sonatype.org/content/repositories/snapshots/"
            return uri(if (isReleaseBuild) releaseRepoUrl else snapshotRepoUrl)
        }

        publications {
            val props = project.properties

            // 1. configure repositories
            repositories {
                maven {
                    url = getRepositoryUrl()
                    // credentials are stored in ~/.gradle/gradle.properties with ~ being the path of the home directory
                    credentials {
                        username = props["ossUsername"]?.toString() ?: throw IllegalStateException("ossUsername not found")
                        password = props["ossPassword"]?.toString() ?: throw IllegalStateException("ossPassword not found")
                    }
                }
            }

            // 2. configure publication
            val publicationName = props["POM_NAME"]?.toString() ?: "publication"
            create<MavenPublication>(publicationName) {
                pom {
                    groupId = props["POM_GROUP_ID"].toString()
                    artifactId = props["POM_ARTIFACT_ID"].toString()
                    version = props["POM_VERSION_NAME"].toString()

                    name.set(props["POM_NAME"].toString())
                    description.set(props["POM_DESCRIPTION"].toString())
                    url.set(props["POM_URL"].toString())
                    packaging = props["POM_PACKAGING"].toString()

                    scm {
                        url.set(props["POM_SCM_URL"].toString())
                        connection.set(props["POM_SCM_CONNECTION"].toString())
                        developerConnection.set(props["POM_SCM_DEV_CONNECTION"].toString())
                    }

                    organization {
                        name.set(props["POM_COMPANY_NAME"].toString())
                        url.set(props["POM_COMPANY_URL"].toString())
                    }

                    developers {
                        developer {
                            id.set(props["POM_DEVELOPER_ID"].toString())
                            name.set(props["POM_DEVELOPER_NAME"].toString())
                            email.set(props["POM_DEVELOPER_EMAIL"].toString())
                        }
                    }

                    licenses {
                        license {
                            name.set(props["POM_LICENCE_NAME"].toString())
                            url.set(props["POM_LICENCE_URL"].toString())
                            distribution.set(props["POM_LICENCE_DIST"].toString())
                        }
                    }
                }
            }

            // 3. sign the artifacts
            signing {
                val signingKeyId = props["signingKeyId"]?.toString()
                    ?: throw IllegalStateException("signingKeyId not found")
                val signingKeyPassword = props["signingKeyPassword"]?.toString()
                    ?: throw IllegalStateException("signingKeyPassword not found")
                val signingKey = props["signingKey"]?.toString()
                    ?: throw IllegalStateException("signingKey not found")
                useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassword)
                sign(publishing.publications.getByName(publicationName))
            }
        }
    }

    // must come after the publishing call
    val publicationsAllowed = listOf("kotlinMultiplatform", "jvm", "ios", "js")
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
