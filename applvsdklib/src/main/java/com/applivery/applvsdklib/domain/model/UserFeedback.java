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

import com.applivery.applvsdklib.ui.model.ScreenCapture;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class UserFeedback implements Feedback {

  private FeedBackType feedBackType;
  private boolean screenshotAttached;
  private String feedbackMessage;
  private ScreenCapture screenCapture;

  @Override public void setType(FeedBackType feedBackType) {
    this.feedBackType = feedBackType;
  }

  @Override public void attachScreenshot(boolean activated) {
    this.screenshotAttached = activated;
  }

  @Override public boolean mustAttachScreenshot() {
    return screenshotAttached;
  }

  @Override public void setMessage(String feedbackMessage) {
    this.feedbackMessage = feedbackMessage;
  }

  @Override public String getMessage() {
    return feedbackMessage;
  }

  @Override public void setScreenCapture(ScreenCapture screenCapture) {
    this.screenCapture = screenCapture;
  }

  @Override public String getBase64ScreenCapture() {
    return screenCapture.getBase64();
  }

  @Override public FeedBackType getType() {
    return feedBackType;
  }
}
