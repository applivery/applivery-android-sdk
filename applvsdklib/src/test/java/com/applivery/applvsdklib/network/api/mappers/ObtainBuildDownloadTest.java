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

package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.DownloadBuildRequest;
import com.applivery.applvsdklib.domain.download.app.DownloadStatusListener;
import com.applivery.applvsdklib.domain.download.app.ExternalStorageWriter;
import com.applivery.applvsdklib.utils.MockAppliveryInstance;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 1/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ObtainBuildDownloadTest {

  @Mock DownloadStatusListener downloadStatusListener;
  @Mock ExternalStorageWriter externalStorageWriter;
  @Mock BuildTokenInfo buildTokenInfo;

  private AppliveryApiService appliveryApiService;

  @Before public void setUp(){
    appliveryApiService = MockAppliveryInstance.getApiServiceInstance();
  }

  @Test public void requestOkTest() throws IOException {

    when(buildTokenInfo.getToken()).thenReturn("2");
    when(buildTokenInfo.getBuild()).thenReturn("1");
    when(externalStorageWriter.writeToFile(isA(InputStream.class), anyInt(),
        isA(DownloadStatusListener.class), isA(String.class))).thenReturn("PATH_TO_FILE");


    DownloadBuildRequest downloadBuildRequest =
        new DownloadBuildRequest(appliveryApiService, buildTokenInfo, "1",
            downloadStatusListener, externalStorageWriter);

    BusinessObject<DownloadResult> businessObject = downloadBuildRequest.execute();

    assertNotNull(businessObject);
    assertNotNull(businessObject.getObject());
    assertTrue(businessObject.getObject() instanceof DownloadResult);
    assertEquals(businessObject.getObject().getPath(), "PATH_TO_FILE");

  }


  @Test
  public void requestKOTest() throws Exception {

    when(buildTokenInfo.getToken()).thenReturn("2");
    when(buildTokenInfo.getBuild()).thenReturn("1");
    when(externalStorageWriter.writeToFile(isA(InputStream.class), anyInt(),
        isA(DownloadStatusListener.class), isA(String.class))).thenThrow(Exception.class);


    DownloadBuildRequest downloadBuildRequest =
        new DownloadBuildRequest(appliveryApiService, buildTokenInfo, "1",
            downloadStatusListener, externalStorageWriter);

    BusinessObject<DownloadResult> businessObject = downloadBuildRequest.execute();

    assertNotNull(businessObject);
    assertNotNull(businessObject.getObject());
    assertTrue(businessObject.getObject() instanceof DownloadResult);
    assertFalse(businessObject.getObject().isSuccess());

  }

}
