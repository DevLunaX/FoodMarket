plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")

    // === NUEVOS PLUGINS PARA MVVM Y HILT ===
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "mx.edu.utng.foodmarket"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utng.foodmarket"
        minSdk = 24
        targetSdk = 36
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

    // === CAMBIAMOS A JAVA 17 (Estándar moderno) ===
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // === LIBRERÍAS BASE (Venían en tu proyecto nuevo) ===
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // === LIBRERÍAS DE FIREBASE (Migradas de tu proyecto viejo) ===
    // Usamos el BOM para asegurar que las versiones sean compatibles
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // === LIBRERÍAS EXTRAS (Migradas de tu proyecto viejo) ===
    implementation("io.coil-kt:coil-compose:2.5.0") // Para cargar fotos
    implementation("androidx.compose.material:material-icons-extended") // Iconos extra
    implementation("com.google.maps.android:maps-compose:6.1.0") // Mapas
    implementation("com.google.android.gms:play-services-location:21.0.1") // GPS

    // === LIBRERÍAS DE ARQUITECTURA (NUEVAS - ESENCIALES PARA MVVM) ===

    // 1. Navigation Compose (Para movernos entre pantallas pro)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 2. ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 3. Hilt (Inyección de Dependencias - OBLIGATORIO para lo que vamos a hacer)
    implementation("com.google.dagger:hilt-android:2.50")
    implementation(libs.androidx.compose.foundation)
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")


    // === TESTING ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}