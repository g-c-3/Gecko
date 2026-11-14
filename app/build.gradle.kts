plugins {
    alias(libs.plugins.android.application)
}

// Version comes from the CI run number as -PardourVersion=X.Y.Z (DECISIONS.md 39); 0.0.0 outside CI.
val ardourVersion = providers.gradleProperty("ardourVersion").getOrElse("0.0.0")
val verParts = ardourVersion.split(".").map { it.toInt() }
require(verParts.size == 3) { "ardourVersion must be X.Y.Z" }
val (verMajor, verMinor, verPatch) = verParts
require(verMinor in 0..9) { "MINOR must be 0 to 9" }
require(verPatch in 0..9) { "PATCH must be 0 to 9" }
val baseCode = (verMajor * 10000 + verMinor * 100 + verPatch) * 10

android {
    namespace = "io.github.g_c_3.ardour"
    // Provisional (DECISIONS.md 32): the build shows whether androidx.webkit needs more.
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.gc3.ardour"
        minSdk {
            version = release(29)
        }
        targetSdk {
            version = release(36)
        }
        versionName = "$verMajor.$verMinor.$verPatch"
    }

    // D in versionCode: 1 sideload APK, 0 Play bundle (DECISIONS.md 8, 27).
    flavorDimensions += "store"
    productFlavors {
        create("sideload") {
            dimension = "store"
            versionCode = baseCode + 1
        }
        create("play") {
            dimension = "store"
            versionCode = baseCode
        }
    }

    // Signing material comes from the release job's environment only (DECISIONS.md 6, 13, 15).
    // PKCS12: store and key password are identical. Without KEYSTORE_PATH the release APKs stay unsigned.
    val keystorePath = System.getenv("KEYSTORE_PATH")
    signingConfigs {
        if (keystorePath != null) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.webkit)
}
