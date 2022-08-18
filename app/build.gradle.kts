
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
}

val composeVersion = "1.2.0-beta03"
val roomVersion = "2.4.2"
val navVersion = "2.4.2"
val hiltVersion ="2.42"

val permissionXVersion = "1.6.4"
val lottieVersion = "5.2.0"

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "io.github.mumu12641.lark"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "0.1.0-alpha"

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

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.compose.ui:ui:$composeVersion")
    implementation ("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation ("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation ("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")
    implementation ("com.afollestad.material-dialogs:color:3.3.0")
    implementation ("androidx.activity:activity-compose:1.5.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$composeVersion")
    implementation ("androidx.compose.ui:ui-util:$composeVersion")
    debugImplementation ("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation( "androidx.compose.ui:ui-test-manifest:$composeVersion")
    implementation ("androidx.room:room-runtime:$roomVersion")
    annotationProcessor ("androidx.room:room-compiler:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    kapt ("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("io.coil-kt:coil-compose:2.2.0")
    implementation("com.github.skydoves:landscapist-glide:1.5.3")
    implementation("me.onebone:toolbar-compose:2.3.4")

    implementation("com.guolindev.permissionx:permissionx:$permissionXVersion")
    implementation("com.github.getActivity:XXPermissions:15.0")


    implementation("com.tencent:mmkv:1.2.13")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    implementation ("androidx.media:media:1.6.0")
    implementation ("com.google.android.exoplayer:exoplayer-core:2.18.1")
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.18.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.18.1")

    // accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.13-rc")
    implementation("com.google.accompanist:accompanist-permissions:0.24.0-alpha")
    implementation ("com.google.accompanist:accompanist-insets:0.24.13-rc")
    implementation ("com.google.accompanist:accompanist-insets-ui:0.24.13-rc")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.25.1")
    implementation ("com.google.accompanist:accompanist-pager:0.25.1")

    implementation ("com.android.support:palette-v7:32.0.0")


    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

}