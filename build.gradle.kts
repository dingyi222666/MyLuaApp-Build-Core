// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.1.3" apply false
    id("com.android.library") version "7.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    id("org.jetbrains.kotlin.jvm") version "1.6.10" apply false
    id("com.github.kezong:fat-aar") version "1.3.8" apply false
    /*id("com.github.kezong:fat-aar-gradle-plugin") version "1.3.8" apply false*/
}



tasks {
    create<Delete>("clean") {
        delete(rootProject.buildDir)
    }
}
