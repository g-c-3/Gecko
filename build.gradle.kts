buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Newer than the Kotlin built into AGP: the engine's dependencies are built with 2.4 (DECISIONS.md 27).
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}
