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

package com.applivery.applvsdklib.ui.views.feedback;

import android.app.Activity;
import com.applivery.applvsdklib.ui.model.ScreenCapture;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public interface FeedbackView {

  void show();

  void showFeedbackFormView();

  void dismissFeedBack();

  void cleanScreenData();

  void takeDataFromScreen();

  void setBugButtonSelected();

  void setFeedbackButtonSelected();

  void showFeedbackImage();

  void hideFeedbackImage();

  boolean isNotShowing();

  void lockRotationOnParentScreen(Activity currentActivity);

  void hideScheenShotPreview();

  void showScheenShotPreview();

  void setScreenCapture(ScreenCapture screenCapture);

  void checkScreenshotSwitch(boolean isChecked);

  void retrieveEditedScreenshot();

  void requestLogin();
}
