plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidMinSdk: Int by rootProject.extra
    val PUBLISH_VERSION: String by rootProject.extra
    val tenantPlaceholder = "{tenant}"

    namespace = "com.applivery.android.sdk"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "LibraryVersion", "\"$PUBLISH_VERSION\"")
        buildConfigField("String", "TenantPlaceholder", "\"$tenantPlaceholder\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
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