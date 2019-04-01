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

package com.applivery.applvsdklib.network.api.responses;

import com.applivery.applvsdklib.network.api.model.ErrorEntity;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration") public class ServerResponse<JsonObject> {

  @SerializedName("status") private boolean status;

  @SerializedName("data") private JsonObject data;

  @SerializedName("error") private ErrorEntity error;

  private int httpCode;

  public void setHttpCode(int httpCode) {
    this.httpCode = httpCode;
  }

  public ErrorEntity getError() {
    return error;
  }

  public void setError(ErrorEntity error) {
    this.error = error;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public JsonObject getData() {
    return data;
  }

  public void setData(JsonObject data) {
    this.data = data;
  }
}
