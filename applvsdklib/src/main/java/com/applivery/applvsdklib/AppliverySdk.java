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
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applivery.applvsdklib.domain.exceptions.NotForegroundActivityAvailable;
import com.applivery.applvsdklib.domain.login.BindUserInteractor;
import com.applivery.applvsdklib.domain.login.GetUserProfileInteractor;
import com.applivery.applvsdklib.domain.login.UnBindUserInteractor;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.features.appconfig.AppConfigUseCase;
import com.applivery.applvsdklib.features.download.DownloadBuildUseCase;
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
import com.applivery.base.domain.model.UserProfile;

import java.util.Collection;

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
    private static boolean lockedApp = false;
    private static volatile boolean isDebugEnabled = BuildConfig.DEBUG;
    private static Context applicationContext;
    private static PermissionChecker permissionRequestManager;
    private static AppliveryActivityLifecycleCallbacks activityLifecycle;
    private static final Object LOCK = new Object();

    private static Boolean sdkInitialized = false;
    private static Boolean checkForUpdatesBackground = BuildConfig.CHECK_FOR_UPDATES_BACKGROUND;
    private static Boolean isUpdating = false;

    public static synchronized void sdkInitialize(Application app, String appToken, String tenant, String redirectScheme) {
        init(app, appToken, tenant, redirectScheme);
    }

    private static void init(Application app, String appToken, String tenant, String redirectScheme) {
        if (!sdkInitialized) {
            sdkInitialized = true;
            initializeAppliveryConstants(app, appToken, tenant, redirectScheme);
            app.registerActivityLifecycleCallbacks(new AppliveryLifecycleCallbacks());
            registerActivityLifecyleCallbacks(app);
            obtainAppConfig(false);
        }
    }

    /**
     * @return true if success false otherwise
     */
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

    private static void initializeAppliveryConstants(Application app, String appToken, String tenant, String redirectScheme) {

        //region validate some requirements
        Context applicationContext = Validate.notNull(app, "Application").getApplicationContext();
        Validate.notNull(applicationContext, "applicationContext");
        Validate.hasInternetPermissions(applicationContext, false);
        //endregion

        AppliveryDataManager.INSTANCE.setAppToken(appToken);
        AppliveryDataManager.INSTANCE.setTenant(tenant);
        AppliveryDataManager.INSTANCE.setRedirectScheme(redirectScheme);

        if (redirectScheme != null && !isRedirectSchemeRegistered(applicationContext, redirectScheme)) {
            throw new RuntimeException(
                    "redirectScheme is not handled by any activity in this app! "
                            + "Ensure that appliveryAuthRedirectScheme in your build.gradle file "
                            + "is correctly configured, or that an appropriate intent filter "
                            + "exists in your app manifest.");
        }

        AppliverySdk.applicationContext = applicationContext;

        AppliverySdk.activityLifecycle = new AppliveryActivityLifecycleCallbacks(applicationContext);
        AppliverySdk.permissionRequestManager = new AndroidPermissionCheckerImpl(AppliverySdk.activityLifecycle);
    }

    private static void obtainAppConfig(boolean checkForUpdates) {
        if (isInitialized()) {
            AppConfigUseCase appConfigUseCase = AppConfigUseCase.Companion.getInstance();
            appConfigUseCase.getAppConfig(checkForUpdates, null);
        }
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

    public static void update() {
        if (isInitialized()) {
            DownloadBuildUseCase downloadBuildUseCase = DownloadBuildUseCase.Companion.getInstance();
            downloadBuildUseCase.downloadLastBuild();
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

    static void getUser(final @NonNull GetUserCallback callback) {

        GetUserProfileInteractor getUserProfileInteractor = Injection.INSTANCE.provideGetUserProfileInteractor();
        getUserProfileInteractor.getUser(
                new Function1<UserProfile, Unit>() {
                    @Override
                    public Unit invoke(UserProfile userProfile) {
                        callback.onSuccess(userProfile);
                        return null;
                    }
                }, new Function1<ErrorObject, Unit>() {
                    @Override
                    public Unit invoke(ErrorObject errorObject) {
                        callback.onError(errorObject.getMessage());
                        return null;
                    }
                }
        );
    }


    private static boolean isRedirectSchemeRegistered(Context context, String scheme) {
        // ensure that the redirect URI declared in the configuration is handled by some activity
        // in the app, by querying the package manager speculatively
        Intent redirectIntent = new Intent();
        redirectIntent.setPackage(context.getPackageName());
        redirectIntent.setAction(Intent.ACTION_VIEW);
        redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        redirectIntent.setData(new Uri.Builder().scheme(scheme).build());

        return !context.getPackageManager().queryIntentActivities(redirectIntent, 0).isEmpty();
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
