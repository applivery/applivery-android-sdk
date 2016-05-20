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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class ApiAppliveryServerErrorResponse {

  public static final short NO_CONNECTION_HTTP_CODE = -123;
  public static final String NO_CONNECTION_HTTP_MSG = "No connection";

  private boolean isBusinessError = true;

  @Expose @SerializedName("code") private int code;

  @Expose @SerializedName("msg") private String msg;

  public ApiAppliveryServerErrorResponse(int code, String msg, boolean isBusinessError) {
    this.code = code;
    this.msg = msg;
    this.isBusinessError = isBusinessError;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public static ApiAppliveryServerErrorResponse createNoConnectionErrorInstance(String message) {
    return new ApiAppliveryServerErrorResponse(NO_CONNECTION_HTTP_CODE, message, false);
  }

  public static ApiAppliveryServerErrorResponse createErrorInstance(int code, String message) {
    return new ApiAppliveryServerErrorResponse(code, message, false);
  }

  public boolean isBusinessError() {
    return isBusinessError;
  }

  public boolean isConnectionError() {
    return (code == NO_CONNECTION_HTTP_CODE);
  }
}
