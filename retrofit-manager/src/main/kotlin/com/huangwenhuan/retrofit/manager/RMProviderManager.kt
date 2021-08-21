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

import com.huangwenhuan.retrofit.manager.RetrofitManager.Listener
import com.huangwenhuan.retrofit.manager.RMProvider.Factory
import java.lang.Exception
import java.util.HashMap

internal object RMProviderManager {
  private const val default_key = "retrofit.manager.DEFAULT_KEY"

  private val providers = HashMap<String, RMProvider>(8)
  internal var debug = false
  internal var listener: Listener? = DefaultListener

  @JvmOverloads
  fun with(key: String = default_key): RMProvider = getProvider(key)

  @Synchronized fun registerProvider(factory: Factory?) {
    registerProvider(default_key, factory)
  }

  @Synchronized fun registerProvider(key: String, factory: Factory?) {
    val provider = RMProvider(key, factory!!, RMStore, this)
    putIfAbsent(key, provider)
  }

  @Synchronized fun unregisterProvider() {
    unregisterProvider(default_key)
  }

  @Synchronized fun unregisterProvider(key: String) {
    providers.remove(key)
    RMStore.remove(key)
  }

  private fun getProvider(key: String): RMProvider {
    return checkNotNull(providers[key]) {}
  }

  private fun putIfAbsent(key: String, value: RMProvider): RMProvider? {
    val target = providers
    var v = target[key]
    if (v == null) {
      v = target.put(key, value)
      onKeyAdded(key, value)
    } else {
      try {
        onKeyDuplicate(key, value)
      } catch (e: Exception) {
      }
    }
    return v
  }

  fun onKeyAdded(key: String, value: RMProvider) {
    if (debug) listener?.let { it.onProviderAdded(key, value) }
  }

  fun onKeyDuplicate(key: String, value: RMProvider) {
    if (debug) listener?.let { it.onProviderDuplicate(key, value) }
  }

  fun onNNetworkSelected(key: String, network: RetrofitManager) {
    if (debug) listener?.let { it.onNNetworkSelected(key, network) }
  }

  fun onNNetworkCreated(key: String, network: RetrofitManager) {
    if (debug) listener?.let { it.onNNetworkCreated(key, network) }
  }

}