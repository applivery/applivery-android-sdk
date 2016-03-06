package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.model.ApiAppConfigData;
import com.applivery.applvsdklib.network.api.model.DateFormatConstants;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.tools.utils.DateUtils;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ApiAppConfigResponseMapper extends BaseMapper<AppConfig, ApiAppConfigData> {

  private SdkMapper sdkMapper;

  public ApiAppConfigResponseMapper(SdkMapper sdkMapper) {
    this.sdkMapper = sdkMapper;
  }

  public BusinessObject map(ApiAppConfigResponse apiAppConfigResponse) {
    return super.map(apiAppConfigResponse);
  }

  @Override protected AppConfig mapBusinessObject(ApiAppConfigData apiAppConfigData) {

    AppConfig appConfig = new AppConfig();

    appConfig.setBuildsCount(apiAppConfigData.getBuildsCount());

    appConfig.setCreated(DateUtils.stringToDateWithFormat(apiAppConfigData.getCreated(),
        DateFormatConstants.DATE_FORMAT));

    appConfig.setModified(DateUtils.stringToDateWithFormat(apiAppConfigData.getModified(),
        DateFormatConstants.DATE_FORMAT));

    appConfig.setDescription(apiAppConfigData.getDescription());
    appConfig.setId(apiAppConfigData.getId());
    appConfig.setName(apiAppConfigData.getName());
    appConfig.setTotalDownloads(apiAppConfigData.getTotalDownloads());
    appConfig.setCrashesCount(apiAppConfigData.getCrashesCount());
    appConfig.setFeedBackCount(apiAppConfigData.getFeedbackCount());
    appConfig.setSitesCount(apiAppConfigData.getSitesCount());
    appConfig.setSo(apiAppConfigData.getSo());
    appConfig.setTotalDownloads(apiAppConfigData.getTotalDownloads());
    appConfig.setSdk(sdkMapper.dataToModel(apiAppConfigData.getSdk()));

    return appConfig;
  }
}
