androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))

        // Explicit JUnit4 dependency to ensure unit tests are discovered/executed in the Android unit test task.
        implementation("junit:junit:4.13.2")
    }
}
