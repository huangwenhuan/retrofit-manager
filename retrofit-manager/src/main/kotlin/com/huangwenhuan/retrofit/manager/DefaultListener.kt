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

internal object DefaultListener : Listener {
  override fun onProviderAdded(key: String, value: RMProvider) {
    Util.log(Util.INFO, String.format("The key [%s] is added", key), null)
  }

  override fun onProviderDuplicate(key: String, value: RMProvider) {
    Util.log(Util.INFO, String.format("The key [%s] is duplicated", key), null)
  }

  override fun onRetrofitManagerSelected(key: String, network: RetrofitManager) {
    Util.log(
      Util.INFO,
      String.format("The key [%s] is selected, RetrofitManager = %s", key, network.toString()),
      null
    )
  }

  override fun onRetrofitManagerCreated(key: String, network: RetrofitManager) {
    Util.log(
      Util.INFO,
      String.format("The key [%s] is selected, RetrofitManager = %s", key, network.toString()),
      null
    )
  }
}
