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
        applicationId = "io.github.g_c_3.ardour"
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
