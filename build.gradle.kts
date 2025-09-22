// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.detekt)
}

allprojects {
    apply(from = "$rootDir/detekt.gradle")
}

val androidCompileSdk by extra { 35 }
val androidMinSdk by extra { 21 }
val androidTargetSdk by extra { 35 }

val libraryGroup by extra { "com.applivery" }
val libraryVersion by extra { "4.7.0" }

