plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.game.dungeon"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.centelles.dungeon"
        minSdk = 26
        targetSdk = 37
        versionCode = 27
        versionName = "0.3.1"
        testInstrumentationRunner = "com.game.dungeon.HiltTestRunner"
        buildConfigField("long", "INITIAL_GIL", "0L")
        buildConfigField("int", "INITIAL_MAGICITE", "0")
        buildConfigField("int", "INITIAL_MASTERY_LEVEL", "0")
    }

    signingConfigs {
        create("release") {
            storeFile = file("dungeon.jks")
            storePassword = "12345678"
            keyAlias = "dungeon"
            keyPassword = "12345678"
        }
    }

    buildTypes {
        debug {
            buildConfigField("long", "INITIAL_GIL", "1000000000L")
            buildConfigField("int", "INITIAL_MAGICITE", "100000")
            buildConfigField("int", "INITIAL_MASTERY_LEVEL", "20")
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
            manifestPlaceholders["admobAppId"] = "ca-app-pub-9749336798654274~7431348510"
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-9749336798654274/8225555430\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs(file("$projectDir/schemas"))
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Ads & Billing
    implementation(libs.play.services.ads)
    implementation(libs.play.billing.ktx)

    // Gson
    implementation(libs.gson)

    // Accompanist
    implementation(libs.accompanist.systemuicontroller)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}