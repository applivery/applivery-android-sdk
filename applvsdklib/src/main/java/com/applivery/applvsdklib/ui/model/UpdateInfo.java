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

package com.applivery.applvsdklib.ui.model;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class UpdateInfo {

  private String appName;
  private String appUpdateMessage;

  public UpdateInfo(String appName, String appUpdateMessage) {
    this.appName = appName;
    this.appUpdateMessage = appUpdateMessage;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppUpdateMessage() {
    return appUpdateMessage;
  }

  public void setAppUpdateMessage(String appUpdateMessage) {
    this.appUpdateMessage = appUpdateMessage;
  }
}
