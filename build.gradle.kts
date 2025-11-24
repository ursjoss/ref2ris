import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    application
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_21.majorVersion.toInt())
}

application {
    mainClass.set("ch.difty.ref2ris.MainKt")
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kris)
    implementation(libs.clikt)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.simple)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.kluent)
}

tasks {
    val deleteOutFolderTask by registering(Delete::class) {
        delete("out")
    }

    named("clean") {
        dependsOn(deleteOutFolderTask)
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines()
        }
    }

    withType<Detekt> {
        allRules = true
        buildUponDefaultConfig = true
        config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

