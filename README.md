# Gradle Dependency Parser #

This little handy console application scans a Gradle build file ()build.gradle) for dependencies and checks for new versions at [mvnrepository.com](http://mvnrepository.com/).

## Disclaimer ##

This application is written by me for me. If it doesn't work for you or don't suit your pruposes, please feel free to rewrite it yourself. :) 

## System requirements ##

What you need to build and run this application:
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Gradle 3](https://https://gradle.org/gradle-download/)

## Build it ##
`gradle fatJar`

## Run it ##
`java -jar build/libs/gradledependencyparser-all.jar path/to/your/build.gradle`

## TODO ##
* Print execution report like "Dependencies with new versions" and "Skipped dependencies".