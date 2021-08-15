/*
 * Copyright (C) 2021. huangwenhuan1125@163.com..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.vanniktech.maven.publish.SonatypeHost.S01

plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm")
  id("com.vanniktech.maven.publish")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

mavenPublishing {
  publishToMavenCentral(S01)
}

dependencies {
  implementation(deps.retrofit.runtime)
  implementation(deps.retrofit.gson)
  implementation(deps.retrofit.rxjava2)
  implementation(deps.squareup.okhttp)

  compileOnly(deps.google.jsr305)
  compileOnly(tests.google.guava)

  testImplementation(tests.test.junit)
  testImplementation(tests.test.truth)
  testImplementation(tests.google.guava)
}
