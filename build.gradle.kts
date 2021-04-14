import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

plugins {
    kotlin("jvm") version "1.3.41"
    `java-gradle-plugin`
    `kotlin-dsl`
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

apply {
    plugin("kotlin")
    from("${project.rootDir}/tools/git/git.gradle")
}

group = "vn.com.extremevn.gradle"
version = "1.0.0"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

gradlePlugin {
    plugins {
        register("gradle-dependency-util") {
            id = "vn.com.extremevn.gradle.deputil"
            implementationClass = "vn.com.extremevn.gradle.deputil.GradleDependencyUtilPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("com.squareup.okio:okio:2.10.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.9")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

detekt {
    input = files("src/main/kotlin")
    config = files("${project.rootDir}/tools/kotlin/detekt/default-detekt-config.yml")
    ignoreFailures = false
    parallel = true
    disableDefaultRuleSets = false
    autoCorrect = true
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
