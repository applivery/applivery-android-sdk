/*
 * Copyright (c) 2019 Applivery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applivery.applvsdklib.ui.views.login

import android.app.Activity


class LoginView(
    private val activity: Activity,
    private val onSuccess: () -> Unit
) {

    fun show() {
        val intent = LoginActivity.getIntent(activity)
        LoginCallbacks.add(LoginCallback(onLogin = onSuccess))
        activity.startActivity(intent)
    }
}
