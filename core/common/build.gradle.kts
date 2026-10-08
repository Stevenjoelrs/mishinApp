plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mishin.core.common"
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
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    api(libs.uuid)
}
