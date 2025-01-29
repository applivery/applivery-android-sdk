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
        manifestPlaceholders["authSchemeSuffix"] = authSchemeSuffix
    }

    buildTypes {
        debug {
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
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    publishing {
        multipleVariants {
            withSourcesJar()
        }
    }
}

dependencies {

    debugImplementation(libs.androidx.core)
    debugImplementation(libs.androidx.startup)
    debugImplementation(libs.androidx.browser)
    debugImplementation(libs.androidx.activity.core)
    debugImplementation(libs.androidx.activity.compose)
    debugImplementation(libs.lifecycle.viewmodel)
    debugImplementation(libs.koin.android)
    debugImplementation(libs.lifecycle.process)
    debugImplementation(libs.retrofit.core)
    debugImplementation(libs.retrofit.converter.gson)
    debugImplementation(libs.okhttp.logging)
    debugImplementation(libs.gson)
    debugImplementation(libs.arrow.core)
    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.graphics)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.material3)
    debugImplementation(libs.ackpine.ktx)
    debugImplementation(libs.capturable)
    releaseCompileOnly(platform(libs.androidx.compose.bom))
    releaseCompileOnly(libs.androidx.compose.ui)
    testReleaseCompileOnly(platform(libs.androidx.compose.bom))
    testReleaseCompileOnly(libs.androidx.compose.ui)
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
        register<MavenPublication>("debug") {
            groupId = libraryGroup
            artifactId = "applivery-sdk"
            version = libraryVersion
            pom(artifactId)

            afterEvaluate {
                from(components["debug"])
            }
        }
        register<MavenPublication>("release") {
            groupId = libraryGroup
            artifactId = "applivery-sdk-no-op"
            version = libraryVersion
            pom(artifactId)

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
        sign(publishing.publications["release"])
        sign(publishing.publications["debug"])
    }
}

fun MavenPublication.pom(artifactId: String) {
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
}
