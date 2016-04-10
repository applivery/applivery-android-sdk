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

package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.download.app.DownloadStatusListener;
import com.applivery.applvsdklib.domain.download.app.ExternalStorageWriter;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

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
    DownloadResult downloadResult = new DownloadResult(false);

    Call<ResponseBody> response = apiService.downloadBuild(token.getBuild(), token.getToken());

    try {
      Response<ResponseBody> apiResponse = response.execute();

      int lenght = Integer.parseInt(apiResponse.headers().get("Content-Length"));
      InputStream in = apiResponse.body().byteStream();
      String apkFileName = appName + "_" + token.getBuild();

      String path = externalStorageWriter.writeToFile(in, lenght, downloadStatusListener, apkFileName);

      if (path!=null){
        downloadResult = new DownloadResult(true, path);
      }

    } catch (Exception e) {
      AppliverySdk.Logger.log(e.getMessage());
    }


    return downloadResult;
  }

}
