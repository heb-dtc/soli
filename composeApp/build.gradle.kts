import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget()
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        sourceSets.commonMain {
            kotlin.srcDir("build/generated/ksp/metadata")
        }

        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.ui)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.media3.exoplayer)
            implementation(libs.media3.exoplayer.hls)
            implementation(libs.media3.session)
            implementation(libs.media3.common)

            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jna)
            implementation(libs.gstreamer.gst1)
        }
        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.material)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(libs.navigation.compose)
            implementation(libs.viewmodel.compose)

            implementation(libs.kotlinx.datetime)

            implementation(libs.material3.window.size)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
    }
}

android {
    namespace = "net.heb.soli"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "net.heb.soli"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

compose.desktop {
    application {
        mainClass = "net.heb.soli.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Soli"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    //ksp(libs.room.compiler)
    add("kspAndroid",libs.room.compiler)
    add("kspIosSimulatorArm64",libs.room.compiler)
    add("kspIosX64",libs.room.compiler)
    add("kspIosArm64",libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
}

// FIXME cant use the room plugin for now
//room {
//    schemaDirectory("$projectDir/schemas")
//}
ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
}

// Solves implicit dependency issue and IDEs source code detection.
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }