/*
 * Copyright (c) 2016 Applivery
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

package com.applivery.applvsdklib.network.api.requests

import com.applivery.applvsdklib.domain.model.BusinessObject
import com.applivery.applvsdklib.domain.model.FeedbackWrapper
import com.applivery.applvsdklib.network.api.AppliveryApiService
import com.applivery.applvsdklib.network.api.requests.mappers.ApiDeviceInfoRequestMapper
import com.applivery.applvsdklib.network.api.requests.mappers.ApiDeviceRequestMapper
import com.applivery.applvsdklib.network.api.requests.mappers.ApiFeedbackRequestMapper
import com.applivery.applvsdklib.network.api.requests.mappers.ApiFeedbackResponseMapper
import com.applivery.applvsdklib.network.api.requests.mappers.ApiOsRequestMapper
import com.applivery.applvsdklib.network.api.requests.mappers.ApiPackageInfoRequestMapper

class FeedbackRequest(private val apiService: AppliveryApiService,
    private val feedbackWrapper: FeedbackWrapper) : ServerRequest() {

  private val apiFeedbackRequestMapper = createMappers()
  private val apiFeedbackResponseMapper: ApiFeedbackResponseMapper = ApiFeedbackResponseMapper()

  private fun createMappers(): ApiFeedbackRequestMapper {

    val apiOsRequestMapper = ApiOsRequestMapper()
    val apiDeviceRequestMapper = ApiDeviceRequestMapper()

    val apiDeviceInfoRequestMapper = ApiDeviceInfoRequestMapper(apiDeviceRequestMapper,
        apiOsRequestMapper)

    val apiPackageInfoRequestMapper = ApiPackageInfoRequestMapper()

    return ApiFeedbackRequestMapper(apiPackageInfoRequestMapper, apiDeviceInfoRequestMapper)
  }

  override fun performRequest(): BusinessObject<*> {
    val apiFeedbackData = apiFeedbackRequestMapper.modelToData(feedbackWrapper)
    val response = apiService.sendFeedback(apiFeedbackData)
    val apiFeedbackResponse = super.performRequest(response)
    return apiFeedbackResponseMapper.map(apiFeedbackResponse)
  }
}
