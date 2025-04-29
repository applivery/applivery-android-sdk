plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.applivery.publish)
    id("kotlin-parcelize")
}

sdkPublish {
    artifactId = "applivery-sdk"
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidMinSdk: Int by rootProject.extra

    val libraryVersion: String by rootProject.extra
    val authSchemeSuffix = "applivery.auth"

    namespace = "com.applivery.android.sdk"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "LibraryVersion", "\"$libraryVersion\"")
        buildConfigField("String", "AuthSchemeSuffix", "\"$authSchemeSuffix\"")
        buildConfigField("String", "CustomDomain", "\"applivery.io\"")
        buildConfigField("String", "ApiBaseUrl", "\"https://sdk-api\"")
        buildConfigField("String", "DownloadApiUrl", "\"https://download-api\"")
        manifestPlaceholders["authSchemeSuffix"] = authSchemeSuffix
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    api(project(":sdk:public"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.activity.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.koin.android)
    implementation(libs.lifecycle.process)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.arrow.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.ackpine.ktx)
    implementation(libs.capturable)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
