import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("java-library")
    id("kotlin")
}

val vJackson = "2.10.2"
val vJunit5 = "5.6.0"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${KotlinCompilerVersion.VERSION}")
    implementation("com.squareup.retrofit2:retrofit:2.7.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${vJackson}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${vJackson}")
    implementation("org.yaml:snakeyaml:1.25")

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${vJunit5}")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:${vJunit5}")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}
