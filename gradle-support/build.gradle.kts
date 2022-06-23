plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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



dependencies {

    testImplementation("junit:junit:4.13.2")

    implementation(project(":core"))
    implementation(project(":base-services-groovy"))
    implementation(project(":launcher"))
    implementation(project(":model-core"))
    implementation(project(":logging"))
    implementation(project(":core-api"))
    implementation(project(":configuration-cache"))
    implementation(project(":base-services"))

    implementation(project(":virtual-process"))
    runtimeOnly("net.rubygrapefruit:file-events-linux-aarch64:0.22-milestone-23")

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

fun isBuildForAndroid(): Boolean {
    val taskNames = project
        .gradle
        .startParameter
        .taskNames

    return taskNames.any { it.indexOf("assemble") != -1 || it.indexOf("clean") != -1 }
}

fun getAllProjectDependency(): Set<Project> {
    val result = mutableSetOf<Project>()

    val subProject = rootProject.file("subprojects")

    subProject.listFiles()?.filter {
        it.name != "android-stubs"
    }?.forEach {
        result.add(project(":${it.name}"))
    }

    return result
}


project.afterEvaluate {
    android.libraryVariants
        .forEach { variant ->

            //capitalize (debug) -> (Debug)
            val variantsName = variant.name.capitalize()

            val variantsOutput = tasks.getByName("bundle${variantsName}Aar")
                .outputs.files.first()

            val unpackAarTask = tasks.create("unpack${variantsName}Aar", Copy::class.java)

            val copyDependencyBuildJarToAarLibsTask =
                tasks.create("copyDependency${variantsName}BuildJarToAarLibs", Copy::class.java) {
                    doLast {

                    }
                    dependsOn(unpackAarTask)
                }

            val reBundleAarTask = tasks.create("reBundle${variantsName}Aar", Zip::class.java) {
                doLast {
                    //increment
                    //delete("build/tmp/unpack")
                }
                dependsOn(copyDependencyBuildJarToAarLibsTask)
            }


            getAllProjectDependency()
                .filter { it.plugins.hasPlugin("java-library") }
                .map {
                    it.tasks.getByName("jar").outputs.files.files.first()
                }.forEach {
                    copyDependencyBuildJarToAarLibsTask.from(it)
                }

            copyDependencyBuildJarToAarLibsTask.into(
                "build/tmp/unpack-${variantsName}/libs"
            )


            /*unpackDebugAarTask.inputs.file(variantsOutput)
            unpackDebugAarTask.outputs.files(
                "build/tmp/unpack"
            )*/


            unpackAarTask.from(zipTree(variantsOutput))
            unpackAarTask.into("build/tmp/unpack-${variantsName}")


            reBundleAarTask.from("build/tmp/unpack-${variantsName}")
            reBundleAarTask.destinationDirectory.set(File("build/outputs/aar"))
            reBundleAarTask.archiveFileName
                .convention(variantsOutput.name.replace(".aar", "-re-bundle.aar"))
                .set(variantsOutput.name.replace(".aar", "-re-bundle.aar"))


            tasks.getByName("assemble${variantsName}")
                .dependsOn(reBundleAarTask)

            unpackAarTask.mustRunAfter(tasks.getByName("bundle${variantsName}Aar"))
        }
}


