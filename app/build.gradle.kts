plugins {
    application
}

dependencies {
    implementation(project(":decoder"))
}

application {
    mainClass = "fi.kapsi.kosmik.javamididecoder.app.MidiDump"
}
