plugins {
    kotlin("jvm") version "1.3.61"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
//    implementation(kotlin("reflect"))

    implementation("com.gitlab.mvysny.konsume-xml:konsume-xml:0.8")
    implementation("com.beust:klaxon:5.2")
    implementation("no.tornado:tornadofx:1.7.20")

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
    modules("javafx.controls", "javafx.graphics")
}
