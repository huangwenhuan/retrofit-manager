buildscript {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
  }

  dependencies {
    classpath("com.android.tools.build:gradle:7.0.0")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
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