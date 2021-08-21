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

import com.google.common.annotations.VisibleForTesting
import java.lang.Exception
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

internal object RMStore {
  private val name2RM: ConcurrentMap<String?, in RetrofitManager> = ConcurrentHashMap(4)

  fun generateKey(clazz: Class<out RetrofitManager?>): String {
    val cache = nameCaches.get()
    val retrofitManagerClazz: Class<*> = clazz
    var name: String?
    synchronized(cache) {
      name = cache[retrofitManagerClazz]
      if (name == null) {
        name = generateKey0(retrofitManagerClazz)
        cache[retrofitManagerClazz] = name
      }
    }
    synchronized(this) {
      // It's not very likely for a user to put more than one retrofit-manager of the same type, but make sure to avoid
      // any name conflicts.  Note that we don't cache the names generated here.
      if (name2RM.containsKey(name)) {
        // Strip the trailing '0'.
        val baseName = name!!.substring(0, name!!.length - 1)
        var i = 1
        while (true) {
          val newName = baseName + i
          if (!name2RM.containsKey(newName)) {
            name = newName
            break
          }
          i++
        }
      }
    }
    return requireNotNull(name) { "com.huangwenhuan.retrofit.manager.RMStore#generateKey(${clazz.name}) must note be null" }
  }

  fun put(manager: RetrofitManager) {
    put(manager.key(), manager)
  }

  fun put(name: String?, manager: RetrofitManager) {
    name2RM[name] = manager
  }

  fun putIfAbsent(manager: RetrofitManager) {
    putIfAbsent(manager.key(), manager)
  }

  fun putIfAbsent(name: String?, manager: RetrofitManager) {
    name2RM.putIfAbsent(name, manager)
  }

  fun remove(name: String?) {
    name2RM.remove(name)
  }

  fun <T : RetrofitManager?> findRetrofitManager(name: String?): T? {
    return try {
      name2RM[name] as T?
    } catch (e: Exception) {
      null
    }
  }

  //////
  //////
  //////

  @JvmOverloads
  fun tryReviseName(
    clazz: Class<out RetrofitManager?>,
    name: String? = null
  ): String {
    return if (!name.isNullOrEmpty() && findRetrofitManager<RetrofitManager?>(name) == null) {
      name
    } else {
      generateKey(clazz)
    }
  }

  @VisibleForTesting
  internal fun clear() {
    name2RM.clear()
  }

  private fun generateKey0(builderType: Class<*>): String {
    return Util.simpleClassName(builderType) + "#0"
  }

  private val nameCaches: ThreadLocal<MutableMap<Class<*>, String?>> =
    object : ThreadLocal<MutableMap<Class<*>, String?>>() {
      override fun initialValue(): MutableMap<Class<*>, String?> {
        return WeakHashMap()
      }
    }
}