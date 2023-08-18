plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

/*compose {
    // Sets a specific JetBrains Compose Compiler version
    kotlinCompilerPlugin.set("1.4.7")
    // Sets a specific Google Compose Compiler version
    // kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.2")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.8.21")
}*/

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(project(":component-toolkit"))
                implementation(compose.material3)
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            }
        }
    }
}

android {
    namespace = "com.pablichj.templato.component.demo"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        applicationId = "com.pablichj.incubator.uistate3.demo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}