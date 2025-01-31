import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

// This matches the JDK used to build the project, and is not related to what is running on device.
kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.android.tools.common)
}

gradlePlugin {
    plugins {
        register("PublishConventionPlugin") {
            id = libs.plugins.applivery.publish.get().pluginId
            implementationClass = "PublishConventionPlugin"
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}