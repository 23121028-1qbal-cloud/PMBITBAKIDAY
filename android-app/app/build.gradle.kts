plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.pmbitbakiday"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pmbitbakiday"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Default base URL for Android Emulator pointing to local development backend.
        // For physical device or custom host, ApiConfig allows dynamic override.
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/api/v1/\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            buildConfigField("String", "BASE_URL", "\"https://pmb.itbakiday.ac.id/api/v1/\"")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Live integration tests read credentials from the environment. The Gradle
// daemon can outlive the shell that exported them, so forward the variables
// explicitly. They are only set when the caller provides them; nothing is
// committed and no default value exists.
listOf("PMB_LIVE_TESTS", "PMB_TEST_ADMIN_EMAIL", "PMB_TEST_ADMIN_PASSWORD").forEach { key ->
    providers.environmentVariable(key).orNull?.let { value ->
        tasks.withType<Test>().configureEach { environment(key, value) }
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Networking & Serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Security & Encrypted Preferences
    implementation(libs.androidx.security.crypto)

    // Architecture & Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}