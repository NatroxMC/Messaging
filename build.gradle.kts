/*
 * Copyright 2020-2022 NatroxMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.notion"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        implementation(files("D:\\NotionPowered\\projects\\notion-common\\build\\libs\\notion-common.jar"))

        implementation("org.jetbrains:annotations:23.0.0")
        implementation("com.google.code.gson:gson:2.9.0")
        implementation("com.google.guava:guava:31.0.1-jre")
        implementation("com.google.inject:guice:4.2.2")
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        shadowJar {
            //Set the Name of the Output File
            archiveFileName.set("${project.name}.jar")
        }
    }
}