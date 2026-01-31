operator fun Project.get(property: String): String = property(property) as String

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    //accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    neoForge {
        //convertAccessWideners.set(true)
        //extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }

    mods {
        register("builtinservers") {
            sourceSet(sourceSets.main.get())
        }
    }
}

//Not to sure this is correct
val common by configurations.creating
val shadowCommon by configurations.creating

configurations {
    common
    shadowCommon // Don't use shadow from the shadow plugin since it *excludes* files.
    compileClasspath { extendsFrom(common) }
    runtimeClasspath { extendsFrom(common) }
    "developmentNeoForge" { extendsFrom(common) }
}

dependencies {
    neoForge("net.neoforged:neoforge:${rootProject["neoforge_version"]}")

    common(project(":common", "namedElements")) { isTransitive=false }
    shadowCommon(project(":common", "transformProductionNeoForge")) { isTransitive=false }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveClassifier.set("neoforge-dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn.add("shadowJar")
    archiveClassifier.set("neoforge")
}

tasks.jar {
    archiveClassifier.set("neoforge-dev")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.get().archiveFile.map { zipTree(it) } )
    archiveClassifier.set("neoforge-sources")
}

with(components["java"] as AdhocComponentWithVariants) {
    withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) { skip() }
}

publishing {
    publications {
        register("mavenNeoForge", MavenPublication::class){
            artifactId = "${base.archivesName.get()}-${project.name}"
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}
