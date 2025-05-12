import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.judemaundu.swiftsway2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.judemaundu.swiftsway2"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose.v276)
    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.androidx.compose.bom.v20231001))
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.navigation.compose.v275)
    implementation(libs.accompanist.permissions)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.maps.compose)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.material3)
    implementation(libs.maps.compose)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx.v2200)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.database.ktx.v2003)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.auth.v2103)
    implementation(libs.androidx.core.ktx.v190)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.material3.v121)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.material.icons.extended.v150)
    implementation(libs.coil.compose)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.coil.compose)
    implementation(libs.google.firebase.storage.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.com.google.firebase.firebase.storage.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.location)
    implementation(libs.firebase.auth.ktx.v2110)// Firebase Auth
    implementation(libs.firebase.database.ktx.v2005) // Firebase Database
    implementation(libs.androidx.material3.v120)
    implementation (libs.androidx.ui.text )
    implementation (libs.androidx.material3.v112 )// or newer




}



