plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidMinSdk: Int by rootProject.extra
    val PUBLISH_VERSION: String by rootProject.extra
    val tenantPlaceholder = "{tenant}"
    val authSchemeSuffix = "applivery.auth"

    namespace = "com.applivery.android.sdk"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "LibraryVersion", "\"$PUBLISH_VERSION\"")
        buildConfigField("String", "TenantPlaceholder", "\"$tenantPlaceholder\"")
        buildConfigField("String", "AuthSchemeSuffix", "\"$authSchemeSuffix\"")
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

    flavorDimensions += "default"
    productFlavors {
        create("sandbox") {
            dimension = "default"
            buildConfigField(
                "String",
                "ApiBaseUrl",
                "\"https://sdk-api.s.${tenantPlaceholder}applivery.dev/\""
            )
            buildConfigField(
                "String",
                "DownloadApiUrl",
                "\"https://download-api.s.${tenantPlaceholder}applivery.dev/\""
            )
        }
        create("live") {
            dimension = "default"
            buildConfigField(
                "String",
                "ApiBaseUrl",
                "\"https://sdk-api.${tenantPlaceholder}applivery.io/\""
            )
            buildConfigField(
                "String",
                "DownloadApiUrl",
                "\"https://download-api.${tenantPlaceholder}applivery.io/\""
            )
        }
    }

    sourceSets {
        getByName("debug") {
            java.srcDir("src/debug/java")
        }
        getByName("release") {
            java.srcDir("src/release/java")
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
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.activity)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.koin.android)
    implementation(libs.lifecycle.process)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.arrow.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}