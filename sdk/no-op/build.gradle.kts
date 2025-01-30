plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.applivery.android.sdk.publish")
}

sdkPublish {
    artifactId = "applivery-sdk-no-op"
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidMinSdk: Int by rootProject.extra

    namespace = "com.applivery.android.sdk"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

tasks.register<Copy>("copyJars") {
    dependsOn(":sdk:public:createJar") // Ensure JARs are built first
    println("Trying to get the JAR from ${"../sdk/public/build/libs/${project(":sdk:public").name}.jar"}")
    from("${rootProject.rootDir}/sdk/public/build/libs/${project(":sdk:public").name}.jar")
    into("libs")
}

tasks.named("preBuild").configure {
    dependsOn("copyJars")
}