/*
 * Copyright (C) 2021. huangwenhuan1125@163.com.
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
package com.huangwenhuan.retrofit.manager

import com.google.common.truth.Truth
import com.huangwenhuan.retrofit.manager.RMProvider.DefaultFactory
import com.huangwenhuan.retrofit.manager.Util.simpleClassName
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit.Builder

class RMStoreTest {
  @Before fun before() {
    RMStore.clear()
  }

  @Test fun testGenerateName0() {
    val retrofitManager = RetrofitManager(null, null)
    val prefix = simpleClassName(RetrofitManager::class.java)
    while (true) {
      val name = RMStore.generateKey(RetrofitManager::class.java)
      println(name)
      RMStore.putIfAbsent(name, retrofitManager)
      Truth.assertThat(name).startsWith(prefix)
      if (name == "RetrofitManager#1000") break
    }
  }

  @Test fun testRMStore() {
    val expected = RetrofitManager(null, "RetrofitManager#0")
    RMStore.putIfAbsent(expected)
    val manager: RetrofitManager? = RMStore.findRetrofitManager("RetrofitManager#0")
    Truth.assertThat(manager).isEqualTo(expected)
  }

  @Test fun testBuilder() {
    val retrofit = Builder().baseUrl("http://www.baidu.com").build()
    while (true) {
      val retrofitManager: RetrofitManager =
        RetrofitManager.Builder()
          .setRetrofit(retrofit.newBuilder()).setName("RetrofitManager/Default")
          .build()
      val manager: RetrofitManager? = RMStore.findRetrofitManager(retrofitManager.key())
      Truth.assertThat(manager).isEqualTo(retrofitManager)
      if (retrofitManager.key() == "RetrofitManager#1000") {
        break
      }
    }
  }

  @Test fun testRMProvider() {
    val key = "RetrofitManager"
    RetrofitManager.registerProvider(key, DefaultFactory("https://httpbin.org"))
    Truth.assertThat(RMProviderManager.with(key).get<RetrofitManager>().key()).isEqualTo(key)
  }
}