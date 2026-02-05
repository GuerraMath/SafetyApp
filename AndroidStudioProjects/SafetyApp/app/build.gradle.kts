plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Mantido: Necessário para o Room
}

android {
    namespace = "com.guerramath.safetyapp"
    compileSdk = 35 // Atualizado para 35

    defaultConfig {
        applicationId = "com.guerramath.safetyapp"
        minSdk = 31 // Mantido 31 (segurança para recursos modernos já implementados)
        targetSdk = 35 // Atualizado para 35
        versionCode = 1
        versionName = "0.3.1" // Mantido sua versão

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Mantido: Sua URL da API
        buildConfigField("String", "BASE_URL", "\"https://safety-api-production.up.railway.app/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        buildConfig = true // Mantido true para ler a BASE_URL
    }
}

dependencies {
    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose (UI) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Ícones estendidos (Setas, checkmarks, etc)
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    // --- Navegação ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- ViewModel & Lifecycle ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // --- Networking (Retrofit/OkHttp) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Google OAuth ---
    implementation("com.google.android.gms:play-services-auth:21.1.0")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0") // Atualizado

    // --- Banco de Dados Local (Room) ---
    // Mantido pois seu projeto original depende disso (SafetyDatabase)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- DataStore (Preferências) ---
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // --- Google Sign-In (Credential Manager) ---
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.google.id)
}