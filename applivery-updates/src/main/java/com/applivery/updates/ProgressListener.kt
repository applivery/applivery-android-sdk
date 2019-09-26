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
package com.applivery.updates

import com.applivery.updates.domain.DownloadInfo
import kotlin.properties.Delegates

// TODO remove this after update to a new view
object ProgressListener {

    var downloadInfo: DownloadInfo by Delegates.observable(DownloadInfo()) { _, _, newValue ->
        onUpdate?.invoke(newValue)
    }

    var onUpdate: ((DownloadInfo) -> Unit)? = null
    var onFinish: (() -> Unit?)? = null

    fun clearListener() {
        onUpdate = null
        onFinish = null
    }
}
