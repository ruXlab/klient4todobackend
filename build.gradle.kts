// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version = "1.3.61"

    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.0-rc02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.0.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
plugins {
    id("org.jmailen.kotlinter") version "2.3.0"
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}

//https://docs.gradle.org/current/userguide/kotlin_dsl.html

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}