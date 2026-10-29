import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sinya.projects.sportsdiary"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sinya.projects.sportsdiary"
        minSdk = 26
        targetSdk = 36
        versionCode = 22
        versionName = "1.6.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources.excludes.add("META-INF/versions/9/previous-compilation-data.bin")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:health"))
    implementation(project(":core:ui"))

    implementation(project(":features:home"))
    implementation(project(":features:menu"))
    implementation(project(":features:statistic"))
    implementation(project(":features:statisticDiagram"))
    implementation(project(":features:settings"))
    implementation(project(":features:trainings"))
    implementation(project(":features:proportions"))
    implementation(project(":features:exercises"))
    implementation(project(":features:routines"))
    implementation(project(":features:pageProportion"))
    implementation(project(":features:pageTraining"))
    implementation(project(":features:pageRoutine"))
    implementation(project(":features:pageExercise"))
    implementation(project(":features:editExercise"))
    implementation(project(":features:backup"))
    implementation(project(":features:calculator"))
    implementation(project(":features:health"))
    implementation(project(":features:notes"))

    implementation(project(":widgets:calendar"))


    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Hilt (сборка единого DI-графа)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    // Навигация и точка входа (MainActivity + NavHost)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Базовый UI и Lifecycle (если переведешь :core:ui на api(), эти 6 строчек тоже можно будет удалить)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.runner)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}