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

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public class ErrorObject {

  private final boolean businessError;
  private final String message;
  private final int businessCode;

  public ErrorObject(boolean businessError, String message, int businessCode) {
    this.businessError = businessError;
    this.message = message;
    this.businessCode = businessCode;
  }

  public ErrorObject() {
    businessCode = -99;
    message = "Unknown Error";
    businessError = false;
  }

  public ErrorObject(String message) {
    this.businessCode = -99;
    this.message = message;
    this.businessError = false;
  }

  public String getMessage() {
    return message;
  }

  public boolean isBusinessError() {
    return businessError;
  }

  public int getBusinessCode() {
    return businessCode;
  }

  public static final int UNAUTHORIZED_ERROR=4004;
  public static final int SUBSCRIPTION_ERROR=5004;
}
