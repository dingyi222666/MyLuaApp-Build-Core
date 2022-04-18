plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(project(":core-api"))
    api(project(":problems"))

    implementation(project(":base-services"))
    implementation(project(":functional"))
    implementation(project(":logging"))
    implementation(project(":messaging"))
    implementation(project(":persistent-cache"))
    implementation(project(":snapshots"))

    implementation(project(":base-annotations"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation ("org.ow2.asm:asm:9.3")
    implementation ("commons-lang:commons-lang:2.6")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
}