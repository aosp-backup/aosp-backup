pluginManagement {
    buildscript {
        repositories {
            mavenCentral()
            maven {
                // https://issuetracker.google.com/issues/227160052#comment37
                // This can be removed when we switch to Android Gradle plugin 8.2.
                setUrl(uri("https://storage.googleapis.com/r8-releases/raw"))
            }
        }
        dependencies {
            classpath("com.android.tools:r8:8.2.28")
        }
    }

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
    }
}

rootProject.name = "aosp-backup"
include(":app")
 