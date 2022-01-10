import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    application
}

group = "dev.ajthom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.17.0")
    implementation("com.github.epadronu:balin:0.4.2")
    implementation("org.apache.poi:poi-ooxml:5.1.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("cdc-data-puller")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("dev.ajthom.covid.cdc.MainKt")
}
