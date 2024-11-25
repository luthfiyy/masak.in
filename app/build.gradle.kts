plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.navigation.safe.args)
    id ("kotlin-kapt")
}

android {
    namespace = "com.upi.masakin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.upi.masakin"
        minSdk = 21
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core Android Dependencies
    implementation(libs.androidx.core.ktx)          // Core KTX
    implementation(libs.androidx.appcompat)         // AppCompat
    implementation(libs.material)                  // Material Design Components
    implementation(libs.androidx.constraintlayout) // ConstraintLayout
    implementation(libs.androidx.activity)         // Activity KTX
    implementation(libs.androidx.coordinatorlayout) // CoordinatorLayout

    // Navigation Component
    implementation(libs.androidx.navigation.fragment) // Navigation Fragment
    implementation(libs.androidx.navigation.ui)       // Navigation UI

    // UI Enhancements
    implementation(libs.lottie)  // Lottie for animations
    implementation(libs.glide)   // Glide for image loading

    // JSON Parsing
    implementation(libs.gson) // GSON for JSON parsing

    // Annotation Processing
    annotationProcessor(libs.compiler) // Glide Annotation Processor

    // Room Database
    implementation(libs.room) // Room Runtime
    implementation(libs.ktx)  // Room KTX
    kapt(libs.kapt)           // Room Annotation Processor

    // Testing Dependencies
    testImplementation(libs.junit)                   // Unit testing
    androidTestImplementation(libs.androidx.junit)   // AndroidX JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso
}
