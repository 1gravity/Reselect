plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    buildToolsVersion = "31.0.0"
    compileSdk = 31

    defaultConfig {
        applicationId = "org.reduxkotlin.select.sample"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":redux-kotlin-select"))

    implementation(AndroidX.appCompat)
    implementation(AndroidX.core.ktx)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.uiKtx)
    implementation("org.reduxkotlin:redux-kotlin:_")
    implementation("org.reduxkotlin:redux-kotlin-thunk:_")
    implementation(KotlinX.coroutines.android)

    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
}
