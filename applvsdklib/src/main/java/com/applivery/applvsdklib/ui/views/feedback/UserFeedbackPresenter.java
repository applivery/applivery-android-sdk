/*
 * Copyright (c) 2019 Applivery
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
import android.text.TextUtils;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.download.permissions.AccessNetworkStatePermission;
import com.applivery.applvsdklib.domain.login.GetUserProfileInteractor;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.FeedBackType;
import com.applivery.applvsdklib.domain.model.UserFeedback;
import com.applivery.applvsdklib.features.feedback.FeedbackUseCase;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidDeviceDetailsInfo;
import com.applivery.applvsdklib.tools.androidimplementations.ScreenCaptureUtils;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;
import com.applivery.applvsdklib.ui.model.ScreenCapture;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;
import com.applivery.base.AppliveryDataManager;
import com.applivery.base.domain.SessionManager;
import com.applivery.base.domain.model.AppData;
import com.applivery.base.domain.model.DeviceInfo;
import com.applivery.base.domain.model.Feedback;
import com.applivery.base.domain.model.PackageInfo;
import com.applivery.base.domain.model.UserProfile;
import com.applivery.base.util.AndroidCurrentAppInfo;
import com.applivery.base.util.AppliveryLog;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/4/16.
 */
public class UserFeedbackPresenter {

    private final FeedbackView feedbackView;
    private final UserFeedback userFeedback;
    final private PermissionChecker permissionRequestExecutor;
    final private AccessNetworkStatePermission accessNetworkStatePermission;
    private final SessionManager sessionManager;
    private final FeedbackUseCase feedbackUseCase;
    private final GetUserProfileInteractor getUserProfileInteractor;
    private final MailValidator mailValidator = new MailValidator();
    private ScreenCapture screenCapture;
    private String email;

    public UserFeedbackPresenter(
            FeedbackView feedbackView,
            SessionManager sessionManager,
            GetUserProfileInteractor getUserProfileInteractor
    ) {
        this.feedbackView = feedbackView;
        this.sessionManager = sessionManager;
        this.userFeedback = new UserFeedback();
        this.permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
        this.accessNetworkStatePermission = new AccessNetworkStatePermission();

        this.feedbackUseCase = FeedbackUseCase.Companion.getInstance();
        this.getUserProfileInteractor = getUserProfileInteractor;
    }

    public void initUi() {
        if (screenCapture == null) {
            feedbackView.checkScreenshotSwitch(false);
        } else {
            feedbackView.showFeedbackImage();
            feedbackView.showScheenShotPreview();
            feedbackView.checkScreenshotSwitch(true);
        }
        getUserProfileInteractor.getUser(new Function1<UserProfile, Unit>() {
            @Override
            public Unit invoke(UserProfile userProfile) {
                feedbackView.onUserProfileLoaded(userProfile);
                return null;
            }
        }, new Function1<ErrorObject, Unit>() {
            @Override
            public Unit invoke(ErrorObject errorObject) {
                return null;
            }
        });
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
        userFeedback.setType(FeedBackType.FEEDBACK);
        feedbackView.setFeedbackButtonSelected();
    }

    public void bugButtonPressed() {
        userFeedback.setType(FeedBackType.BUG);
        feedbackView.setBugButtonSelected();
    }

    public void screenshotSwitchPressed(boolean activated) {
        userFeedback.attachScreenshot(activated);

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

    public void setScreenCapture(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
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

        userFeedback.setMessage(feedbackMessage);
        userFeedback.setScreen(screen);

        if (userFeedback.mustAttachScreenshot()) {
            userFeedback.setScreenCapture(screenCapture);
        } else {
            userFeedback.setScreenCapture(null);
        }

        if (!permissionRequestExecutor.isGranted(accessNetworkStatePermission)) {
            askForPermission();
        } else {
            if (needLogin()) {
                feedbackView.requestLogin();
            } else {
                sendFeedback();
            }
        }
    }

    public void onEmailEntered(String email) {
        this.email = !TextUtils.isEmpty(email) ? email : null;
        feedbackView.onEmailError(false);
    }

    private Boolean needLogin() {
        AppData appConfig = AppliveryDataManager.INSTANCE.getAppData();
        if (appConfig != null) {
            boolean isAuthUpdate = appConfig.component5().getForceAuth();
            return isAuthUpdate && !sessionManager.hasSession();
        } else {
            AppliveryLog.error("Null app config at send feedback");
            return true;
        }
    }

    private void onSuccess() {
        screenCapture = null;
        feedbackView.cleanScreenData();
        feedbackView.dismissFeedBack();
    }

    private void onError(ErrorObject error) {
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
                    @Override
                    public void onPermissionAllowed(boolean permissionAllowed) {
                        if (permissionAllowed) {
                            sendFeedback();
                        }
                    }
                }, AppliverySdk.getCurrentActivity());
    }

    private void sendFeedback() {
        PackageInfo packageInfo = AndroidCurrentAppInfo.Companion.getPackageInfo();
        AndroidDeviceDetailsInfo androidDeviceDetailsInfo = new AndroidDeviceDetailsInfo();
        DeviceInfo deviceInfo = androidDeviceDetailsInfo.getDeviceInfo();

        if (!TextUtils.isEmpty(email) && !mailValidator.isValid(email)) {
            feedbackView.onEmailError(true);
            return;
        }

        Feedback feedback = new Feedback(
                deviceInfo,
                userFeedback.getMessage(),
                packageInfo, userFeedback.getBase64ScreenCapture(),
                userFeedback.getType().getStringValue(),
                email
        );

        feedbackUseCase.sendFeedback(feedback, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {

                if (aBoolean) {
                    onSuccess();
                } else {
                    onError(new ErrorObject());
                }
                return null;
            }
        });
    }
}
