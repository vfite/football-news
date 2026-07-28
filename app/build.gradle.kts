import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.vfite.football"
    compileSdk = 36

    android.buildFeatures.buildConfig = true
    defaultConfig {
        applicationId = "com.vfite.football"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiBaseUrl: String = providers.gradleProperty("API_URL")
            .getOrElse("https://football.com/")
        val newsUrl = providers.gradleProperty("NEWS_URL")
            .getOrElse("https://football.com/news/%s/%d")
        buildConfigField("String", "NEWS_URL", "\"$newsUrl\"")
        buildConfigField("String", "API_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        compileOptions {
            jvmTarget = JvmTarget.JVM_11.target
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)

    implementation(libs.kodein)
    implementation(libs.kodein.android)
    implementation(libs.kodein.conf)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.glide)

    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)

    implementation(libs.work.runtime.ktx)

    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}