plugins {
    kotlin("jvm") version "1.3.61"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.4.3"
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

    implementation("com.gitlab.mvysny.konsume-xml:konsume-xml:0.8")
    implementation("com.beust:klaxon:5.2")

    implementation("no.tornado:tornadofx:1.7.20"){
        exclude("org.jetbrains.kotlin")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testCompile("org.junit.jupiter:junit-jupiter-params:5.5.2")
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

javafx {
    version = "13"
    modules("javafx.base", "javafx.graphics", "javafx.controls")
}

jlink {
    launcher {
        name = "gw-mod-manager"
    }
    addExtraDependencies("javafx")
    imageZip.set(project.file("${project.buildDir}/image-zip/gw-mod-manager.zip"))
}
