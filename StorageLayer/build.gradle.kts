import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//TODO: investigate having same jdk for all projects
plugins {
    kotlin("jvm")
}

group = "me.cookpal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation(kotlin("script-runtime"))
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}