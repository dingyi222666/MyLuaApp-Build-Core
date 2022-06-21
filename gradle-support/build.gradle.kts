plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.kezong.fat-aar")
}


android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        // resources.excludes.addAll(arrayOf("xsd/*", "license/*"))
        // resources.pickFirsts.addAll(arrayOf("kotlin/**","META-INF/**"))
        if (isBuildForAndroid()) {
            resources
                .excludes
                .addAll(arrayOf("org/fusesource/**", "**.dylib", "**.dll"))
        }
        resources.merges += "META-INF/**"
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

    testImplementation("junit:junit:4.13.2")

    add("embed", project(":core"))
    add("embed", project(":base-services-groovy"))
    add("embed", project(":launcher"))
    add("embed", project(":model-core"))
    add("embed",project(":logging"))
    add("embed",project(":core-api"))
    add("embed",project(":configuration-cache"))
    add("embed",project(":base-services"))

    implementation(project(":virtual-process"))
    runtimeOnly("net.rubygrapefruit:file-events-linux-aarch64:0.22-milestone-23")

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}