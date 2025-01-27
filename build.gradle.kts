plugins {
  kotlin("jvm") version "2.0.0"
  id("idea")
  id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("ch.qos.logback:logback-core:1.4.14")
  implementation("org.slf4j:slf4j-api:2.0.11")
  implementation("com.mysql:mysql-connector-j:8.0.33")
  implementation("com.zaxxer:HikariCP:4.0.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

  runtimeOnly("ch.qos.logback:logback-classic:1.4.14")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

application {
  mainClass.set("org.example.MainKt")
}
