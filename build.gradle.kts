plugins {
    id("java")

    `java-library`
    `java-library-distribution`
    `java-test-fixtures`

}

group = "ic3-software"
version = "0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

repositories {
    mavenCentral()

    flatDir {
        dirs("$rootDir/lib")
    }
}

dependencies {


    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    // e.g., cdp4j
    implementation("org.apache.logging.log4j:log4j-1.2-api:2.22.0")

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.vdurmont:semver4j:3.1.0")
    implementation(":cdp4j-6.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("kit-clean") {

    dependsOn(
            "clean"
    )

    doLast {

        delete(layout.buildDirectory.dir("AnalyticsOps"))

    }

}

tasks.register("kit-install") {

    dependsOn(
            "installDist"
    )

    doLast { }

}

tasks.register("kit-build") {

    group = "kit"
    description = "Build the AnalyticsOps kit"

    dependsOn(
            "kit-clean",
            "kit-install"
    )

    doLast {

        mkdir(layout.buildDirectory.dir("AnalyticsOps"))

        mkdir(layout.buildDirectory.dir("AnalyticsOps/lib"))

        copy {
            from(layout.buildDirectory.dir("install/ic3-analytics-ops/lib")) {
                include("**/*.jar")
            }
            into(layout.buildDirectory.dir("AnalyticsOps/lib"))
        }
        copy {
            from(layout.buildDirectory.dir("install/ic3-analytics-ops")) {
                include("*.jar")
            }
            into(layout.buildDirectory.dir("AnalyticsOps/lib"))
        }

        mkdir(layout.buildDirectory.dir("AnalyticsOps/bin"))

        copy {
            from(layout.projectDirectory.dir("etc/bin")) {
                include("AnalyticsOps.sh")
            }
            into(layout.buildDirectory.dir("AnalyticsOps/bin"))
        }

        mkdir(layout.buildDirectory.dir("AnalyticsOps/tests"))

        copy {
            from(layout.projectDirectory.dir("etc/tests")) {
                include("smoke.test.json5")
            }
            into(layout.buildDirectory.dir("AnalyticsOps/tests"))
        }
    }
}