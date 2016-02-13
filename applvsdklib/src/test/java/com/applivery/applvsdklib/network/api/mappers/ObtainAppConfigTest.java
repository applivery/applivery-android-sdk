package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.AppConfig;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.Sdk;
import com.applivery.applvsdklib.network.api.requests.ObtainAppConfigRequest;
import com.applivery.applvsdklib.network.api.requests.mappers.ApiAppConfigResponseMapper;
import com.applivery.applvsdklib.network.api.requests.mappers.SdkMapper;
import com.applivery.applvsdklib.network.api.responses.ApiAppConfigResponse;
import com.applivery.applvsdklib.network.api.model.ApiSdK;
import com.applivery.applvsdklib.builders.ApiAppConfigDataBuilder;
import com.applivery.applvsdklib.utils.MockAppliveryInstance;

import java.util.Calendar;
import java.util.Date;
import org.mockito.runners.MockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.applivery.applvsdklib.matchers.IsDateEqualTo.isDateEqualTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 1/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ObtainAppConfigTest {

  @Mock SdkMapper sdkMapper;
  @Mock Sdk sdk;

  private AppliveryApiService appliveryApiService;

  @Before public void setUp(){
    appliveryApiService = MockAppliveryInstance.getApiServiceInstance();
  }

  @Test public void requestTest(){

    ObtainAppConfigRequest obtainAppConfigRequest =
        new ObtainAppConfigRequest(appliveryApiService, "test", "TEST_TOKEN");

    BusinessObject businessObject = obtainAppConfigRequest.execute();

    assertNotNull(businessObject);

  }

  @Test public void mapApiAppConfigResponseTest(){

    when(sdkMapper.dataToModel(isA(ApiSdK.class))).thenReturn(sdk);

    ApiAppConfigResponse apiAppConfigResponse = new ApiAppConfigResponse();
    apiAppConfigResponse.setData(ApiAppConfigDataBuilder.Builder().build());
    apiAppConfigResponse.setStatus(true);

    ApiAppConfigResponseMapper apiAppConfigResponseMapper = new ApiAppConfigResponseMapper(sdkMapper);
    AppConfig appConfig  = (AppConfig) apiAppConfigResponseMapper.map(apiAppConfigResponse);

    assertEquals(appConfig.getBuildsCount(), ApiAppConfigDataBuilder.BUILDS_COUNT);
    assertEquals(appConfig.getDescription(), ApiAppConfigDataBuilder.DESCRIPTION);
    assertEquals(appConfig.getId(), ApiAppConfigDataBuilder.ID);
    assertEquals(appConfig.getName(), ApiAppConfigDataBuilder.NAME);
    assertEquals(appConfig.getSo(), ApiAppConfigDataBuilder.SO);
    assertEquals(appConfig.getTotalDownloads(), ApiAppConfigDataBuilder.TOTAL_DOWNLOADS_COUNT);
    assertEquals(appConfig.getCrashesCount(), ApiAppConfigDataBuilder.CRASHES_COUNT);
    assertEquals(appConfig.getFeedBackCount(), ApiAppConfigDataBuilder.FEEDBACK_COUNT);
    assertEquals(appConfig.getSitesCount(), ApiAppConfigDataBuilder.SITES_COUNT);

    assertEquals(appConfig.getSdk(), sdk);

    Date created = getCalendar(2015, Calendar.OCTOBER, 21, 11, 46, 13);
    Date modified = getCalendar(2015, Calendar.OCTOBER, 21, 18, 41, 39);

    assertThat(created, isDateEqualTo(appConfig.getCreated()));
    assertThat(modified, isDateEqualTo(appConfig.getModified()));

  }

  private Date getCalendar(int year, int month, int day, int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hour, minute, second);
    return calendar.getTime();
  }

}
