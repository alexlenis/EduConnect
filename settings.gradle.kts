pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // ⛔ ΑΛΛΑΖΟΥΜΕ αυτό
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // ✅ ΑΠΑΡΑΙΤΗΤΟ για MPAndroidChart
    }
}

rootProject.name = "EduConnect"
include(":app")
