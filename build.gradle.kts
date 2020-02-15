plugins {
    kotlin("jvm") version "1.3.61"
    application
//    id("org.openjfx.javafxplugin") version "0.0.8"
//    id("org.beryx.jlink") version "2.17.1"
}

group = "com.geowarin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDir = compileKotlin.destinationDir

application {
    mainClassName = "gw.modmanager/com.geowarin.modmanager.MyApp"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
//    implementation(kotlin("reflect"))

    implementation("com.gitlab.mvysny.konsume-xml:konsume-xml:0.9")
    implementation("com.beust:klaxon:5.2")

    implementation("no.tornado:tornadofx:1.7.20"){
        exclude("org.jetbrains.kotlin")
    }
    implementation("org.redundent:kotlin-xml-builder:1.6.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testImplementation("com.google.jimfs:jimfs:1.1")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.21")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//javafx {
//    version = "13"
//    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.web")
//}
//
//jlink {
//    launcher {
//        name = "gw-mod-manager"
//    }
//    addExtraDependencies("javafx")
//    imageZip.set(project.file("${project.buildDir}/image-zip/gw-mod-manager.zip"))
//}
