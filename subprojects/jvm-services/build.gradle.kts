plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":base-services"))
    implementation(project(":core-api"))
    implementation(project(":file-temp"))
    implementation(project(":process-services"))
    implementation(project(":logging"))

    implementation("javax.inject:javax.inject:1")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("org.ow2.asm:asm:9.3")

    implementation("net.rubygrapefruit:native-platform:0.22-milestone-23")

    implementation("org.slf4j:slf4j-api:1.7.36")

}