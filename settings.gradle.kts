import org.gradle.internal.impldep.org.jsoup.safety.Safelist.basic

pluginManagement {
    repositories {
        google()
        mavenCentral()

        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication{

            }
            credentials{
                username = "mapbox"
                password = "sk.eyJ1IjoiYWhlZGJsIiwiYSI6ImNtNWgxajZpZDBkY3Aya3NnM3hiMjJlbjUifQ.eLKFEWnqEMmjRTGu20dIYw"
            }
        }

    }
}

rootProject.name = "VikVakt"
include(":app")
 