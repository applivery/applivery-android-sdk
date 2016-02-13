package com.applivery.applvsdklib.utils;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 1/1/16.
 */
public class MockAppliveryInstance {

  private final static int ERROR_RESPONSE_CODE = 500;

  public static AppliveryTestApi getApiTestInstance(){
    MockWebServer server = new MockWebServer();
    server.setDispatcher(getMockwebserverDispatcherInstance());
    return initializeApiclient(server);
  }

  public static AppliveryApiService getApiServiceInstance(){
    MockWebServer server = new MockWebServer();
    server.setDispatcher(getMockwebserverDispatcherInstance());
    return initializeApiServiceClient(server);
  }

  private static Dispatcher getMockwebserverDispatcherInstance() {
    return  new Dispatcher() {
      @Override public MockResponse dispatch(RecordedRequest request)
          throws InterruptedException {
        try {

          if (request.getPath().equals("/api/apps/test")) {
            return new MockResponse().setResponseCode(200).setBody(
                TestUtils.getContentFromFile("testOkHttp.json", this));
          } else if (request.getPath().equals("/api/apps/error")) {
            return new MockResponse().setStatus("HTTP/1.1 500 KO")
                .setBody(TestUtils.getContentFromFile("testErrorHttp.json", this));
          } else if (request.getPath().equals("/api/apps/errorBusiness")) {
            return new MockResponse().setResponseCode(200).setBody(
                TestUtils.getContentFromFile("testErrorBusiness.json", this));
          } else if (request.getPath().equals("/api/apps/bad")) {
            return new MockResponse().setResponseCode(ERROR_RESPONSE_CODE).setBody(
                TestUtils.getContentFromFile("testBadHttp.json", this));
          } else if (request.getPath().equals("/api/builds/1/token")) {
            return new MockResponse().setResponseCode(200).setBody(
                TestUtils.getContentFromFile("testOkBuildToken.json", this));
          } else if (request.getPath().equals("/download/1/manifest/2")) {
            return new MockResponse().setResponseCode(200).setBody(
                TestUtils.getContentFromFile("testOkBuild.json", this));
          }else{
            return null;
          }
        } catch (Exception e) {
          e.printStackTrace();
          return new MockResponse().setResponseCode(ERROR_RESPONSE_CODE).setBody("");
        }
      }
    };
  }


  private static AppliveryTestApi initializeApiclient(MockWebServer server) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/").toString())
        .addConverterFactory(GsonConverterFactory.create()).build();
    return retrofit.create(AppliveryTestApi.class);
  }


  private static AppliveryApiService initializeApiServiceClient(MockWebServer server) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/").toString())
        .addConverterFactory(GsonConverterFactory.create()).build();
    return retrofit.create(AppliveryApiService.class);
  }

}
