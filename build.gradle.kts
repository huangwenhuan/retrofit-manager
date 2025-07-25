buildscript {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
  }

  dependencies {
    classpath("com.android.tools.build:gradle:8.11.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
    classpath("com.vanniktech:gradle-maven-publish-plugin:0.17.0")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
  }
}