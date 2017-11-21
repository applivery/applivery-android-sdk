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

import android.graphics.Bitmap;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.download.permissions.AccessNetworkStatePermission;
import com.applivery.applvsdklib.domain.feedback.FeedbackInteractor;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.FeedBackType;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.UserFeedback;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.tools.androidimplementations.ScreenCaptureUtils;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;
import com.applivery.applvsdklib.ui.model.ScreenCapture;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class UserFeedbackPresenter implements InteractorCallback<FeedbackResult> {

  private final FeedbackView feedbackView;
  private final Feedback feedback;
  private AppliveryApiService appliveryApiService;
  private ScreenCapture screenCapture;
  final private PermissionChecker permissionRequestExecutor;
  final private AccessNetworkStatePermission accessNetworkStatePermission;

  public UserFeedbackPresenter(FeedbackView feedbackView) {
    this.feedbackView = feedbackView;
    this.feedback = new UserFeedback();
    this.permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
    this.accessNetworkStatePermission = new AccessNetworkStatePermission();
  }

  public void setAppliveryApiService(AppliveryApiService appliveryApiService) {
    this.appliveryApiService = appliveryApiService;
  }

  public void setScreenCapture(ScreenCapture screenCapture) {
    this.screenCapture = screenCapture;
  }

  public void initUi() {
    if (screenCapture == null) {
      feedbackView.checkScreenshotSwitch(false);
    } else {
      feedbackView.showFeedbackImage();
      feedbackView.showScheenShotPreview();
      feedbackView.checkScreenshotSwitch(true);
    }
  }

  public void cancelButtonPressed() {
    feedbackView.cleanScreenData();
    feedbackView.dismissFeedBack();
    screenCapture = null;
  }

  public void okButtonPressed() {
    okScreenShotPressed();
  }

  public void sendButtonPressed() {
    feedbackView.takeDataFromScreen();
  }

  public void feedbackButtonPressed() {
    feedback.setType(FeedBackType.FEEDBACK);
    feedbackView.setFeedbackButtonSelected();
  }

  public void bugButtonPressed() {
    feedback.setType(FeedBackType.BUG);
    feedbackView.setBugButtonSelected();
  }

  public void screenshotSwitchPressed(boolean activated) {
    feedback.attachScreenshot(activated);

    if (activated) {
      feedbackView.showFeedbackImage();
    } else {
      feedbackView.hideFeedbackImage();
    }
  }

  public ScreenCapture getScreenCapture() {

    if (screenCapture == null) {
      ScreenCapture screenCapture =
          ScreenCaptureUtils.getScreenCapture(AppliverySdk.getCurrentActivity());
      this.screenCapture = screenCapture;
    }

    return screenCapture;
  }

  public void updateScreenCaptureWith(Bitmap screenshot) {
    if (screenshot == null) {
      AppliverySdk.Logger.log("Cannot update ScreenCapture with a null bitmap.");
      return;
    }

    screenCapture = new ScreenCapture(screenshot);
    feedbackView.showFeedbackImage();
    feedbackView.checkScreenshotSwitch(true);
  }

  public void sendFeedbackInfo(String feedbackMessage, String screen) {
    if (permissionRequestExecutor == null) {
      AppliverySdk.Logger.log(
          "PermissionRequestExecutor must be initialized before accessing network state");
      return;
    }

    feedback.setMessage(feedbackMessage);
    feedback.setScreen(screen);

    if (feedback.mustAttachScreenshot()) {
      feedback.setScreenCapture(screenCapture);
    } else {
      feedback.setScreenCapture(null);
    }

    if (!permissionRequestExecutor.isGranted(accessNetworkStatePermission)) {
      askForPermission();
    } else {
      sendFeedback();
    }
  }

  @Override public void onSuccess(FeedbackResult businessObject) {
    screenCapture = null;
    feedbackView.cleanScreenData();
    feedbackView.dismissFeedBack();
  }

  @Override public void onError(ErrorObject error) {
    ShowErrorAlert showErrorAlert = new ShowErrorAlert();
    showErrorAlert.showError(error);
  }

  public void feedbackImagePressed() {
    feedbackView.showScheenShotPreview();
  }

  public void okScreenShotPressed() {
    feedbackView.retrieveEditedScreenshot();
    feedbackView.hideScheenShotPreview();
  }

  private void askForPermission() {
    permissionRequestExecutor.askForPermission(accessNetworkStatePermission,
        new UserPermissionRequestResponseListener() {
          @Override public void onPermissionAllowed(boolean permissionAllowed) {
            if (permissionAllowed) {
              sendFeedback();
            }
          }
        }, AppliverySdk.getCurrentActivity());
  }

  private void sendFeedback() {
    AppliverySdk.getExecutor()
        .execute(FeedbackInteractor.getInstance(appliveryApiService, feedback, this));
  }
}
