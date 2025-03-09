plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.safe.args)
}

android {
    namespace = "com.example.foodplannerapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodplannerapplication"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.lottie)

    // for google authentication
    implementation(libs.firebase.ui.auth)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    // Network Image Viewer
    implementation(libs.glide)
    // Youtube Player
    implementation(libs.android.youtube.player)

    // Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.room.testing)
    kapt(libs.room.compiler)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // lifecycle
    implementation(libs.lifecycle.viewmodel)

    // facebook login
    implementation(libs.facebook.login)
}