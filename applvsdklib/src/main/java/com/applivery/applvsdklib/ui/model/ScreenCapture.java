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

import android.graphics.Bitmap;
import com.applivery.applvsdklib.tools.utils.ImageUtils;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class ScreenCapture {

  private final Bitmap screenShot;

  public ScreenCapture(Bitmap screenShot) {
    this.screenShot = screenShot;
  }

  public Bitmap getScreenShot() {
    return screenShot;
  }

  public String getBase64() {
    return ImageUtils.getBase64(screenShot);
  }
}
