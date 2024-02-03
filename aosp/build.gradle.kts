import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  id("java-library")
  id("maven-publish")
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

val generatedAospSourcesDir = "$projectDir/build/generated/sources/aosp"

val generateAospSources = tasks.register<Copy>("generateAospSources") {
  destinationDir = File(generatedAospSourcesDir)
  from("$projectDir/src/main/aosp")

  duplicatesStrategy = DuplicatesStrategy.FAIL

  // Rename "aidl" files to "java" so sources are attached in Android Studio.
  rename {
    it.replace(".aidl", ".java")
  }
}

val sourceJarTask = tasks.register<Jar>("sourceJar") {
  dependsOn(tasks.named("jar"))
  dependsOn(generateAospSources)

  from(generatedAospSourcesDir)
  archiveClassifier.set("sources")
}

dependencies {
  val aospLibs = fileTree("$projectDir/libs") {
    // out/target/common/obj/JAVA_LIBRARIES/framework-minus-apex_intermediates/classes.jar
    include("android.jar")
    // out/target/common/obj/JAVA_LIBRARIES/core-libart.com.android.art_intermediates/classes.jar
    include("libcore.jar")
  }

  implementation(aospLibs)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("aospSource") {
        groupId = "com.stevesoltys.aosp_backup"
        artifactId = "aosp"
        version = "1.0.0"

        artifact(tasks["shadowJar"]) {
          classifier = ""
        }

        artifact(sourceJarTask) {
          classifier = "sources"
        }
      }
    }
  }
}
