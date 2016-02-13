package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidExternalStorageWriterImpl;
import com.squareup.okhttp.ResponseBody;
import java.io.InputStream;
import retrofit.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class DownloadBuildRequest extends ServerRequest {

  private final AppliveryApiService apiService;
  private final BuildTokenInfo token;
  private final DownloadStatusListener downloadStatusListener;
  private final ExternalStorageWriter externalStorageWriter;
  private final String appName;

  public DownloadBuildRequest(AppliveryApiService apiService, BuildTokenInfo token, String appName,
      DownloadStatusListener downloadStatusListener, ExternalStorageWriter externalStorageWriter) {

    this.externalStorageWriter = externalStorageWriter;

    this.downloadStatusListener = downloadStatusListener;
    this.apiService = apiService;
    this.token = token;
    this.appName = appName;
  }

  @Override protected BusinessObject performRequest() {
    DownloadResult downloadResult;

    Call<ResponseBody> response = apiService.downloadBuild(token.getBuild(), token.getToken());

    try {
      retrofit.Response<ResponseBody> apiResponse = response.execute();

      int lenght = Integer.parseInt(apiResponse.headers().get("Content-Length"));
      InputStream in = apiResponse.body().byteStream();
      String fileName = appName + "_" + token.getBuild();

      String path = externalStorageWriter.writeToFile(in, lenght, downloadStatusListener, fileName);

      downloadResult = new DownloadResult(true, path);
    } catch (Exception e) {
      downloadResult = new DownloadResult(false);
      e.printStackTrace();
    }

    return downloadResult;
  }

}
