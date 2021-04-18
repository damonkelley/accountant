import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    application
}

group = "com.damonkelley"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.eventstore:db-client-java:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3")
    implementation("com.tylerthrailkill.helpers:pretty-print:v2.0.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    implementation("io.github.serpro69:kotlin-faker:1.6.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("com.natpryce:hamkrest:1.8.0.1")
    testImplementation("io.mockk:mockk:1.11.0")

    val spekVersion = "2.0.15"
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

tasks.test {
    useJUnitPlatform() {
        includeEngines("spek2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"

    kotlinOptions {
        useIR = true
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}

application {
    mainClassName = "MainKt"
}