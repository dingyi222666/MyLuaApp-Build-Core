plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(project(":build-cache-base"))
    api(project(":snapshots"))

    implementation(project(":base-services"))
    implementation(project(":core-api"))
    implementation(project(":files"))
    implementation(project(":file-temp"))
    implementation(project(":native"))
    implementation(project(":persistent-cache"))
    implementation(project(":resources"))
    implementation(project(":logging"))

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

}