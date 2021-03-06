plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":base-annotations"))
    implementation(project(":files"))
    implementation("org.slf4j:slf4j-api:1.7.36")
}