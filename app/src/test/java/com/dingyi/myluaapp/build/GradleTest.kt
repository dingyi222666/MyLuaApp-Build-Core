package com.dingyi.myluaapp.build


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.configuration.ConsoleOutput
import org.gradle.api.logging.configuration.ShowStacktrace
import org.gradle.api.logging.configuration.WarningMode
import org.gradle.groovy.scripts.internal.BuildScriptTransformer
import org.gradle.initialization.UserHomeInitScriptFinder
import org.gradle.internal.build.BuildStateRegistry
import org.gradle.internal.buildtree.BuildTreeModelControllerServices
import org.gradle.internal.classpath.ClassPath
import org.gradle.internal.service.ServiceRegistry
import org.gradle.internal.service.scopes.GlobalScopeServices
import org.gradle.launcher.TestGradleLauncher
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader


class GradleTest {


    private fun extractImplementationClassNames(resource: URL): List<String>? {

        val urlConnection = resource.openConnection()

        val inputStream = urlConnection.getInputStream()
        return kotlin.runCatching {
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val implementationClassNames = mutableListOf<String>()
            reader.forEachLine {
                val line = it.replace("#.*".toRegex(), "").trim { it <= ' ' }
                if (line.isNotEmpty()) {
                    implementationClassNames.add(line)
                }

            }
            implementationClassNames
        }.onFailure {
            inputStream.close()
        }.getOrNull()
    }


    private fun copyTestResourcesToLocalResources() {
        val path = File("").canonicalFile.resolve("src/test/resources")
        val localResourcePath = this.javaClass.classLoader
            .getResource("META-INF")


        path.copyRecursively(File(localResourcePath.toURI()),true)
    }

    @Test
    fun test1() {

        copyTestResourcesToLocalResources()

        val projectPath = File("G:\\android studio project\\MyLuaApp-Build-Core\\app\\src\\main\\resources\\TestProject")

        System.setProperty("org.gradle.native", "false");
        val launcher = TestGradleLauncher
            .createLauncher {
                it.showStacktrace = ShowStacktrace.ALWAYS_FULL;
                /* setConfigurationCache(BuildOption.Value.value(true));
                 startParameter.setConfigurationCacheDebug(true);*/
                it.warningMode = WarningMode.All;
                it.consoleOutput = ConsoleOutput.Plain
                it.logLevel = LogLevel.INFO;
                it.isBuildCacheEnabled = true
                it.isBuildCacheDebugLogging = true
                it.projectDir = projectPath
                it.gradleUserHomeDir = projectPath.resolve(".gradle_home")
                it.projectCacheDir =  projectPath.resolve(".gradle")

            }


        launcher
            /*.apply {
                onCreateGradle { gradle ->
                    println("get gradle")
                    println("gradle: $gradle")
                }
            }*/
            .execute("assemble")
    }


}


