import com.android.build.api.variant.FilterConfiguration
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val versionProps = Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}
val verMajor = versionProps.getProperty("MAJOR").toInt()
val verMinor = versionProps.getProperty("MINOR").toInt()
val verPatch = versionProps.getProperty("PATCH").toInt()
require(verMinor in 0..9) { "MINOR must be 0 to 9" }
require(verPatch in 0..99) { "PATCH must be 0 to 99" }
val baseCode = (verMajor * 10000 + verMinor * 100 + verPatch) * 10

android {
    namespace = "io.github.g_c_3.ardour"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.gc3.ardour"
        minSdk {
            version = release(26)
        }
        targetSdk {
            version = release(36)
        }
        versionCode = baseCode
        versionName = "$verMajor.$verMinor.$verPatch"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
            isUniversalApk = true
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

// D = 1 universal, 2 arm64 (DECISIONS.md 4)
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val abi = output.filters
                .find { it.filterType == FilterConfiguration.FilterType.ABI }
                ?.identifier
            output.versionCode.set(baseCode + if (abi == "arm64-v8a") 2 else 1)
        }
    }
}
