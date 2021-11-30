import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

plugins {
    id("maven-publish")
    id("signing")
}

publishing {
    fun Project.getRepositoryUrl(): URI {
        val isReleaseBuild = properties["POM_VERSION_NAME"]?.toString()?.contains("SNAPSHOT") == false
        val releaseRepoUrl = properties["RELEASE_REPOSITORY_URL"]?.toString() ?: "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        val snapshotRepoUrl = properties["SNAPSHOT_REPOSITORY_URL"]?.toString() ?: "https://oss.sonatype.org/content/repositories/snapshots/"
        return uri(if (isReleaseBuild) releaseRepoUrl else snapshotRepoUrl)
    }


    publications {
        val props = project.properties

        // 1. configure repositories
        repositories {
            maven {
                url = getRepositoryUrl()
                // credentials are stored in ~/.gradle/gradle.properties with ~ being the path of the home directory
                credentials {
                    username = props["ossUsername"]?.toString() ?: throw IllegalStateException("ossUsername not found")
                    password = props["ossPassword"]?.toString() ?: throw IllegalStateException("ossPassword not found")
                }
            }
        }

        // 2. configure publication
        val publicationName = props["POM_NAME"]?.toString() ?: "publication"
        create<MavenPublication>(publicationName) {
            pom {
                groupId = props["POM_GROUP_ID"].toString()
                artifactId = props["POM_ARTIFACT_ID"].toString()
                version = props["POM_VERSION_NAME"].toString()

                name.set(props["POM_NAME"].toString())
                description.set(props["POM_DESCRIPTION"].toString())
                url.set(props["POM_URL"].toString())
                packaging = props["POM_PACKAGING"].toString()

                scm {
                    url.set(props["POM_SCM_URL"].toString())
                    connection.set(props["POM_SCM_CONNECTION"].toString())
                    developerConnection.set(props["POM_SCM_DEV_CONNECTION"].toString())
                }

                organization {
                    name.set(props["POM_COMPANY_NAME"].toString())
                    url.set(props["POM_COMPANY_URL"].toString())
                }

                developers {
                    developer {
                        id.set(props["POM_DEVELOPER_ID"].toString())
                        name.set(props["POM_DEVELOPER_NAME"].toString())
                        email.set(props["POM_DEVELOPER_EMAIL"].toString())
                    }
                }

                licenses {
                    license {
                        name.set(props["POM_LICENCE_NAME"].toString())
                        url.set(props["POM_LICENCE_URL"].toString())
                        distribution.set(props["POM_LICENCE_DIST"].toString())
                    }
                }
            }
        }

        // 3. sign the artifacts
        signing {
            val signingKeyId = props["signingKeyId"]?.toString() ?: throw IllegalStateException("signingKeyId not found")
            val signingKeyPassword = props["signingKeyPassword"]?.toString() ?: throw IllegalStateException("signingKeyPassword not found")
            val signingKey = props["signingKey"]?.toString() ?: throw IllegalStateException("signingKey not found")
            useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassword)
            sign(publishing.publications.getByName(publicationName))
        }
    }
}
