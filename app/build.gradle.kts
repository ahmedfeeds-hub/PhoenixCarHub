plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.phoenix.carhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.phoenix.carhub"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // API Keys
        buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCxUT--lTfPC1PHtxhDBVSMpVPGEfb_l-8\"")
        // Weather: Open-Meteo — free, no API key required

        manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyCxUT--lTfPC1PHtxhDBVSMpVPGEfb_l-8"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.animation)
    implementation(libs.compose.runtime)
    debugImplementation(libs.compose.ui.tooling)

    // Activity
    implementation(libs.activity.compose)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.service)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Google Play Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.compose)

    // DataStore
    implementation(libs.datastore.preferences)

    // Media
    implementation(libs.media)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // JSON
    implementation(libs.gson)

    // Coroutines
    implementation(libs.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test)
}

// Allow references to generated code from Hilt
kapt {
    correctErrorTypes = true
}
