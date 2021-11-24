plugins {
    kotlin("multiplatform")
    id("com.android.library")
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
    mingwX64()
    mingwX86()
    tvosArm64()
    tvosX64()
    wasm32()
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
        iosArm32 { dependencies {  } }
        iosArm64 { dependencies {  } }
        iosX64 { dependencies {  } }
        linuxArm32Hfp { dependencies {  } }
        linuxArm64 { dependencies {  } }
        linuxMips32 { dependencies {  } }
        linuxMipsel32 { dependencies {  } }
        linuxX64 { dependencies {  } }
        js { dependencies {  } }
        jvm { dependencies {  } }
        mingwX64 { dependencies {  } }
        mingwX86 { dependencies {  } }
        tvosArm64 { dependencies {  } }
        tvosX64 { dependencies {  } }
        wasm32 { dependencies {  } }
        watchosArm32 { dependencies {  } }
        watchosArm64 { dependencies {  } }
        watchosX86 { dependencies {  } }
    }
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

afterEvaluate {
    tasks {
        // Alias the task names we use elsewhere to the new task names.
        create("installMP").dependsOn("publishKotlinMultiplatformPublicationToMavenLocal")
        create("installLocally") {
            dependsOn("publishKotlinMultiplatformPublicationToTestRepository")
            dependsOn("publishJvmPublicationToTestRepository")
            dependsOn("publishJsPublicationToTestRepository")
            dependsOn("publishMetadataPublicationToTestRepository")
        }
        create("installIosLocally") {
            dependsOn("publishKotlinMultiplatformPublicationToTestRepository")
            dependsOn("publishIosArm32PublicationToTestRepository")
            dependsOn("publishIosArm64PublicationToTestRepository")
            dependsOn("publishIosX64PublicationToTestRepository")
            dependsOn("publishMetadataPublicationToTestRepository")
        }
        // NOTE: We do not alias uploadArchives because CI runs it on Linux and we only want to run it on Mac OS.
        // tasks.create("uploadArchives").dependsOn("publishKotlinMultiplatformPublicationToMavenRepository")
    }
}

//apply(from = rootProject.file("gradle/publish.gradle"))
