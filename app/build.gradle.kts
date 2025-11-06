import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

val localProps = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) {
    localFile.inputStream().use { localProps.load(it) }
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "STRIPE_PUBLISHABLE_KEY",
            "\"${localProps.getProperty("STRIPE_PUBLISHABLE_KEY", "")}\""
        )
        buildConfigField(
            "String",
            "STRIPE_SECRET_KEY",
            "\"${localProps.getProperty("STRIPE_SECRET_KEY", "")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Google Sign-In
    implementation(libs.play.services.auth)
    implementation(libs.play.services.identity)

    // Stripe
    implementation(libs.stripe.android)

    // Retrofit
    implementation(libs.okhttp)

    // SweetAlert
    implementation(libs.library)

    // Gson
    implementation(libs.gson)

    // Cardview
    implementation(libs.androidx.cardview)

    // Material
    implementation(libs.material.v190)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Glide
    implementation(libs.glide)
    ksp(libs.glideCompiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
