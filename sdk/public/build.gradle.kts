import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.register<Jar>("createJar") {
    archiveBaseName.set(project.name)
    from(sourceSets.main.get().output) // Include compiled Java class files
    destinationDirectory.set(file("$buildDir/libs"))
}