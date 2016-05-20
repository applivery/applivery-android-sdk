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

package com.applivery.applvsdklib.doubles;

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.requests.ServerRequest;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import com.applivery.applvsdklib.utils.AppliveryTestApi;
import retrofit2.Call;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
public class MockRequest extends ServerRequest {

    private AppliveryTestApi apiService;

    @Override protected BusinessObject performRequest() {
      // empty. This class is created just with testing purposes
      return null;
    }

  public <T extends ServerResponse> T performSuperRequest(Call<T> call){
    return super.performRequest(call);
  }


}
