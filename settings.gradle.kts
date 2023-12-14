pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven ( url = "https://jitpack.io" )
        google()
        jcenter()
    }
}

rootProject.name = "SariStorePOS"
include(":app")
 