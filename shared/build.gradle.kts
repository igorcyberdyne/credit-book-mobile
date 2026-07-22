import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("app.cash.sqldelight") version "2.0.2"
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    androidLibrary {
        namespace = "org.creditbook.project.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation("io.ktor:ktor-client-okhttp:2.3.12")
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            implementation("io.insert-koin:koin-android:4.2.2")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta02")
            implementation("com.russhwolf:multiplatform-settings:1.2.0")
            implementation("io.ktor:ktor-client-auth:2.3.12")
            implementation("com.benasher44:uuid:0.8.4")

            implementation("io.insert-koin:koin-core:4.2.2")
            implementation("io.insert-koin:koin-compose:4.2.2")
            implementation("io.insert-koin:koin-compose-viewmodel:4.2.2")
            implementation("io.ktor:ktor-client-logging:2.3.12")
            implementation("cafe.adriel.voyager:voyager-core:1.1.0-beta02")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.12")
            implementation("app.cash.sqldelight:native-driver:2.0.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("org.creditbook.project.shared.db")
        }
    }
}