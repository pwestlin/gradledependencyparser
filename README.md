# Gradle Dependency Parser #

This little application scans a Gradle build file ()build.gradle) for dependencies and checks for new version at [mvnrepository.com](http://mvnrepository.com/).

## System requirements ##

What you need to build and run this application:
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Gradle 3](https://https://gradle.org/gradle-download/)

## Build it ##
`gradle build`

## Run it ##
`java -jar build/libs/GradleDependencyParser.jar path/to/your/build.gradle`

## TODO ##
* Use `cols.size() - x` instead of `if(cols.size == 4)...`.
* Make an executable jar with libs.
