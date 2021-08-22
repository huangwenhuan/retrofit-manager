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
  val retrofit: Retrofit?,
  val name: String
) {
  /** Callbacks for RetrofitManager register events.  */
  interface Listener {
    fun onProviderAdded(key: String, value: RMProvider)
    fun onProviderDuplicate(key: String, value: RMProvider)
    fun onRetrofitManagerSelected(key: String, network: RetrofitManager)
    fun onRetrofitManagerCreated(key: String, network: RetrofitManager)
  }

  private val instanceStore: Store

  init {
    instanceStore = Store()
  }

  fun key(): String = name

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

  class Builder constructor() {
    private var retrofitBuilder: Retrofit.Builder? = null
    private var okHttpClient: OkHttpClient? = null
    private var baseUrl: String? = null
    private var name: String? = null

    internal constructor(manager: RetrofitManager) : this() {
      retrofitBuilder = manager.retrofit?.newBuilder()
    }

    fun setRetrofitBuilder(builder: Retrofit.Builder?): Builder = apply {
      this.retrofitBuilder = builder
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient?): Builder = apply {
      this.okHttpClient = okHttpClient
    }

    fun setBaseUrl(baseUrl: String): Builder = apply {
      this.baseUrl = baseUrl
    }

    fun setName(name: String): Builder = apply {
      this.name = name
    }

    fun build(): RetrofitManager {
      val retrofitBuilder = checkNotNull(retrofitBuilder) { "Retrofit required." }
        .apply {
          okHttpClient?.let { client(it) }
          baseUrl?.let { baseUrl(it) }
        }
      name = RMStore.tryReviseName(RetrofitManager::class.java, name)
      val retrofitManager = RetrofitManager(retrofitBuilder.build(), requireNotNull(name))
      RMStore.putIfAbsent(retrofitManager)
      return retrofitManager
    }
  }

  private class Store {
    private val threadLocal: ThreadLocal<MutableMap<Class<*>, Any?>> =
      object : ThreadLocal<MutableMap<Class<*>, Any?>>() {
        override fun initialValue(): MutableMap<Class<*>, Any?> {
          return WeakHashMap()
        }
      }

    fun <T> put(key: Class<T>, instance: Any?) {
      threadLocal.get()[key] = instance
    }

    operator fun <T> get(key: Class<T>): T? {
      return threadLocal.get()[key] as T?
    }
  }

  companion object {
    @JvmStatic
    fun with(): RetrofitManager? {
      return RMProviderManager.with().get()
    }

    @JvmStatic
    fun with(key: String): RetrofitManager {
      return requireNotNull(RMProviderManager.with(key).get()) {
      }
    }

    @JvmStatic
    @Synchronized
    fun registerProvider(factory: Factory) {
      RMProviderManager.registerProvider(factory)
    }

    @JvmStatic
    @Synchronized
    fun registerProvider(key: String, factory: Factory) {
      RMProviderManager.registerProvider(key, factory)
    }

    @JvmStatic
    @Synchronized
    fun unregisterProvider() {
      RMProviderManager.unregisterProvider()
    }

    @JvmStatic
    @Synchronized
    fun unregisterProvider(key: String) {
      RMProviderManager.unregisterProvider(key)
    }

    @JvmStatic
    var isDebug: Boolean
      set(value) {
        RMProviderManager.debug = value
      }
      get() = RMProviderManager.debug

    @JvmStatic var listener: Listener?
      set(value) {
        RMProviderManager.listener = value
      }
      get() = RMProviderManager.listener
  }

}