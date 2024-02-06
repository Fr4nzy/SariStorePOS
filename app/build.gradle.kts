plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.projectfkklp.saristorepos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.projectfkklp.saristorepos"
        minSdk = 26
        targetSdk = 34
        versionCode = 7
        versionName = "7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core:1.12.0")

    implementation("com.google.code.gson:gson:2.10")

    implementation("com.google.android.material:material:<version>")
    implementation("com.google.android.material:material:x.y.z")
    implementation("com.google.android.material:material:1.11.0")

    implementation("com.github.Marvel999:Android-Loading-Animation:1.0.0")
    implementation("com.github.ISchwarz23:SortableTableView:2.8.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation("com.github.clans:fab:1.6.4")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    testImplementation("junit:junit:4.13.2")

}