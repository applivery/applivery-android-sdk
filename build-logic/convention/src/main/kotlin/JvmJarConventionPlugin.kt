import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

class JvmJarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<JavaPluginExtension> {
                tasks.register<Jar>("createJar") {
                    archiveBaseName.set(project.name)
                    from(sourceSets.getByName("main").output)
                    destinationDirectory.set(file("$buildDir/libs"))
                }
            }
        }
    }

}