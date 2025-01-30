import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

private val extensionPropertyMissingError: (String) -> String = {
    " No $it was provided for SdkPublishExtension. Provide one using sdkPublish DSL"
}
private val artifactIdMissingError = extensionPropertyMissingError("artifactId")
private val publicModuleNameMissingError = extensionPropertyMissingError("publicModuleName")

private val artifactConfiguration: Project.(Secrets, String) -> ArtifactConfiguration =
    { secrets, artifactId ->
        val libraryGroup: String by rootProject.extra
        val libraryVersion: String by rootProject.extra
        val pomConfiguration = pomConfiguration(secrets)
        ArtifactConfiguration(
            artifactId = artifactId,
            groupId = libraryGroup,
            version = libraryVersion,
            pom = pomConfiguration
        )
    }

private val pomConfiguration: Project.(Secrets) -> PomConfiguration = {
    PomConfiguration(
        name = "Applivery Android SDK",
        description = "Applivery Android SDK",
        url = "https://github.com/applivery/applivery-android-sdk",
        license = LicenseConfiguration(
            name = "The Apache Software License, Version 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        ),
        developer = DeveloperConfiguration(
            id = "applivery",
            name = "Applivery",
            email = "info@applivery.com"
        ),
        scm = ScmConfiguration(
            connection = "scm:git@github.com:applivery/applivery-android-sdk.git",
            developerConnection = "scm:git@github.com:applivery/applivery-android-sdk.git",
            url = "https://github.com/applivery/applivery-android-sdk"
        )
    )
}

private val repositoryConfiguration: Project.(Secrets) -> RepositoryConfiguration = { secrets ->
    val libraryVersion: String by rootProject.extra
    val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
    val repoUrl = if (libraryVersion.endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
    RepositoryConfiguration(
        name = "sonatype",
        url = repoUrl,
        credentials = RepositoryCredentials(
            username = secrets.mavenCentralUsername,
            password = secrets.mavenCentralPassword
        )
    )
}

interface SdkPublishExtension {
    var artifactId: String?
    var publicModuleName: String?
}

class PublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
            }
            val options = extensions.create("sdkPublish", SdkPublishExtension::class)
            val secrets = Secrets.fromPropertiesFile("local.properties")
            afterEvaluate {
                val artifactId = options.artifactId ?: error(artifactIdMissingError)
                val publicModule = options.publicModuleName ?: error(publicModuleNameMissingError)
                extensions.configure<PublishingExtension> {
                    configureWith(
                        artifact = artifactConfiguration(secrets, artifactId),
                        repository = repositoryConfiguration(secrets),
                    )
                }

                extensions.configure<SigningExtension> {
                    val publishing = extensions.getByType<PublishingExtension>()
                    useInMemoryPgpKeys(
                        secrets.signingKeyId,
                        secrets.signingKey,
                        secrets.signingPassword
                    )
                    sign(publishing.publications)
                }

                tasks.register<Copy>("copyJars") {
                    val publicModuleDir = publicModule.replace(":", "/")
                    dependsOn("$publicModule:createJar")
                    from("${rootProject.rootDir}$publicModuleDir/build/libs/${project(publicModule).name}.jar")
                    into("libs")
                }

                tasks.named("preBuild").configure {
                    dependsOn("copyJars")
                }
            }
        }
    }

    context (Project, PublishingExtension)
    private fun PublishingExtension.configureWith(
        artifact: ArtifactConfiguration,
        repository: RepositoryConfiguration
    ) {
        publications {
            register<MavenPublication>("release") {
                groupId = artifact.groupId
                artifactId = artifact.artifactId
                version = artifact.version
                pom {
                    name = artifact.pom.name
                    description = artifact.pom.description
                    url = artifact.pom.url
                    licenses {
                        license {
                            name = artifact.pom.license.name
                            url = artifact.pom.license.url
                        }
                    }
                    developers {
                        developer {
                            id = artifact.pom.developer.id
                            name = artifact.pom.developer.name
                            email = artifact.pom.developer.email
                        }
                    }
                    scm {
                        connection = artifact.pom.scm.connection
                        developerConnection = artifact.pom.scm.developerConnection
                        url = artifact.pom.scm.url
                    }
                }
                from(components["release"])
            }
        }
        repositories {
            maven {
                name = repository.name
                url = uri(repository.url)
                credentials {
                    username = repository.credentials.username
                    password = repository.credentials.password
                }
            }
        }
    }
}

private data class Secrets(
    val mavenCentralUsername: String,
    val mavenCentralPassword: String,
    val signingKeyId: String,
    val signingKey: String,
    val signingPassword: String
) {

    companion object {
        context (Project)
        fun fromPropertiesFile(name: String): Secrets {
            val secretsFile = rootProject.file(name)
            val secrets = Properties()
            if (secretsFile.exists()) {
                secrets.load(secretsFile.inputStream())
            }
            return Secrets(
                mavenCentralUsername = secrets.getProperty("mavenCentralUsername"),
                mavenCentralPassword = secrets.getProperty("mavenCentralPassword"),
                signingKeyId = secrets.getProperty("signing.keyId"),
                signingKey = secrets.getProperty("signing.key"),
                signingPassword = secrets.getProperty("signing.password")
            )
        }
    }
}

private data class ArtifactConfiguration(
    val artifactId: String,
    val groupId: String,
    val version: String,
    val pom: PomConfiguration
)

private data class PomConfiguration(
    val name: String,
    val description: String,
    val url: String,
    val license: LicenseConfiguration,
    val developer: DeveloperConfiguration,
    val scm: ScmConfiguration
)

data class LicenseConfiguration(
    val name: String,
    val url: String
)

data class DeveloperConfiguration(
    val id: String,
    val name: String,
    val email: String
)

data class ScmConfiguration(
    val connection: String,
    val developerConnection: String,
    val url: String
)

private data class RepositoryConfiguration(
    val name: String,
    val url: String,
    val credentials: RepositoryCredentials
)

private data class RepositoryCredentials(
    val username: String,
    val password: String
)