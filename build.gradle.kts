// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("io.gitlab.arturbosch.detekt") version "1.19.0-RC2"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
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

// TODO: delete
val kotlin_version by extra { "1.8.21" }
