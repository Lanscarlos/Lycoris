plugins {
    java
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("common-5")
    install("module-database")
    install("module-configuration")
    install("module-ui")
    install("module-chat")
    install("module-lang")
    install("module-kether")
    install("expansion-command-helper")
    install("expansion-player-database")
    install("platform-bukkit")
	
	description {
        contributors {
            name("Lanscarlos")
        }
    }
	
    classifier = null
    version = "6.0.3-21"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}