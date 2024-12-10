plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    // **Core Android Dependencies**
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.coordinatorlayout)

    // **UI and Animation**
    implementation(libs.lottie) // Lottie animations
    implementation(libs.glide) // Glide for image loading
    annotationProcessor(libs.compiler) // Glide annotation processor

    // **Navigation Component**
    implementation(libs.androidx.navigation.fragment) // Navigation fragment
    implementation(libs.androidx.navigation.ui) // Navigation UI

    // **Database and Persistence**
    implementation(libs.room) // Room runtime
    implementation(libs.ktx) // Room Kotlin Extensions
    ksp(libs.ksp) // Room compiler (KSP)

    // **Dependency Injection**
    implementation(libs.hilt) // Hilt runtime
    ksp(libs.hilt.compiler) // Hilt compiler (KSP)

    // **API**
    implementation(libs.retrofit) // Retrofit library
    implementation(libs.retrofit2.converter.gson) // Gson converter for Retrofit

    // **Parsing**
    implementation(libs.gson) // JSON parsing

    // **Multimedia**
    implementation(libs.youtube.player) // YouTube Player library (assuming 'libs.core' is for YouTube Player)
    implementation(libs.androidx.viewpager2) // ViewPager2 for displaying content

    // **Testing**
    testImplementation(libs.junit) // Unit testing with JUnit
    androidTestImplementation(libs.androidx.junit) // Android-specific JUnit extensions
    androidTestImplementation(libs.androidx.espresso.core) // UI testing with Espresso
}
