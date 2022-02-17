plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.1.0"
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

        implementation("org.jetbrains:annotations:20.1.0")
        implementation("com.google.code.gson:gson:2.8.6")
        implementation("com.google.guava:guava:30.0-jre")
        implementation("com.google.inject:guice:4.2.2")
    }

    /*
    if (System.getProperty("publishName") != null && System.getProperty("publishPassword") != null) {
        publishing {
            (components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
                skip()
            }
            publications {
                create<MavenPublication>(project.name) {
                    groupId = "de.notion"
                    artifactId = "natrox-messaging"
                    version = "1.0-SNAPSHOT"
                    from(components.findByName("java"))
                    pom {
                        name.set(project.name)
                        properties.put("inceptionYear", "2021")
                        developers {
                            developer {
                                id.set("dasdrolpi")
                                name.set("Lars")
                                email.set("admin@natrox.de")
                            }
                        }
                    }
                }
                repositories {
                    maven("https://repo.natrox.de/repository/maven-internal/") {
                        this.name = "natrox-internal"
                        credentials {
                            this.password = System.getProperty("publishPassword")
                            this.username = System.getProperty("publishName")
                        }
                    }
                }
            }
        }
    }

     */

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