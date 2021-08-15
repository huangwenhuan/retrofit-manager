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

import okhttp3.OkHttpClient
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RMProvider internal constructor(
  private val key: String,
  private val factory: Factory,
  private val rmStore: RMStore,
  private val mNProviderManager: RMProviderManager
) {
  /**
   * Implementations of `Factory` interface are responsible to instantiate
   * [RetrofitManager].
   */
  interface Factory {
    fun <T : RetrofitManager?> create(key: String): T
  }

  fun <T : RetrofitManager?> get(): T {
    val key = key
    var manager = rmStore.findRetrofitManager<RetrofitManager>(key)
    return if (manager != null) {
      mNProviderManager.onNNetworkSelected(key, manager)
      manager as T
    } else {
      manager = factory.create(key)
      mNProviderManager.onNNetworkCreated(key, manager!!)
      manager as T
    }
  }

  class DefaultFactory(private val baseUrl: String) : Factory {
    override fun <T : RetrofitManager?> create(key: String): T {
      val builder = RetrofitManager.Builder()
        .setRetrofit(getRetrofit(baseUrl))
        .setOkHttpClient(okHttpClient)
        .setName(key)
        .setBaseUrl(baseUrl)
      return builder.build() as T
    }

    protected fun getRetrofit(baseUrl: String?): Builder {
      return Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl!!)
    }

    protected val okHttpClient: OkHttpClient?
      protected get() = null

  }
}