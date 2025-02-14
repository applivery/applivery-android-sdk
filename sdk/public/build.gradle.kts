import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.applivery.publish)
}

sdkPublish {
    artifactId = "applivery-sdk-public"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
