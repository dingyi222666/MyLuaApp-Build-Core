plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":base-services"))


    implementation("com.esotericsoftware:kryo:5.3.0")
    implementation ("org.slf4j:slf4j-api:1.7.36")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation("it.unimi.dsi:fastutil:8.5.8")
}