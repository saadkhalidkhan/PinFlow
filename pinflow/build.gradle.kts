plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    signing
}

val pinflowGroup = providers.gradleProperty("PINFLOW_GROUP").get()
val pinflowArtifactId = providers.gradleProperty("PINFLOW_ARTIFACT_ID").get()
val pinflowVersion = providers.gradleProperty("PINFLOW_VERSION_NAME").get()

android {
    namespace = "com.pinflow.compose"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    testImplementation(libs.junit)

    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:2.2.0")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.play.services.auth.api.phone)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    val signingRequired = providers.gradleProperty("signing.required")
        .map { it.toBoolean() }
        .getOrElse(true)
    if (signingRequired) {
        signAllPublications()
    }

    coordinates(pinflowGroup, pinflowArtifactId, pinflowVersion)

    pom {
        name.set("PinFlow Compose")
        description.set(
            "Lightweight, animated OTP and PIN input for Jetpack Compose with Material 3, " +
                "smart paste, secure PIN mode, and configurable motion.",
        )
        inceptionYear.set("2026")
        url.set("https://github.com/saadkhalidkhan/PinFlow")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("saadkhalidkhan")
                name.set("Saad Khalid Khan")
                url.set("https://github.com/saadkhalidkhan")
            }
        }
        scm {
            url.set("https://github.com/saadkhalidkhan/PinFlow")
            connection.set("scm:git:git://github.com/saadkhalidkhan/PinFlow.git")
            developerConnection.set("scm:git:ssh://git@github.com/saadkhalidkhan/PinFlow.git")
        }
    }
}

signing {
    val inMemoryKey = providers.gradleProperty("signingInMemoryKey").orNull
    if (!inMemoryKey.isNullOrBlank()) {
        val password = providers.gradleProperty("signingInMemoryKeyPassword").orNull
        useInMemoryPgpKeys(inMemoryKey, password)
    } else {
        useGpgCmd()
    }
}
