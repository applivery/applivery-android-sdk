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

package com.applivery.applvsdklib.domain.model;

import com.applivery.applvsdklib.network.api.responses.ApiAppliveryServerErrorResponse;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ErrorObject implements BusinessObject<ErrorObject> {

  private boolean isBusinessError;
  private String message;
  private int businessCode;

  public ErrorObject(ApiAppliveryServerErrorResponse serverResponse) {
    businessCode = serverResponse.getCode();
    message = serverResponse.getMsg();
    isBusinessError = serverResponse.isBusinessError();
  }

  public ErrorObject() {
    businessCode = -99;
    message = "Unknown Error";
    isBusinessError = false;
  }

  public ErrorObject(String message) {
    this.businessCode = -99;
    this.message = message;
    this.isBusinessError = false;
  }

  @Override public ErrorObject getObject() {
    return this;
  }

  public String getMessage() {
    return message;
  }
}
