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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.tools.injection.Injection;
import com.applivery.applvsdklib.ui.model.ScreenCapture;
import com.applivery.applvsdklib.ui.views.DrawingImageView;
import com.applivery.applvsdklib.ui.views.login.LoginView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class UserFeedbackView extends DialogFragment
    implements FeedbackView, View.OnClickListener {

  private static UserFeedbackView userFeedbackView;

  private ImageButton cancelButton;
  private ImageButton okButton;
  private ImageButton sendButton;

  private LinearLayout feedbackButton;
  private LinearLayout bugButton;

  private DrawingImageView screenshot;
  private ImageView feedbackImage;
  private SwitchCompat screenShotSwitch;

  private AppCompatEditText feedbackMessage;

  private LinearLayout feedbackFormContainer;

  private UserFeedbackPresenter userFeedbackPresenter;

  public UserFeedbackView() {
    this.userFeedbackPresenter = Injection.INSTANCE.provideFeedbackPresenter(this);
  }

  public static FeedbackView getInstance(AppliveryApiService appliveryApiService) {
    if (userFeedbackView == null) {
      userFeedbackView = new UserFeedbackView();
      userFeedbackView.userFeedbackPresenter.setAppliveryApiService(appliveryApiService);
    }

    return userFeedbackView;
  }

  /**
   * * Using DialogFragment instead of Dialog because DialogFragment is not dismissed in rotation.
   */
  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  /**
   * Overrided in order to get fullScreen dialog
   */
  @Override public Dialog onCreateDialog(final Bundle savedInstanceState) {

    final RelativeLayout root = new RelativeLayout(getActivity());
    root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));

    final Dialog dialog = new Dialog(getActivity());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(root);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
    dialog.getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    return dialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstState) {
    View view = inflater.inflate(R.layout.applivery_user_feedback, container);
    setCancelable(false);
    initViewElements(view);
    return view;
  }

  private void initViewElements(View view) {

    cancelButton = (ImageButton) view.findViewById(R.id.applivery_feedback_cancel_button);
    okButton = (ImageButton) view.findViewById(R.id.applivery_feedback_ok_button);
    sendButton = (ImageButton) view.findViewById(R.id.applivery_feedback_send_button);
    feedbackButton = (LinearLayout) view.findViewById(R.id.applivery_tab_button_selector_feedback);
    bugButton = (LinearLayout) view.findViewById(R.id.applivery_tab_button_selector_bug);
    screenshot = (DrawingImageView) view.findViewById(R.id.appliveryScreenShot);
    feedbackImage = (ImageView) view.findViewById(R.id.applivery_feedback_image);
    feedbackMessage = (AppCompatEditText) view.findViewById(R.id.applivery_feedback_description);
    feedbackFormContainer = (LinearLayout) view.findViewById(R.id.applivery_feedback_form);
    screenShotSwitch = (SwitchCompat) view.findViewById(R.id.attach_screenshot_switch);

    initViewState();

    initElementActions();
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private void initViewState() {
    okButton.setVisibility(View.GONE);
    sendButton.setVisibility(View.VISIBLE);
    initFeedbackMessageCustomStyle();
    showFeedbackFormView();
    userFeedbackPresenter.initUi();
    userFeedbackPresenter.feedbackButtonPressed();
  }

  @Override public void onClick(View v) {

    if (R.id.applivery_feedback_cancel_button == v.getId()) {
      userFeedbackPresenter.cancelButtonPressed();
    } else if (R.id.applivery_feedback_ok_button == v.getId()) {
      userFeedbackPresenter.okButtonPressed();
    } else if (R.id.applivery_feedback_send_button == v.getId()) {
      userFeedbackPresenter.sendButtonPressed();
    } else if (R.id.applivery_tab_button_selector_feedback == v.getId()) {
      userFeedbackPresenter.feedbackButtonPressed();
    } else if (R.id.applivery_tab_button_selector_bug == v.getId()) {
      userFeedbackPresenter.bugButtonPressed();
    } else if (R.id.attach_screenshot_switch == v.getId()) {
      userFeedbackPresenter.screenshotSwitchPressed(screenShotSwitch.isChecked());
    } else if (R.id.applivery_feedback_image == v.getId()) {
      userFeedbackPresenter.feedbackImagePressed();
    }
  }

  private void initFeedbackMessageCustomStyle() {
    int appliveryPrimaryColor =
        getActivity().getResources().getColor(R.color.applivery_primary_color);
    ColorStateList colorStateList = ColorStateList.valueOf(appliveryPrimaryColor);
    feedbackMessage.setSupportBackgroundTintList(colorStateList);
  }

  private void initElementActions() {
    cancelButton.setOnClickListener(this);
    okButton.setOnClickListener(this);
    sendButton.setOnClickListener(this);
    feedbackButton.setOnClickListener(this);
    bugButton.setOnClickListener(this);
    screenShotSwitch.setOnClickListener(this);
    feedbackImage.setOnClickListener(this);
  }

  @Override public void show() {
    FragmentManager fragmentManager = AppliverySdk.getCurrentActivity().getFragmentManager();
    fragmentManager.beginTransaction().add(this, "").commitAllowingStateLoss();
  }

  @Override public void showFeedbackFormView() {
    feedbackImage.setVisibility(View.GONE);
    feedbackImage.setImageResource(android.R.color.transparent);
  }

  @Override public void dismissFeedBack() {
    dismiss();
    AppliverySdk.unlockRotation();
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) @Override public void cleanScreenData() {
    screenShotSwitch.setChecked(false);
    screenshot.setImageResource(android.R.color.transparent);
    okButton.setVisibility(View.GONE);
    sendButton.setVisibility(View.VISIBLE);
    feedbackImage.setImageResource(android.R.color.transparent);
    feedbackImage.setVisibility(View.GONE);
    feedbackMessage.getText().clear();
  }

  @Override public void takeDataFromScreen() {
    userFeedbackPresenter.sendFeedbackInfo(feedbackMessage.getText().toString(),
        getActivity().getLocalClassName());
  }

  @Override public void setBugButtonSelected() {
    feedbackButton.setBackgroundResource(R.drawable.applivery_tab_button_selector);
    bugButton.setBackgroundResource(R.drawable.applivery_selected_tab_button);
  }

  @Override public void setFeedbackButtonSelected() {
    feedbackButton.setBackgroundResource(R.drawable.applivery_selected_tab_button);
    bugButton.setBackgroundResource(R.drawable.applivery_tab_button_selector);
  }

  @Override public void showFeedbackImage() {
    ScreenCapture screenCapture = userFeedbackPresenter.getScreenCapture();
    if (screenCapture != null) {
      feedbackImage.setVisibility(View.VISIBLE);
      feedbackImage.setImageBitmap(screenCapture.getScreenShot());
    }
  }

  @Override public void hideFeedbackImage() {
    feedbackImage.setImageResource(android.R.color.transparent);
    feedbackImage.setVisibility(View.GONE);
  }

  @Override public boolean isNotShowing() {
    if (getDialog() == null) {
      return true;
    } else {
      return !getDialog().isShowing();
    }
  }

  @Override public void lockRotationOnParentScreen(Activity currentActivity) {
    AppliverySdk.lockRotationToPortrait();
  }

  @Override public void hideScheenShotPreview() {
    screenshot.setImageResource(android.R.color.transparent);
    screenshot.setVisibility(View.GONE);
    okButton.setVisibility(View.GONE);
    sendButton.setVisibility(View.VISIBLE);
  }

  @Override public void showScheenShotPreview() {
    ScreenCapture screenCapture = userFeedbackPresenter.getScreenCapture();
    if (screenCapture != null) {
      screenshot.setVisibility(View.VISIBLE);
      screenshot.setImageBitmap(screenCapture.getScreenShot());
    }
    sendButton.setVisibility(View.GONE);
    okButton.setVisibility(View.VISIBLE);
  }

  @Override public void setScreenCapture(ScreenCapture screenCapture) {
    userFeedbackPresenter.setScreenCapture(screenCapture);
  }

  @Override public void checkScreenshotSwitch(boolean isChecked) {
    screenShotSwitch.setChecked(isChecked);
  }

  @Override public void retrieveEditedScreenshot() {
    Bitmap retrievedScreenshot = screenshot.getBitmap();
    userFeedbackPresenter.updateScreenCaptureWith(retrievedScreenshot);
  }

  @Override public void requestLogin() {
    LoginView loginView = new LoginView(AppliverySdk.getCurrentActivity(), new Function0<Unit>() {
      @Override public Unit invoke() {
        takeDataFromScreen();
        return null;
      }
    });
    loginView.getPresenter().requestLogin();
  }
}