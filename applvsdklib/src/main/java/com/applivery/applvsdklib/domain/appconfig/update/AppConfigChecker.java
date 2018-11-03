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

package com.applivery.applvsdklib.domain.appconfig.update;

import com.applivery.applvsdklib.AppliverySdk;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 11/1/16.
 */
public class AppConfigChecker {

  private final LastConfigReader lastConfigReader;

  public AppConfigChecker(LastConfigReader lastConfigReader) {
    this.lastConfigReader = lastConfigReader;
  }

  public boolean shouldCheckAppConfigForUpdate() {

    if (AppliverySdk.isSdkFirstTime()) {
      AppliverySdk.setSdkFirstTimeFalse();
      return false;
    }

    if (AppliverySdk.isSdkRestarted()) {
      AppliverySdk.setSdkRestartedFalse();
      return true;
    }

    if (lastConfigReader.notExistsLastConfig()) {
      return true;
    }

    return AppliverySdk.getCheckForUpdatesBackground();
  }
}
