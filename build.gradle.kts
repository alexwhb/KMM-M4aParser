plugins {
    kotlin("multiplatform") version "1.9.10"
}

group = "me.alexblack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
//    js {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isArm64 = System.getProperty("os.arch") == "aarch64"
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
//        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
//        hostOs == "Linux" && isArm64 -> linuxArm64("native")
//        hostOs == "Linux" && !isArm64 -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    ios {
        binaries {
            framework {
                baseName = "M4aUtils"
            }
        }
    }
    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = "M4aUtils"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
                implementation("io.ktor:ktor-client-core:2.3.4")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-core-jvm:2.3.4")
                implementation("io.ktor:ktor-client-apache5:2.3.4")
            }
        }

        val jvmTest by getting
//        val jsMain by getting
//        val jsTest by getting
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:2.3.4")
//                implementation("io.ktor:ktor-client-darwin:2.3.4")

            }

        }
//        val nativeMain by getting
//        val nativeTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}
