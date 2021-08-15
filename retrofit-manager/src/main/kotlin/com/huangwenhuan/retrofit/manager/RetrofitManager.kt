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
import com.huangwenhuan.retrofit.manager.RMProvider.Factory
import retrofit2.Retrofit
import java.util.WeakHashMap

class RetrofitManager internal constructor(
  val retrofit: Retrofit?, val name: String?
) {
  /** Callbacks for NNetwork register events.  */
  interface Listener {
    fun onProviderAdded(key: String, value: RMProvider)
    fun onProviderDuplicate(key: String, value: RMProvider)
    fun onNNetworkSelected(key: String, network: RetrofitManager)
    fun onNNetworkCreated(key: String, network: RetrofitManager)
  }

  private val instanceStore: Store

  fun key(): String? = name

  fun newBuilder(): Builder {
    return Builder(this)
  }

  fun <T> getService(service: Class<T>): T? {
    var instance = instanceStore[service]
    if (instance == null) {
      instance = retrofit?.create(service)
      instanceStore.put(service, instance)
    }
    return instance
  }

  class Builder {
    private var retrofit: Retrofit.Builder? = null
    private var okHttpClient: OkHttpClient? = null
    private var baseUrl: String? = null
    private var name: String? = null

    constructor() {}
    internal constructor(manager: RetrofitManager) {
      retrofit = manager.retrofit?.newBuilder()
    }

    fun setRetrofit(retrofit: Retrofit.Builder?): Builder {
      this.retrofit = retrofit
      return this
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient?): Builder {
      this.okHttpClient = okHttpClient
      return this
    }

    fun setBaseUrl(baseUrl: String?): Builder {
      this.baseUrl = baseUrl
      return this
    }

    fun setName(name: String?): Builder {
      this.name = name
      return this
    }

    fun build(): RetrofitManager {
      checkNotNull(retrofit) { "Retrofit required." }
      if (okHttpClient != null) {
        retrofit = retrofit!!.client(okHttpClient!!)
      }
      if (baseUrl != null) {
        retrofit = retrofit!!.baseUrl(baseUrl!!)
      }
      name = RMStore.tryReviseName(RetrofitManager::class.java, name)
      val retrofitManager = RetrofitManager(retrofit!!.build(), name)
      RMStore.putIfAbsent(retrofitManager)
      return retrofitManager
    }
  }

  internal class Store {
    val mThreadLocal: ThreadLocal<MutableMap<Class<*>, Any?>> =
      object : ThreadLocal<MutableMap<Class<*>, Any?>>() {
        override fun initialValue(): MutableMap<Class<*>, Any?> {
          return WeakHashMap()
        }
      }

    fun <T> put(key: Class<T>, instance: Any?) {
      mThreadLocal.get()[key] = instance
    }

    operator fun <T> get(key: Class<T>): T? {
      return mThreadLocal.get()[key] as T?
    }
  }

  companion object {
    @JvmStatic fun with(): RetrofitManager? {
      return RMProviderManager.with().get()
    }

    @JvmStatic fun with(key: String): RetrofitManager {
      return requireNotNull(RMProviderManager.with(key).get()) {
      }
    }

    @JvmStatic @Synchronized fun registerProvider(factory: Factory) {
      RMProviderManager.registerProvider(factory)
    }

    @JvmStatic @Synchronized fun registerProvider(key: String, factory: Factory) {
      RMProviderManager.registerProvider(key, factory)
    }

    @JvmStatic @Synchronized fun unregisterProvider() {
      RMProviderManager.unregisterProvider()
    }

    @JvmStatic @Synchronized fun unregisterProvider(key: String) {
      RMProviderManager.unregisterProvider(key)
    }

    @JvmStatic var isDebug: Boolean
      set(value) {
        RMProviderManager.debug = value
      }
      get() = RMProviderManager.debug

    @JvmStatic var listener: Listener?
      set(value) {
        RMProviderManager.listener = listener
      }
      get() = RMProviderManager.listener
  }

  init {
    instanceStore = Store()
  }
}