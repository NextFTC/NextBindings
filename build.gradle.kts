plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    `java-library`
    alias(libs.plugins.deployer)
    alias(libs.plugins.dokka)
}

group = "dev.nextftc"
version = property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

val dokkaJar = tasks.register<Jar>("dokkaJar") {
    dependsOn(tasks.named("dokkaGenerate"))
    from(dokka.basePublicationsDirectory.dir("html"))
    archiveClassifier = "html-docs"
}

deployer {
    projectInfo {
        name = "NextBindings"
        description = "A user-friendly bindings library for the FIRST Tech Challenge"
        url = "https://nextftc.dev/bindings"
        scm {
            fromGithub("NextFTC", "NextBindings")
        }
        license("GNU General Public License, version 3", "https://www.gnu.org/licenses/gpl-3.0.html")
        developer("Davis Luxenberg", "davis.luxenberg@outlook.com", url = "https://github.com/BeepBot99")
    }

    signing {
        key = secret("MVN_GPG_KEY")
        password = secret("MVN_GPG_PASSWORD")
    }

    content {
        kotlinComponents {
            kotlinSources()
            docs(dokkaJar)
        }
    }

    localSpec {
        release.version = "$version-LOCAL"
    }

    nexusSpec("snapshot") {
        release.version = "$version-SNAPSHOT"
        repositoryUrl = "https://central.sonatype.com/repository/maven-snapshots/"
        auth {
            user = secret("SONATYPE_USERNAME")
            password = secret("SONATYPE_PASSWORD")
        }
    }

    centralPortalSpec {
        auth {
            user = secret("SONATYPE_USERNAME")
            password = secret("SONATYPE_PASSWORD")
        }
        allowMavenCentralSync = (property("automaticMavenCentralSync") as String).toBoolean()
    }
}
