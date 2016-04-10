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

package com.applivery.applvsdklib.tools.androidimplementations;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.ui.model.ScreenCapture;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class ScreenCaptureUtils {

  private static final int COMPRESSION_BITMAP_QUALITY = 100;

  public static ScreenCapture getScreenCapture(Activity activity) {

    try {
      View v1 = activity.getWindow().getDecorView().getRootView();
      v1.setDrawingCacheEnabled(true);
      Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
      v1.setDrawingCacheEnabled(false);

      ByteArrayOutputStream out = new ByteArrayOutputStream();

      bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_BITMAP_QUALITY, out);
      Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

      return new ScreenCapture(decoded);
    } catch (Throwable e) {
      e.printStackTrace();
      return new ScreenCapture(BitmapFactory.decodeResource(null, R.drawable.applivery_icon));
    }
  }

}
