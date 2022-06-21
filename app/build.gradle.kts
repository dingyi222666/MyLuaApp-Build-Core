plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {

    compileSdk = 32
    buildToolsVersion = "32.0.0"

    defaultConfig {
        applicationId = "com.dingyi.myluaapp.build.api"
        minSdk = 26
        targetSdk = 32

        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
            /* resources.srcDirs("src/main/res")*/

        }
        getByName("test") {
            java.srcDirs("src/test/kotlin")
            resources.srcDirs("src/test/resources")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            ndk {
//                this.abiFilters.addAll(arrayOf("armeabi-v7a", "arm64-v8a"))
            }
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    //need to apply in application module
    packagingOptions {
       // resources.excludes.addAll(arrayOf("xsd/*", "license/*"))
        // resources.pickFirsts.addAll(arrayOf("kotlin/**","META-INF/**"))
        if (isBuildForAndroid()) {
            resources
                .excludes
                .addAll(arrayOf("org/fusesource/**", "**.dylib", "**.dll"))
        }
        resources.merges.add("META-INF/**")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }



}

fun isBuildForAndroid(): Boolean {
    val taskNames = project
        .gradle
        .startParameter
        .taskNames

    return taskNames.any { it.indexOf("assemble") != -1 || it.indexOf("clean") != -1 }
}


dependencies {
//implementation(project(":build-core"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.4.0")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

   /* implementation(project(":core"))
    implementation(project(":base-services-groovy"))
    implementation(project(":launcher"))
    implementation(project(":model-core"))
    implementation(project(":logging"))
    implementation(project(":core-api"))
    implementation(project(":configuration-cache"))
    implementation(project(":base-services"))
*/

    implementation(project(":gradle-support"))

    implementation(project(":terminal-view"))
    /*runtimeOnly("net.rubygrapefruit:file-events-linux-aarch64:0.22-milestone-23")*/

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

/*implementation("io.github.dingyi222666:groovy-android:1.0.4")*/


//implementation(project(":configuration-cache"))


//    implementation ("com.esotericsoftware:kryo:5.3.0")
//    implementation("javax.inject:javax.inject:1")
//    implementation("com.google.guava:guava:30.1.1-jre")
//    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
//    implementation("com.google.code.findbugs:jsr305:3.0.2")
//    implementation("commons-lang:commons-lang:2.6")
//    implementation("com.googlecode:openbeans:1.0")
//    implementation ("org.ow2.asm:asm:9.3")
//    implementation ("net.rubygrapefruit:native-platform:0.22-milestone-23")
//    implementation("net.rubygrapefruit:file-events:0.22-milestone-23")
//    implementation("commons-io:commons-io:2.11.0")
//    implementation("org.apache.commons:commons-compress:1.21")
//    implementation ("org.slf4j:slf4j-api:1.7.36")
//    implementation("org.fusesource.jansi:jansi:2.4.0")
//    implementation("it.unimi.dsi:fastutil:8.5.8")

}