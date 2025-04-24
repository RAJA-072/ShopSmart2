plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}

buildscript {
    dependencies {
        // Google Services classpath for Firebase functionality
        classpath("com.google.gms:google-services:4.3.15") // Ensure this is the latest version
    }
}

allprojects {
    // Don't define repositories here since they're managed at the settings level
}
