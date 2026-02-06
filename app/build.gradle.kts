plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.uitm.safecampus"
    compileSdk = 36  // FIXED: Used the Number 36 (required by your libraries)

    defaultConfig {
        applicationId = "com.uitm.safecampus"
        minSdk = 24
        targetSdk = 36   // FIXED: Used the Number 36 (matches your Emulator API 36.1)
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    // ADD THIS LINE for Realtime Database
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- ASSIGNMENT REQUIREMENTS ---

    // 1. Google Maps & Location
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // 2. Material Design 3
    implementation("com.google.android.material:material:1.11.0")

    // 3. Retrofit & Gson (Server Communication)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 4. CardView
    implementation("androidx.cardview:cardview:1.0.0")
}