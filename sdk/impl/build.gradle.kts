import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidMinSdk: Int by rootProject.extra

    val libraryVersion: String by rootProject.extra
    val tenantPlaceholder = "{tenant}"
    val authSchemeSuffix = "applivery.auth"

    namespace = "com.applivery.android.sdk"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "LibraryVersion", "\"$libraryVersion\"")
        buildConfigField("String", "TenantPlaceholder", "\"$tenantPlaceholder\"")
        buildConfigField("String", "AuthSchemeSuffix", "\"$authSchemeSuffix\"")
        buildConfigField(
            "String",
            "ApiBaseUrl",
            "\"https://sdk-api.${tenantPlaceholder}applivery.io/\""
        ) // TODO: this should go to local.properties and file in Github
        buildConfigField(
            "String",
            "DownloadApiUrl",
            "\"https://download-api.${tenantPlaceholder}applivery.io/\""
        ) // TODO: this should go to local.properties and file in Github
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.activity.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.koin.android)
    implementation(libs.lifecycle.process)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.arrow.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.ackpine.ktx)
    implementation(libs.capturable)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

val secretsFile = rootProject.file("local.properties")
val secrets = Properties()

if (secretsFile.exists()) {
    secrets.load(secretsFile.inputStream())
}

publishing {
    val libraryGroup: String by rootProject.extra
    val libraryVersion: String by rootProject.extra

    publications {
        register<MavenPublication>("release") {
            groupId = libraryGroup
            artifactId = "applivery-sdk"
            version = libraryVersion
            pom {
                name = artifactId
                description = "Applivery android sdk"
                url = "https://github.com/applivery/applivery-android-sdk"
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "applivery"
                        name = "Applivery"
                        email = "info@applivery.com"
                    }
                }
                scm {
                    connection = "scm:git@github.com:applivery/applivery-android-sdk.git"
                    developerConnection = "scm:git@github.com:applivery/applivery-android-sdk.git"
                    url = "https://github.com/applivery/applivery-android-sdk"
                }
            }
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            val repoUrl = if (libraryVersion.endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            url = uri(repoUrl)
            credentials {
                username = secrets.getProperty("mavenCentralUsername")
                password = secrets.getProperty("mavenCentralPassword")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        secrets.getProperty("signing.keyId"),
        secrets.getProperty("signing.key"),
        secrets.getProperty("signing.password")
    )

    afterEvaluate {
        sign(publishing.publications)
    }
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
