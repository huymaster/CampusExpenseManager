plugins {
    idea
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

apply { plugin("org.jetbrains.kotlin.kapt") }
apply { plugin("realm-android") }

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

android {
    namespace = "com.github.huymaster.campusexpensemanager"
    compileSdk = 36
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.github.huymaster.campusexpensemanager"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    jvmToolchain(21)
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.splashscreen)
    implementation(libs.cardview)
    implementation(libs.gridlayout)
    implementation(libs.recyclerview)
    implementation(libs.recyclerViewDecoration)
    implementation(libs.fragment)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.compose.runtime)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.donut)
    implementation(libs.mpchart)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.sqlite)
    implementation(libs.realm)
    implementation(libs.guava)
    implementation(libs.hilt)
    implementation(libs.dagger.android.support)
    implementation(libs.work.runtime)
    implementation(libs.core.ktx)

    runtimeOnly(libs.compose.bom)

    ksp(libs.hilt.compiler)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.testing)
    androidTestAnnotationProcessor(libs.hilt.compiler)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.ktorm.core)
    testImplementation(libs.mockito)
    testImplementation(libs.powermock)
    testImplementation(libs.powermock.junit4)
    testImplementation(libs.powermock.junit4.rule)
    testImplementation(libs.powermock.xstream)
    testImplementation(libs.hilt.testing)

    testAnnotationProcessor(libs.hilt.compiler)
}