plugins {
    id("com.google.devtools.ksp")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.ithink.dailytodo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ithink.dailytodo"
        minSdk = 29
        targetSdk = 35
        versionCode = 8
        versionName = "1.0.6"

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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.view)
    implementation(libs.compose)
    implementation (libs.mpandroidchart)

    implementation(libs.androidx.core.splashscreen)
    implementation (libs.billing)
    implementation(libs.billing.ktx)

    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.preference.ktx)
    annotationProcessor (libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.room.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("script-runtime"))
}