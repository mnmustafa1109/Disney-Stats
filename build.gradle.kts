plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // add opencsv dependency
    implementation("com.opencsv:opencsv:5.5.2")
    // add add mariadb
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.4")

    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
}

tasks.test {
    useJUnitPlatform()
}