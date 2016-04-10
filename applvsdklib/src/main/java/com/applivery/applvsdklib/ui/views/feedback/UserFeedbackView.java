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
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.ui.model.ScreenCapture;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class UserFeedbackView extends DialogFragment implements FeedbackView, View.OnClickListener {

  private static UserFeedbackView userFeedbackView;

  private ImageButton cancelButton;
  private ImageButton nextButton;
  private ImageButton sendButton;

  private LinearLayout feedbackButton;
  private LinearLayout bugButton;

  private ImageView screenshot;
  private ImageView feedbackImage;
  private Switch screenShotSwitch;

  private EditText feedbackMessage;

  private LinearLayout feedbackFormContainer;

  private UserFeedbackPresenter userFeedbackPresenter;

  public UserFeedbackView(){
    this.userFeedbackPresenter = new UserFeedbackPresenter(this);
  }

  public static FeedbackView getInstance(AppliveryApiService appliveryApiService) {
    if (userFeedbackView == null){
      userFeedbackView = new UserFeedbackView();
      userFeedbackView.userFeedbackPresenter.setAppliveryApiService(appliveryApiService);
    }

    return userFeedbackView;
  }

  /**
   * * Using DialogFragment instead of Dialog because DialogFragment is not dismissed in rotation.
   * @param activity
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  /**
   * Overrided in order to get fullScreen dialog
   * @param savedInstanceState
   * @return
   */
  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {

    final RelativeLayout root = new RelativeLayout(getActivity());
    root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    final Dialog dialog = new Dialog(getActivity());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(root);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    return dialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstState) {
    View view = inflater.inflate(R.layout.user_feedback, container);
    setCancelable(false);
    initViewElements(view);
    return view;
  }

  private void initViewElements(View view) {

    cancelButton = (ImageButton) view.findViewById(R.id.applivery_feedback_cancel_button);
    nextButton = (ImageButton) view.findViewById(R.id.applivery_feedback_next_button);
    sendButton = (ImageButton) view.findViewById(R.id.applivery_feedback_send_button);
    feedbackButton = (LinearLayout) view.findViewById(R.id.applivery_tab_button_selector_feedback);
    bugButton = (LinearLayout) view.findViewById(R.id.applivery_tab_button_selector_bug);
    screenshot = (ImageView) view.findViewById(R.id.appliveryScreenShot);
    feedbackImage = (ImageView) view.findViewById(R.id.applivery_feedback_image);
    feedbackMessage = (EditText) view.findViewById(R.id.applivery_feedback_description);
    feedbackFormContainer = (LinearLayout) view.findViewById(R.id.applivery_feedback_form);
    screenShotSwitch = (Switch) view.findViewById(R.id.attach_screenshot_switch);

    initViewState();

    initElementActions();

  }

  private void initViewState() {
    showScreenshotElements();
    userFeedbackPresenter.feedbackButtonPressed();
    setFeedbackButtonSelected();
    screenShotSwitch.setChecked(true);
  }

  @Override public void onClick(View v) {

    if (R.id.applivery_feedback_cancel_button == v.getId()){
      userFeedbackPresenter.cancelButtonPressed();
    }else if(R.id.applivery_feedback_next_button == v.getId()){
      userFeedbackPresenter.nextButtonPressed();
    }else if(R.id.applivery_feedback_send_button == v.getId()){
      userFeedbackPresenter.sendButtonPressed();
    }else if(R.id.applivery_tab_button_selector_feedback == v.getId()){
      userFeedbackPresenter.feedbackButtonPressed();
    }else if(R.id.applivery_tab_button_selector_bug == v.getId()){
      userFeedbackPresenter.bugButtonPressed();
    }else if(R.id.attach_screenshot_switch == v.getId()){
      userFeedbackPresenter.screenshotSwitchPressed(screenShotSwitch.isChecked());
    }
  }

  private void initElementActions() {
    cancelButton.setOnClickListener(this);
    nextButton.setOnClickListener(this);
    sendButton.setOnClickListener(this);
    feedbackButton.setOnClickListener(this);
    bugButton.setOnClickListener(this);
    screenShotSwitch.setOnClickListener(this);
  }

  private void showScreenshotElements() {
    ScreenCapture screenCapture = userFeedbackPresenter.getScreenCapture();
    //NOTE screenshot get lost after screen capture
    if (screenCapture!=null){
      screenshot.setImageBitmap(screenCapture.getScreenShot());
    }
    screenshot.setVisibility(View.VISIBLE);
    nextButton.setVisibility(View.VISIBLE);
  }

  private void hideFormElements() {
    feedbackFormContainer.setVisibility(View.GONE);
    sendButton.setVisibility(View.GONE);
  }

  private void showFormElements() {
    feedbackFormContainer.setVisibility(View.VISIBLE);
    sendButton.setVisibility(View.VISIBLE);
    showFeedbackImage();
  }

  private void hideScreenShotElements() {
    screenshot.setImageResource(android.R.color.transparent);
    screenshot.setVisibility(View.GONE);
    nextButton.setVisibility(View.GONE);
  }

  @Override
  public void show() {
    show(AppliverySdk.getCurrentActivity().getFragmentManager(), "");
  }

  @Override public void showFeedbackFormView() {
    hideScreenShotElements();
    showFormElements();
  }

  @Override public void dismissFeedBack() {
    dismiss();
  }

  @Override public void cleanScreenData() {
    hideFormElements();
    screenShotSwitch.setChecked(true);
    screenshot.setImageResource(android.R.color.transparent);
    feedbackImage.setImageResource(android.R.color.transparent);
    feedbackImage.setVisibility(View.VISIBLE);
    feedbackMessage.getText().clear();
  }

  @Override public void takeDataFromScreen() {
    userFeedbackPresenter.sendFeedbackInfo(feedbackMessage.getText().toString());
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
    feedbackImage.setVisibility(View.VISIBLE);
    ScreenCapture screenCapture = userFeedbackPresenter.getScreenCapture();
    //NOTE screenshot get lost after screen capture
    if (screenCapture!=null) {
      feedbackImage.setImageBitmap(screenCapture.getScreenShot());
    }
  }

  @Override public void hideFeedbackImage() {
    feedbackImage.setImageResource(android.R.color.transparent);
    feedbackImage.setVisibility(View.GONE);
  }

  @Override public void setScreenCapture(ScreenCapture screenCapture) {
    userFeedbackPresenter.setScreenCapture(screenCapture);
  }

  @Override public boolean isNotShowing() {
    if (getDialog() == null){
      return true;
    }else{
      return !getDialog().isShowing();
    }
  }

}