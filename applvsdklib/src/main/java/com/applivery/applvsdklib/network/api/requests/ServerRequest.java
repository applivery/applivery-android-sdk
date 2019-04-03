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

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.model.ErrorDataEntityResponse;
import com.applivery.applvsdklib.network.api.model.ErrorEntity;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public abstract class ServerRequest {

  public final BusinessObject execute() {
    try {
      return performRequest();
    } catch (RequestHttpException re) {
      return re.getErrorEntity().toErrorObject();
    }
  }

  protected abstract BusinessObject performRequest();

  public <T extends ServerResponse> T performRequest(Call<T> call) {

    Response<T> apiResponse = null;

    try {

      apiResponse = call.execute();

      if (apiResponse.isSuccessful()) {
        T response = apiResponse.body();
        response.setHttpCode(apiResponse.code());
        return response;
      } else {

        ErrorEntity error = ErrorDataEntityResponse.Companion.parseError(apiResponse);
        throw new RequestHttpException(error);
      }
    } catch (IOException exception) {
      throw new RequestHttpException(new ErrorEntity(0, "Exception", null));
    }
  }
}
