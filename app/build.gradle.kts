plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.7.10"
}

val composeVersion = "1.2.0"
val roomVersion = "2.4.2"
val navVersion = "2.4.2"
val hiltVersion = "2.42"

val permissionXVersion = "1.6.4"
val lottieVersion = "5.2.0"

val activityVersion = "1.5.1"
val material3Version = "1.0.0-beta02"

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "io.github.mumu12641.lark"
        minSdk = 21
        targetSdk = 33
        versionCode = 14
        versionName = "0.8.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
//            "-Xskip-prerelease-check",
//            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
//            "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi",
//            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
//            "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
//            "-Xopt-in=kotlin.RequiresOptIn",
//            "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
//            "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi",
//            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )

    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.appcompat:appcompat:1.6.0-rc01")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("com.afollestad.material-dialogs:color:3.3.0")
    implementation("androidx.activity:activity-compose:$activityVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    implementation("androidx.compose.ui:ui-util:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("com.android.support:palette-v7:33.0.0")
    implementation("io.coil-kt:coil-compose:2.2.1")
    implementation ("com.github.skydoves:landscapist-glide:1.6.1")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")
    implementation("me.onebone:toolbar-compose:2.3.4")

    implementation("com.guolindev.permissionx:permissionx:$permissionXVersion")
    implementation("com.github.getActivity:XXPermissions:16.0")


    implementation("com.tencent:mmkv:1.2.14")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    implementation("androidx.media:media:1.6.0")
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.1")
//    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.1")

    // accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-permissions:0.24.11-rc")
    implementation("com.google.accompanist:accompanist-insets:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-insets-ui:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.25.1")
    implementation("com.google.accompanist:accompanist-pager:0.25.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation("androidx.glance:glance:1.0.0-alpha04")

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

//    implementation("io.github.hokofly:hoko-blur:1.3.7")
//    implementation("com.github.caiyonglong:musicapi:1.1.4")


}