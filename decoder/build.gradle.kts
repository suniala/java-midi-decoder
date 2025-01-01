plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.jar {
    archiveBaseName.set("java-midi-decoder")
    archiveVersion.set(rootProject.version.toString())
}
