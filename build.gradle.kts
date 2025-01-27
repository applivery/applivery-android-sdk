// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.gradle.nexus.publish)
}

allprojects {
    apply(from = "$rootDir/detekt.gradle")
}

apply(from = "${rootDir}/scripts/publish-root.gradle")

val androidCompileSdk by extra { 34 }
val androidMinSdk by extra { 24 }
val androidTargetSdk by extra { 34 }

val PUBLISH_GROUP_ID by extra { "com.applivery" }
val PUBLISH_VERSION by extra { "3.8.5" }

