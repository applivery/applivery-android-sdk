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

package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.network.api.responses.ApiFeedbackResponse;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class ApiFeedbackResponseMapper extends BaseMapper<FeedbackResult, Object> {

  public ApiFeedbackResponseMapper() {
  }

  public BusinessObject map(ApiFeedbackResponse apiFeedbackResponse) {
    return super.map(apiFeedbackResponse);
  }

  @Override protected FeedbackResult mapBusinessObject(Object serverResponse) {
    FeedbackResult feedbackResult = new FeedbackResult(true);
    return feedbackResult;
  }

  //@Override protected AppConfig mapBusinessObject(ApiAppConfigData apiAppConfigData) {
  //
  //  AppConfig appConfig = new AppConfig();
  //
  //  appConfig.setBuildsCount(apiAppConfigData.getBuildsCount());
  //
  //  appConfig.setCreated(DateUtils.stringToDateWithFormat(apiAppConfigData.getCreated(),
  //      DateFormatConstants.DATE_FORMAT));
  //
  //  appConfig.setModified(DateUtils.stringToDateWithFormat(apiAppConfigData.getModified(),
  //      DateFormatConstants.DATE_FORMAT));
  //
  //  appConfig.setDescription(apiAppConfigData.getDescription());
  //  appConfig.setId(apiAppConfigData.getId());
  //  appConfig.setName(apiAppConfigData.getName());
  //  appConfig.setTotalDownloads(apiAppConfigData.getTotalDownloads());
  //  appConfig.setCrashesCount(apiAppConfigData.getCrashesCount());
  //  appConfig.setFeedBackCount(apiAppConfigData.getFeedbackCount());
  //  appConfig.setSitesCount(apiAppConfigData.getSitesCount());
  //  appConfig.setSo(apiAppConfigData.getSo());
  //  appConfig.setTotalDownloads(apiAppConfigData.getTotalDownloads());
  //  appConfig.setSdk(sdkMapper.dataToModel(apiAppConfigData.getSdk()));
  //
  //  return appConfig;
  //}
}
