plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.agicp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.agicp"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)

    // Firebase BOM - Asegura compatibilidad entre dependencias
    implementation(platform(libs.firebase.bom.v3223))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")

    // Firebase Database
    implementation(libs.firebase.database.ktx.v2031) // Compatible con Kotlin 1.9.0

    // Firebase Firestore
    implementation(libs.firebase.firestore.ktx.v2491) // Compatible con Kotlin 1.9.0

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google Sign-In
    implementation(libs.play.services.auth.v2070)

    // Kotlin extensions y coroutines para Jetpack Compose
    implementation(libs.androidx.activity.compose.v180)
    implementation(libs.androidx.navigation.compose)

    // Jetpack Compose Core
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Manejo de imágenes con Coil
    implementation(libs.coil.compose)

    // Google Maps y Ubicación
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:2.11.2")
    implementation("com.google.android.gms:play-services-location:21.3.0")


    // Jetpack Lifecycle Runtime para KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.media3.common.ktx)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
