plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(project(":files"))

    compileOnly(project(":android-stubs"))
    compileOnly(project(":virtual-process"))

    implementation(project(":base-services"))
    implementation(project(":file-temp"))


    implementation("javax.inject:javax.inject:1")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.code.findbugs:jsr305:3.0.2")



    implementation("net.rubygrapefruit:native-platform:0.22-milestone-23")
    implementation("net.rubygrapefruit:file-events:0.22-milestone-23")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.fusesource.jansi:jansi:2.4.0")

}