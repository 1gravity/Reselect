import com.arkivanov.gradle.AndroidConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.arkivanov.gradle.setup")
}

setupAndroidApp {
    AndroidConfig(
        minSdkVersion = 21,
        compileSdkVersion = 31,
        targetSdkVersion = 31
    )
    androidApp(
        applicationId = "org.reduxkotlin.select.demo",
        versionCode = 1,
        versionName = "1.0"
    )
}

dependencies {
    implementation(project(":redux-kotlin-select"))

    implementation(AndroidX.activity.compose)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.animation)
    implementation(AndroidX.compose.ui.tooling)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)

    implementation(AndroidX.navigation.fragment)
    implementation(AndroidX.navigation.ui)

    implementation(KotlinX.coroutines.android)
    implementation("org.reduxkotlin:redux-kotlin:_")
    implementation("org.reduxkotlin:redux-kotlin-thunk:_")
}
