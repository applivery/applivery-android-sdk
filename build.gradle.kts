// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom("$rootDir/default-detekt-config.yml")

    }
}

val androidCompileSdk by extra { 36 }
val androidMinSdk by extra { 21 }
val androidTargetSdk by extra { 36 }

val libraryGroup by extra { "com.applivery" }
val libraryVersion by extra { "5.0.0-RC1" }

