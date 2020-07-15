/*
 * Copyright 2020 poispois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val githubUser = "pois0"
val githubRepository = "Liter"

val bintrayUser = "pois"
val bintrayRepository = "KotlinLibs"
val bintrayPackage = "Liter"

val rawVersion = System.getenv("LITER_VERSION")

group = "jp.pois"
version = rawVersion?.toString()?.let {
    if (it[0] == 'v') it.substring(1) else it
} ?: ""

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.jfrog.bintray") version "1.8.5"
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

bintray {
    user = "pois"
    key = System.getenv("BINTRAY_API")

    publish = true

    setPublications("library")

    pkg.apply {
        repo = "KotlinLibs"
        name = bintrayPackage
        setLicenses("Apache-2.0")

        version.apply {
            name = project.version as String
            released = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(ZonedDateTime.now())
            vcsUrl = "https://github.com/pois0/Liter/tree/$rawVersion"
            vcsTag = rawVersion
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            groupId = project.group as String
            artifactId = rootProject.name
            version = project.version as String

            from(components["java"])

            pom {
                name.set(rootProject.name)
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("pois")
                        email.set("dev@pois.jp")
                    }
                }
                scm {
                    url.set("https://giuhtb.com/$githubUser/$githubRepository")
                }
            }
        }
    }
}
