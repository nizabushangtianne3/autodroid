plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "dev.niza.autodroid"
    compileSdk = 36

    val releaseStoreFile = System.getenv("AUTODROID_RELEASE_STORE_FILE")
    val releaseStorePassword = System.getenv("AUTODROID_RELEASE_STORE_PASSWORD")
    val releaseKeyAlias = System.getenv("AUTODROID_RELEASE_KEY_ALIAS")
    val releaseKeyPassword = System.getenv("AUTODROID_RELEASE_KEY_PASSWORD")
    if (
        releaseStoreFile != null &&
        releaseStorePassword != null &&
        releaseKeyAlias != null &&
        releaseKeyPassword != null
    ) {
        signingConfigs {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    defaultConfig {
        applicationId = "dev.niza.autodroid"
        minSdk = 24
        targetSdk = 36
        versionCode = 10
        versionName = "0.1.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = if (releaseStoreFile != null) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":engine"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.test:core:1.6.1")
}

android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
