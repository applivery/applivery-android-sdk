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

import android.widget.Toast;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.model.FeedBackType;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.UserFeedback;
import com.applivery.applvsdklib.ui.model.ScreenCapture;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class UserFeedbackPresenter{

  final FeedbackView feedbackView;
  final Feedback feedback;
  private ScreenCapture screenCapture;

  public UserFeedbackPresenter(FeedbackView feedbackView) {
    this.feedbackView = feedbackView;
    this.feedback = new UserFeedback();
  }

  public void cancelButtonPressed() {
    feedbackView.cleanScreenData();
    feedbackView.dismissFeedBack();
  }

  public void nextButtonPressed() {
    feedbackView.showFeedbackFormView();
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

    if (activated){
      feedbackView.showFeedbackImage();
    }else{
      feedbackView.hideFeedbackImage();
    }
  }

  public void setScreenCapture(ScreenCapture screenCapture) {
    this.screenCapture = screenCapture;
  }

  public ScreenCapture getScreenCapture() {
    return screenCapture;
  }

  public void sendFeedbackInfo(String s) {
    Toast.makeText(AppliverySdk.getCurrentActivity(), "Feedback Ready", Toast.LENGTH_SHORT).show();
  }
}
