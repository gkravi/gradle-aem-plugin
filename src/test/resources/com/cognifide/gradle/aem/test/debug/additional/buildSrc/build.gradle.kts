repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("http://dl.bintray.com/cognifide/maven-public") }
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:8.1.0")
}
