package com.dingyi.myluaapp.build

import groovy.lang.Closure
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.logger.info("test plugin")
        target.task("sync") { task ->

            task.doLast {
                target
                    .configurations
                    .matching {
                        it.isCanBeResolved
                    }
                    .forEach {
                        it.resolve()
                    }
                target.logger.info("sync task")
            }

            //config input

        }
    }
}

