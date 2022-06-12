plugins {
    id("org.jetbrains.kotlin.jvm").version("1.7.0")
    id("net.mbonnin.sjmp").version("0.2")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}
tasks.withType(JavaCompile::class.java) {
    options.release.set(8)
}

sjmp {
    jvmProject {
        publication {
            artifactId = "bare-graphql"
            groupId = "net.mbonnin.bare-graphql"
            version = "0.0.1-SNAPSHOT"
            simplePom {
                name = "bare-graphql"
                githubRepository = "martinbonnin/bare-graphql"
                githubLicensePath = "LICENSE"
                license = "MIT License"
            }
        }
    }
}
