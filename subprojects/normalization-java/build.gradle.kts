plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":base-annotations"))
    implementation(project(":hashing"))
    implementation(project(":files"))
    implementation(project(":snapshots"))
    implementation(project(":functional"))

    implementation ("org.ow2.asm:asm:9.3")
    implementation ("commons-lang:commons-lang:2.6")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
}