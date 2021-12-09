plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets()
    publications()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("org.reduxkotlin:redux-kotlin:_")
            }
        }

        android {
            publishLibraryVariants("release")
        }
        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}
