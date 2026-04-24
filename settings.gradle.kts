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
        // Google Navigation SDK repository
        maven {
            url = uri("https://storage.googleapis.com/android_repo/")
        }
    }
}

rootProject.name = "PhoenixCarHub"
include(":app")
