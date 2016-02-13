package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.ObtainBuildDownloadTokenRequest;
import com.applivery.applvsdklib.utils.MockAppliveryInstance;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 1/1/16.
 */
public class ObtainBuildDownloadTokenTest {

  private AppliveryApiService appliveryApiService;

  @Before public void setUp(){
    appliveryApiService = MockAppliveryInstance.getApiServiceInstance();
  }

  @Test public void requestTest(){

    ObtainBuildDownloadTokenRequest obtainBuildDownloadTokenRequest =
        new ObtainBuildDownloadTokenRequest(appliveryApiService, "1");

    BusinessObject<BuildTokenInfo> businessObject = obtainBuildDownloadTokenRequest.execute();

    assertNotNull(businessObject);
    assertNotNull(businessObject.getObject());
    assertTrue(businessObject.getObject() instanceof BuildTokenInfo);

  }

}
