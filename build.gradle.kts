import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion="0.37.3"
val flywayVersion="8.4.3"
val h2Version = "2.1.210"
val hikariCpVersion="5.0.1"
val hopliteVersion="1.4.16"
val koinVersion="3.1.5"
val ktorFlywayVersion="1.2.2"
val ktorVersion="1.6.7"
val kotlinVersion="1.5.0"
val logbackVersion="1.2.10"
val postgresqlVersion="42.3.2"


plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "harke.me"
application {
    mainClass.set("harke.me.api.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("PASSED", "FAILED", "SKIPPED")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "11"
}
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "harke.me.api.ApplicationKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("showcase-api.jar")
    from(configurations.runtimeClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
}

repositories {
    mavenCentral()
}

configurations.all {
    exclude("junit", "junit")
    exclude("org.jetbrains.kotlin", "kotlin-test-junit")
}
kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")

    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinVersion")
    testImplementation("io.mockk:mockk:1.12.3")
}