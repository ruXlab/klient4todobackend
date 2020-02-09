import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

apply(plugin = "org.jmailen.kotlinter")

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "vc.rux.klinent4todobackend"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        kotlinOptions {
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


val vArchLivecycle = "2.2.0"
val vJunit5 = "5.6.0"
val coroutinesVersion = "1.2.1"
val vDagger = "2.26"
dependencies {
    implementation(project(":todoclient"))
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$vArchLivecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$vArchLivecycle")
    implementation("androidx.fragment:fragment-ktx:1.2.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${KotlinCompilerVersion.VERSION}")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("org.slf4j:slf4j-android:1.7.30")

    api("com.google.dagger:dagger:$vDagger")
    api("com.google.dagger:dagger-android-support:$vDagger")
    kapt("com.google.dagger:dagger-compiler:$vDagger")
    kapt("com.google.dagger:dagger-android-processor:$vDagger")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.assertj:assertj-core:3.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$vJunit5")
    testImplementation("org.junit.vintage:junit-vintage-engine:$vJunit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$vJunit5")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.slf4j:slf4j-simple:1.7.30")

    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
