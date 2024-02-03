@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.com.android.application)
  alias(libs.plugins.org.jetbrains.kotlin.android)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.dagger.hilt.android)
  idea
}

android {
  namespace = "com.stevesoltys.aosp_backup"
  compileSdk = libs.versions.targetSdk.get().toInt()

  defaultConfig {
    applicationId = "com.stevesoltys.aosp_backup"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    // Sign with AOSP platform key for development.
    create("aosp") {
      keyAlias = "platform"
      keyPassword = "platform"
      storeFile = file("development/platform.jks")
      storePassword = "platform"
    }
  }

  buildTypes {

    release {
      isMinifyEnabled = false
    }

    getByName("release").signingConfig = signingConfigs.getByName("aosp")
    getByName("debug").signingConfig = signingConfigs.getByName("aosp")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.4"
  }
}

idea {
  module {
    isDownloadSources = true
  }
}

dependencies {
  //noinspection UseTomlInstead
  compileOnly("com.stevesoltys.aosp_backup:aosp:1.0.0")

  // Tink
  implementation(libs.tink.android)

  // Core dependencies
  implementation(libs.material)
  implementation(libs.androidx.core)
  implementation(libs.androidx.preference)
  implementation(libs.result4k)
  implementation(libs.apache.commons.compress)

  // Compose dependencies
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Hilt dependencies
  implementation(libs.hilt)
  kapt(libs.hilt.compiler)

  // Test dependencies
  testImplementation(libs.junit)
  testImplementation(libs.hilt.testing)
  kaptTest(libs.hilt.compiler)
}

hilt {
  enableAggregatingTask = true
}

tasks.withType<JavaCompile> {
  dependsOn(":aosp:publishToMavenLocal")
}

tasks.register<Exec>("provisionEmulator") {
  group = "emulator"

  dependsOn(tasks.getByName("assembleRelease"))

  doFirst {
    commandLine(
      "${project.projectDir}/development/scripts/provision_emulator.sh",
      "aosp-backup",
      "system-images;android-34;default;x86_64"
    )

    environment("ANDROID_HOME", android.sdkDirectory.absolutePath)
    environment("JAVA_HOME", System.getProperty("java.home"))
  }
}

tasks.register<Exec>("startEmulator") {
  group = "emulator"

  doFirst {
    commandLine("${project.projectDir}/development/scripts/start_emulator.sh", "aosp-backup")

    environment("ANDROID_HOME", android.sdkDirectory.absolutePath)
    environment("JAVA_HOME", System.getProperty("java.home"))
  }
}

tasks.register<Exec>("installEmulatorRelease") {
  group = "emulator"

  dependsOn(tasks.getByName("assembleDebug"))

  doFirst {
    commandLine("${project.projectDir}/development/scripts/install_app.sh")

    environment("ANDROID_HOME", android.sdkDirectory.absolutePath)
    environment("JAVA_HOME", System.getProperty("java.home"))
  }
}

val activityTasks = setOf(
  ".ui.screen.settings.SettingsActivity",
  ".ui.screen.initialize.InitializationActivity",
  ".ui.screen.initialize.location.InitializeLocationActivity",
  ".ui.screen.restore.RestoreActivity"
)

activityTasks.forEach {
  val activityName = it.substringAfterLast(".")

  tasks.register<Exec>("start$activityName") {
    group = "emulator"

    dependsOn(tasks.getByName("installEmulatorRelease"))

    doFirst {
      commandLine(
        "${android.sdkDirectory.absolutePath}/platform-tools/adb",
        "shell", "am", "start", "-n", "${android.namespace}/$it"
      )

      environment("ANDROID_HOME", android.sdkDirectory.absolutePath)
      environment("JAVA_HOME", System.getProperty("java.home"))
    }
  }
}

tasks.register<Exec>("clearEmulatorAppData") {
  group = "emulator"

  doFirst {
    commandLine("${project.projectDir}/development/scripts/clear_app_data.sh")

    environment("ANDROID_HOME", android.sdkDirectory.absolutePath)
    environment("JAVA_HOME", System.getProperty("java.home"))
  }
}
