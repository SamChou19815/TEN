plugins {
    java
    `java-library`
    maven
    `maven-publish`
    signing
}

group = "com.developersam"
version = "2.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(dependencyNotation = "org.jetbrains:annotations:16.0.2")
    testImplementation(dependencyNotation = "junit:junit:4.12")
}

tasks {
    named<Test>(name = "test") {
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
    register<Jar>("sourcesJar") {
        from(sourceSets["main"].allJava)
        classifier = "sources"
    }
    register<Jar>("javadocJar") {
        from(javadoc)
        classifier = "javadoc"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set("TEN")
                description.set("Board Game TEN")
                url.set("https://github.com/SamChou19815/ten")
                scm {
                    url.set("https://github.com/SamChou19815/ten")
                    connection.set("https://github.com/SamChou19815/TEN/tree/master")
                    developerConnection.set("scm:git:ssh://github.com:SamChou19815/ten.git")
                }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        name.set("Developer Sam")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                val sonatypeUsername: String? by project
                val sonatypePassword: String? by project
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
