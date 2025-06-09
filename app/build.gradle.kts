import java.util.Properties // Bunu dosyanın en üstüne ekle

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
android {
    namespace = "com.example.calorease"
    compileSdk = 35


    buildFeatures {
        buildConfig = true // ✅ Bunu ekle
    }

    defaultConfig {
        applicationId = "com.example.calorease"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPENAI_API_KEY", "\"${localProperties["OPENAI_API_KEY"]}\"")
        println(">> DEBUG OPENAI_API_KEY: ${localProperties["OPENAI_API_KEY"]}")

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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1")) // BoM EKLENDİ

    // Firebase kütüphaneleri artık sürümsüz
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-appcheck")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Diğer bağımlılıkların
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity-ktx:1.2.4")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("com.google.android.gms:play-services-auth:20.0.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.firebase.firestore)
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

