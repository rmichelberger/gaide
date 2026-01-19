import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

val kmpExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
kmpExtension.targets.configureEach {
    // Iterate through all compilations (main, test, debug, release, etc.)
    compilations.configureEach {
        // Generate a task name, for example "generateSecretsIosSimulatorArm64Main"
        val generateSecretsTaskName = buildString {
            append("generateSecrets")
            // Capitalize the first letter of the target name
            append(target.name.replaceFirstChar { it.titlecase() })
            // Capitalize the first letter of the compilation name
            append(name.replaceFirstChar { it.titlecase() })
        }
        // Register a task that generates Secrets.kt
        val generateSecretsTask = tasks.register(generateSecretsTaskName) {
            group = "codegen"
            description = "Generate secrets for ${target.name}:${name}"
            doFirst {
                // 1) Determine which local.properties to use
                val moduleProps = file("local.properties")
                val globalProps = rootProject.file("local.properties")
                val propsFile = when {
                    moduleProps.exists() -> moduleProps
                    globalProps.exists() -> globalProps
                    else -> error("❌ No local.properties found for module: $name")
                }
                if (!propsFile.exists()) {
                    error("local.properties file not found at ${propsFile.absolutePath}")
                }
                // 2) Load values from local.properties
                val properties = Properties().apply {
                    load(propsFile.inputStream())
                }
                // 3) Generate Secrets.kt (by default in commonMain)
                val srcDir = File("$projectDir/src/commonMain/kotlin")
                val secretsPackageDir = File(srcDir, "secrets")
                val secretsFile = File(secretsPackageDir, "Secrets.kt")
                if (!secretsPackageDir.exists()) {
                    secretsPackageDir.mkdirs()
                }
                val content = buildString {
                    appendLine("package secrets")
                    appendLine()
                    appendLine("object Secrets {")
                    properties.forEach { (k, v) ->
                        val key = k.toString()
                        // Check that the key matches the regex for a variable
                        if (Regex("^[a-zA-Z_][a-zA-Z0-9_]*$").matches(key)) {
                            appendLine("    const val $key = \"$v\"")
                        }
                    }
                    appendLine("}")
                }
                secretsFile.writeText(content)
                // 4) Add Secrets.kt to .gitignore
                val gitIgnoreFile = file(".gitignore")
                val relativePath = secretsFile.relativeTo(projectDir).path.replace("\\", "/")
                if (!gitIgnoreFile.exists()) {
                    gitIgnoreFile.writeText("# Auto-generated .gitignore\n$relativePath\n")
                } else {
                    val existing = gitIgnoreFile.readText()
                    if (!existing.contains(relativePath)) {
                        gitIgnoreFile.appendText("\n$relativePath\n")
                    }
                }
            }
        }
        // 5) Link the generated secrets to compileKotlinTask
        this.compileTaskProvider.dependsOn(generateSecretsTask)
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.camerak)
            implementation(libs.textToSpeech)
            implementation(libs.textToSpeech.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "games.thinkin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "games.thinkin"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

