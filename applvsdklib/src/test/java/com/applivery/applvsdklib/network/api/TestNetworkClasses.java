package com.applivery.applvsdklib.network.api;

import android.content.Context;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.requests.RequestHttpException;
import com.applivery.applvsdklib.network.api.responses.ApiAppliveryServerErrorResponse;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import com.applivery.applvsdklib.network.api.model.ApiAppConfigData;
import com.applivery.applvsdklib.doubles.MockRequest;
import com.applivery.applvsdklib.doubles.MockRequestExecute;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo;
import com.applivery.applvsdklib.utils.AppliveryTestApi;
import com.applivery.applvsdklib.utils.MockAppliveryInstance;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;

import static org.junit.Assert.*;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
public class TestNetworkClasses {

  private AppliveryTestApi appliveryApiTest;

  @Before public void setUp()throws Exception{
    appliveryApiTest = MockAppliveryInstance.getApiTestInstance();
  }

  @Test
  public void apiServiceBuilderTest(){
    Context applicationContext = null;
    AppliveryApiService appliveryApi = AppliveryApiServiceBuilder.getAppliveryApiInstance(
        new AndroidCurrentAppInfo(applicationContext));
    assertNotNull(appliveryApi);
  }

  @Test
  public void serverRequestOkTest(){
    MockRequest mockRequest = new MockRequest();
    Call call = appliveryApiTest.obtainAppConfig("test");
    ServerResponse resp = mockRequest.performSuperRequest(call);
    assertNotNull(resp);
    assertNotNull(resp.getData());
    assertNull(resp.getError());
    assertTrue(resp.getData() instanceof ApiAppConfigData);
  }

  @Test
  public void serverRequestErrorBusinessTest(){
    MockRequest mockRequest = new MockRequest();
    Call call = appliveryApiTest.obtainAppConfig("errorBusiness");
    ServerResponse resp;

    try {
      resp = mockRequest.performSuperRequest(call);
    }catch (RequestHttpException re){
      resp = re.getServerResponse();
    }

    assertNotNull(resp);
    assertNull(resp.getData());
    assertNotNull(resp.getError());
    assertTrue(resp.getError() instanceof ApiAppliveryServerErrorResponse);
    assertTrue(resp.getError().getCode()>0);
  }


  @Test
  public void serverRequestHttpErrorTest(){
    MockRequest mockRequest = new MockRequest();
    Call call = appliveryApiTest.obtainAppConfig("error");
    ServerResponse resp;

    try {
      resp = mockRequest.performSuperRequest(call);
    }catch (RequestHttpException re){
      resp = re.getServerResponse();
    }

    assertNotNull(resp);
    assertNull(resp.getData());
    assertNotNull(resp.getError());
    assertTrue(resp.getError() instanceof ApiAppliveryServerErrorResponse);
    assertTrue(resp.getError().getCode() > 0);
  }

  @Test
  public void serverRequestHttpBadTest(){
    MockRequest mockRequest = new MockRequest();
    Call call = appliveryApiTest.obtainAppConfig("bad");
    ServerResponse resp;

    try {
      resp = mockRequest.performSuperRequest(call);
    }catch (RequestHttpException re){
      resp = re.getServerResponse();
    }

    assertNotNull(resp);
    assertNull(resp.getData());
    assertNotNull(resp.getError());
    assertTrue(resp.getError() instanceof ApiAppliveryServerErrorResponse);
    assertTrue(resp.getError().getCode() > 0);
  }

  @Test
  public void serverRequestExecutorHttpBadTest(){
    MockRequestExecute mockRequest = new MockRequestExecute(appliveryApiTest,"bad");

    BusinessObject businessObject = mockRequest.execute();

    assertNotNull(businessObject);

  }

  @Test
  public void serverRequestExecutorBusinessErrorTest(){
    MockRequestExecute mockRequest = new MockRequestExecute(appliveryApiTest,"errorBusiness");

    BusinessObject businessObject = mockRequest.execute();

    assertNotNull(businessObject);
  }

  @Test
  public void serverRequestExecutorOkTest(){
    MockRequestExecute mockRequest = new MockRequestExecute(appliveryApiTest,"test");

    BusinessObject businessObject = mockRequest.execute();

    assertNotNull(businessObject);
  }

}
