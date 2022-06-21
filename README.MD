# MyLuaApp-Build-Core

This is a project for portable build of gradle based on gradle 7.2.
The target is to be implemented on android devices with minimal gradle integration, and to provide support for services between the original MyLuaApp build service and the original Gradle build service. 
The project can now run gradle's most basic tasks on android devices, such as `help` tasks

## TODO
 - [ ] Simplify the code of gradle
 - [ ] Refactor virtual process module and fix terminal display error
 - [x] Running groovy scripts in android (See [#1](https://github.com/dingyi222666/GroovyInAndroid))
 - [x] Run gradle help tasks in android
 - [ ] Run gradle java plugin in android
 - [ ] Generate project model for project based on gradle
 - [ ] Reimplement an android gradle plugin for this ported gradle

## Know Issues
 - [ ] The task build runtime output to terminal from gradle run is not correct
 - [ ] Not support setting the value of the system property `org.gradle.native` to true

