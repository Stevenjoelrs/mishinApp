plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mishin.core.domain"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
