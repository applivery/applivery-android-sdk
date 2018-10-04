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
 * Date 3/1/16.
 */
public class DownloadResult implements BusinessObject<DownloadResult> {

  private final boolean success;
  private final String path;
  private final String error;

  public DownloadResult(boolean status, String path) {
    this.success = status;
    this.path = path;
    this.error = "";
  }

  public DownloadResult(boolean status) {
    this.success = status;
    this.path = "";
    this.error = "";
  }

  public DownloadResult(String error) {
    this.success = false;
    this.path = "";
    this.error = error;
  }

  @Override public DownloadResult getObject() {
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getPath() {
    return path;
  }

  public String getError() {
    return error;
  }
}
