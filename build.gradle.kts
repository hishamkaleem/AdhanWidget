// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// NOTE: The following dependency must be added in your app module's build.gradle.kts (not here):
// implementation("com.google.android.gms:play-services-location:21.0.1")