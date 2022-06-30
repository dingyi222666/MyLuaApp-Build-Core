plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {



    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("commons-io:commons-io:2.11.0")
    api ("org.slf4j:slf4j-api:1.7.36")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("commons-lang:commons-lang:2.6")
    implementation("javax.inject:javax.inject:1")

    implementation("commons-io:commons-io:2.11.0")

    implementation(project(":base-services"))
    implementation(project(":messaging"))
    implementation(project(":cli"))
    implementation(project(":build-option"))

    implementation(project(":native"))

    implementation("org.slf4j:jul-to-slf4j:1.7.36")


    implementation("org.fusesource.jansi:jansi:2.4.0")
}