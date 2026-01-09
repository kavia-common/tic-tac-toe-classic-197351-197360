pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.android-ecosystem").version("0.1.43")
}

rootProject.name = "example-android-app"

include("app")
include("list")
include("utilities")

defaults {
    androidApplication {
        jdkVersion = 17
        compileSdk = 34
        minSdk = 30

        versionCode = 1
        versionName = "0.1"
        applicationId = "org.gradle.experimental.android.app"

        testing {
            dependencies {
                // Use JUnit 4 for Android unit tests in this project; it is the most compatible with the
                // current Gradle experimental android-ecosystem plugin test runner.
                implementation("junit:junit:4.13.2")
            }
        }
    }

    androidLibrary {
        jdkVersion = 17
        compileSdk = 34
        minSdk = 30

        testing {
            dependencies {
                // Keep libraries consistent with the app test framework (JUnit 4).
                implementation("junit:junit:4.13.2")
            }
        }
    }
}
