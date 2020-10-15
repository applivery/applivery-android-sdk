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
package com.applivery.applvsdklib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.util.Log;

import com.applivery.applvsdklib.domain.exceptions.NotForegroundActivityAvailable;
import com.applivery.applvsdklib.domain.login.BindUserInteractor;
import com.applivery.applvsdklib.domain.login.UnBindUserInteractor;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.features.appconfig.AppConfigUseCase;
import com.applivery.applvsdklib.tools.androidimplementations.AppliveryActivityLifecycleCallbacks;
import com.applivery.applvsdklib.tools.androidimplementations.ScreenshotObserver;
import com.applivery.applvsdklib.tools.androidimplementations.sensors.SensorEventsController;
import com.applivery.applvsdklib.tools.injection.Injection;
import com.applivery.applvsdklib.tools.permissions.AndroidPermissionCheckerImpl;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.utils.Validate;
import com.applivery.applvsdklib.ui.model.ScreenCapture;
import com.applivery.applvsdklib.ui.views.feedback.FeedbackView;
import com.applivery.applvsdklib.ui.views.feedback.UserFeedbackView;
import com.applivery.base.AppliveryDataManager;
import com.applivery.base.AppliveryLifecycleCallbacks;
import com.applivery.base.domain.model.UserData;

import java.util.Collection;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/10/15.
 */
public class AppliverySdk {

    // TODO This class is already using too many Static fields, consider redesign.
    // TODO Hold static reference only to AppliverySdk object and wrap smaller objects inside
    private static final String TAG = AppliverySdk.class.getCanonicalName();
    private static volatile Executor executor;
    private static volatile String appToken;
    private static volatile String fileProviderAuthority;
    private static boolean lockedApp = false;
    private static volatile boolean isDebugEnabled = BuildConfig.DEBUG;
    private static Context applicationContext;
    private static PermissionChecker permissionRequestManager;
    private static AppliveryActivityLifecycleCallbacks activityLifecycle;
    private static final Object LOCK = new Object();

    private static Boolean sdkInitialized = false;
    private static Boolean checkForUpdatesBackground = BuildConfig.CHECK_FOR_UPDATES_BACKGROUND;
    private static Boolean isUpdating = false;

    public static synchronized void sdkInitialize(Application app, String appToken) {
        init(app, appToken);
    }

    @TargetApi(14)
    private static void init(Application app, String appToken) {
        if (!sdkInitialized) {
            sdkInitialized = true;
            initializeAppliveryConstants(app, appToken);
            app.registerActivityLifecycleCallbacks(new AppliveryLifecycleCallbacks());
            registerActivityLifecyleCallbacks(app);
            obtainAppConfig(false);
        }
    }

    /**
     * @return true if success false otherwise
     */
    @TargetApi(14)
    private static boolean registerActivityLifecyleCallbacks(Application app) {
        try {
            app.registerActivityLifecycleCallbacks(activityLifecycle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void isUpdating(Boolean updating) {
        isUpdating = updating;
    }

    public static synchronized Boolean isUpdating() {
        return isUpdating;
    }

    private static void initializeAppliveryConstants(Application app, String appToken) {

        //region validate some requirements
        Context applicationContext = Validate.notNull(app, "Application").getApplicationContext();
        Validate.notNull(applicationContext, "applicationContext");
        Validate.hasInternetPermissions(applicationContext, false);
        //endregion

        AppliverySdk.appToken = appToken;
        AppliveryDataManager.INSTANCE.setAppToken(appToken);

        AppliverySdk.fileProviderAuthority = composeFileProviderAuthority(app);

        AppliverySdk.applicationContext = applicationContext;

        AppliverySdk.activityLifecycle = new AppliveryActivityLifecycleCallbacks(applicationContext);
        AppliverySdk.permissionRequestManager =
                new AndroidPermissionCheckerImpl(AppliverySdk.activityLifecycle);
    }

    private static void obtainAppConfig(boolean checkForUpdates) {
        if (isInitialized()) {
            AppConfigUseCase appConfigUseCase = AppConfigUseCase.Companion.getInstance();
            appConfigUseCase.getAppConfig(checkForUpdates, null);
        }
    }

    private static String composeFileProviderAuthority(Application application) {
        return application.getPackageName() + ".provider";
    }

    @Nullable
    public static Context getApplicationContext() {
        return applicationContext;
    }

    @Nullable
    public static PermissionChecker getPermissionRequestManager() {
        return permissionRequestManager;
    }

    public static Activity getCurrentActivity() throws NotForegroundActivityAvailable {
        Activity activity = activityLifecycle.getCurrentActivity();
        if (activity != null) {
            return activity;
        } else {
            throw new NotForegroundActivityAvailable("There is not any available ActivityContext");
        }
    }

    public static boolean isContextAvailable() {
        return (activityLifecycle.getCurrentActivity() != null);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static void lockRotationToPortrait() {
        Activity activity = activityLifecycle.getCurrentActivity();
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void unlockRotation() {
        Activity activity = activityLifecycle.getCurrentActivity();
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static synchronized boolean isInitialized() {
        return sdkInitialized;
    }

    public static void obtainAppConfigForCheckUpdates() {
        if (isInitialized()) {
            obtainAppConfig(true);
        }
    }

    public static void downloadLastBuild() {
        if (isInitialized()) {
            AppConfigUseCase appConfigUseCase = AppConfigUseCase.Companion.getInstance();
            appConfigUseCase.downloadLastBuild();
        }
    }

    public static void updateAppConfig() {
        obtainAppConfig(false);
    }

    public static void continuePendingPermissionsRequestsIfPossible() {
        if (isInitialized()) {
            permissionRequestManager.continuePendingPermissionsRequestsIfPossible();
        }
    }

    public static Boolean getCheckForUpdatesBackground() {
        return checkForUpdatesBackground;
    }

    public static void setCheckForUpdatesBackground(Boolean checkForUpdatesBackground) {
        if (isInitialized()) {
            AppliverySdk.checkForUpdatesBackground = checkForUpdatesBackground;
        }
    }

    public static FeedbackView requestForUserFeedBack() {
        obtainAppConfig(false);
        FeedbackView feedbackView = null;
        if (!lockedApp) {
            feedbackView = UserFeedbackView.getInstance();
            if (feedbackView.isNotShowing()) {
                try {
                    feedbackView.lockRotationOnParentScreen(getCurrentActivity());
                    feedbackView.show();
                } catch (NotForegroundActivityAvailable exception) {
                }
            }
        }

        return feedbackView;
    }

    public static void requestForUserFeedBackWith(ScreenCapture screenCapture) {
        if (lockedApp) {
            return;
        }

        FeedbackView feedbackView = requestForUserFeedBack();
        if (feedbackView != null) {
            feedbackView.setScreenCapture(screenCapture);
        }
    }

    static void disableShakeFeedback() {
        if (isInitialized()) {
            SensorEventsController sensorController =
                    SensorEventsController.getInstance(applicationContext);
            sensorController.disableSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    static void enableShakeFeedback() {
        if (isInitialized()) {
            SensorEventsController sensorController =
                    SensorEventsController.getInstance(applicationContext);
            sensorController.enableSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    static void disableScreenshotFeedback() {
        if (isInitialized()) {
            ScreenshotObserver screenshotObserver = ScreenshotObserver.getInstance(applicationContext);
            screenshotObserver.stopObserving();
            screenshotObserver.disableScreenshotObserver();
        }
    }

    static void enableScreenshotFeedback() {
        if (isInitialized()) {
            ScreenshotObserver screenshotObserver = ScreenshotObserver.getInstance(applicationContext);
            screenshotObserver.enableScreenshotObserver();

            if (activityLifecycle.isActivityContextAvailable()) {
                screenshotObserver.startObserving();
            }
        }
    }

    static void bindUser(@NonNull String email, @Nullable String firstName, @Nullable String lastName,
                         @Nullable Collection<String> tags, final @Nullable BindUserCallback callback) {

        BindUserInteractor bindUserInteractor = Injection.INSTANCE.provideBindUserInteractor();
        UserData userData = new UserData(email, firstName, lastName, tags, "", "");
        bindUserInteractor.bindUser(userData, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (callback != null) {
                    callback.onSuccess();
                }
                return null;
            }
        }, new Function1<ErrorObject, Unit>() {
            @Override
            public Unit invoke(ErrorObject errorObject) {
                if (callback != null) {
                    callback.onError(errorObject.getMessage());
                }
                return null;
            }
        });
    }

    static void unbindUser() {
        UnBindUserInteractor unBindUserInteractor = Injection.INSTANCE.provideUnBindUserInteractor();
        unBindUserInteractor.unBindUser(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        }, new Function1<ErrorObject, Unit>() {
            @Override
            public Unit invoke(ErrorObject errorObject) {
                return null;
            }
        });
    }

    static void isUpToDate(IsUpToDateCallback isUpToDateCallback) {
        if (isInitialized()) {
            AppConfigUseCase appConfigUseCase = AppConfigUseCase.Companion.getInstance();
            appConfigUseCase.getAppConfig(false, isUpToDateCallback);
        }
    }

    public static class Logger {
        private static volatile boolean debug = isDebugEnabled;

        public static void log(String text) {
            if (debug) {
                if (text == null) {
                    text = "Empty message";
                }
                Log.d(TAG, text);
            }
        }

        public static void loge(String text) {
            if (debug) {
                if (text == null) {
                    text = "Empty message";
                }
                Log.e(TAG, text);
            }
        }
    }
}
