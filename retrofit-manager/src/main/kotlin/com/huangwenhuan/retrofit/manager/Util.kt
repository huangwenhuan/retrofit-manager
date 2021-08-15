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

import java.util.logging.Level
import java.util.logging.Logger

internal object Util {
  private val LOGGER = Logger.getLogger(RetrofitManager::class.java.name)
  private const val PACKAGE_SEPARATOR_CHAR = '.'
  const val INFO = 4
  const val WARN = 5

  /**
   * The shortcut to [simpleClassName(o.getClass())][.simpleClassName].
   */
  fun simpleClassName(o: Any?): String {
    return if (o == null) {
      "null_object"
    } else {
      simpleClassName(o.javaClass)
    }
  }

  /**
   * Generates a simplified name from a [Class].  Similar to [Class.getSimpleName],
   * but it works fine
   * with anonymous classes.
   */
  fun simpleClassName(clazz: Class<*>?): String {
    val className = checkNotNull(clazz, "clazz").name
    val lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR)
    return if (lastDotIdx > -1) {
      className.substring(lastDotIdx + 1)
    } else className
  }

  /**
   * Checks that the given argument is not null. If it is, throws [NullPointerException].
   * Otherwise, returns the argument.
   */
  fun <T> checkNotNull(arg: T?, text: String?): T {
    if (arg == null) {
      throw NullPointerException(text)
    }
    return arg
  }

  /**
   * Returns true if the string is null or 0-length.
   *
   * @param str the string to be examined
   * @return true if str is null or zero length
   */
  fun isEmpty(str: CharSequence?): Boolean {
    return str == null || str.length == 0
  }

  fun log(level: Int, message: String?, t: Throwable?) {
    val logLevel = if (level == WARN) Level.WARNING else Level.INFO
    LOGGER.log(logLevel, message, t)
  }
}
