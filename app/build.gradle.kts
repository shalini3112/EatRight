plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt) // if using hilt too
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eatright"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eatright"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // or latest available
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Coil (Image loading)
    implementation(libs.coil.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Hilt (optional if you use it)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // SwipeRefresh
    implementation(libs.accompanist.swiperefresh)

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")

// (Already you should have if using Firebase)
// implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.compose.material:material-icons-extended")


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.incremental", "true")
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.expandProjection", "true")
        arg("room.generateKotlin", "true")
        arg("room.verifySchema", "false") // 💥 DISABLE ROOM VERIFICATION to avoid kapt crash
    }
}
