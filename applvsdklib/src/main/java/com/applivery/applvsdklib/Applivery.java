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

package com.applivery.applvsdklib;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 17/1/16.
 */
public class Applivery {

    /**
     * Initializes Sdk for the current app and developer. Call this method from your application
     * instance when onCreate method is called. Pay attention to description of isPlayStoreRelease
     * param.
     *
     * @param app      your app instance, it can't be null.
     * @param appToken your app tokenoken. You can find this value at applivery dashboard in your
     *                 app settings section
     */
    public static void init(@NonNull Application app, @NonNull String appToken) {
        AppliverySdk.sdkInitialize(app, appToken, null);
    }

    /**
     * Initializes Sdk for the current app and developer. Call this method from your application
     * instance when onCreate method is called. Pay attention to description of isPlayStoreRelease
     * param.
     *
     * @param app      your app instance, it can't be null.
     * @param appToken your app tokenoken. You can find this value at applivery dashboard in your
     *                 app settings section
     * @param tenant   tenant for private Applivery instances
     */
    public static void init(@NonNull Application app, @NonNull String appToken, @NonNull String tenant) {
        AppliverySdk.sdkInitialize(app, appToken, tenant);
    }

    /**
     * Sets if the app should check for updates when comming from background or only should
     * check it when the app is init for first time.
     * default false
     *
     * @param checkForUpdatesBackground Boolean if true the app check for updates when
     *                                  comming from background
     */
    public static void setCheckForUpdatesBackground(Boolean checkForUpdatesBackground) {
        AppliverySdk.setCheckForUpdatesBackground(checkForUpdatesBackground);
    }

    /**
     * @return Boolean Boolean true if the app check for updates when comming from background
     */
    public static Boolean getCheckForUpdatesBackground() {
        return AppliverySdk.getCheckForUpdatesBackground();
    }

    /**
     * Manually check for App updates on Applivery.
     */
    public static void checkForUpdates() {
        AppliverySdk.obtainAppConfigForCheckUpdates();
    }

    /**
     * Start the download of the last app version.
     */
    public static void update() {
        AppliverySdk.update();
    }

    /**
     * Enables feedback on shake, call it having your app in foreground whenever you want.
     */
    public static void enableShakeFeedback() {
        AppliverySdk.enableShakeFeedback();
    }

    /**
     * Disables feedback on shake, call it having your app in foreground whenever you want.
     */
    public static void disableShakeFeedback() {
        AppliverySdk.disableShakeFeedback();
    }

    /**
     * Enables feedback on screenshot capture, call it having your app in foreground whenever you
     * want.
     */
    public static void enableScreenshotFeedback() {
        AppliverySdk.enableScreenshotFeedback();
    }

    /**
     * Disables feedback on screenshot capture, call it having your app in foreground whenever you
     * want.
     */
    public static void disableScreenshotFeedback() {
        AppliverySdk.disableScreenshotFeedback();
    }

    /**
     * Login a user
     * <p>
     * Programatically login a user in Applivery, for example if the app has a custom login and don't
     * want to use Applivery's authentication to track the user in the platform
     * <p>
     * - Parameters:
     * - email: The user email. **Required**
     * - firstName: The first name of the user. **Optional**
     * - lastName: The last name of the user. **Optional**
     * - tags: A list of tags linked to the user with group / categorize purpose. **Optional**
     * <p>
     * - Since: 3.0
     * - Version: 3.0
     */
    public static void bindUser(@NonNull String email, @Nullable String firstName,
                                @Nullable String lastName, @Nullable Collection<String> tags) {
        AppliverySdk.bindUser(email, firstName, lastName, tags, null);
    }

    /**
     * Login a user
     * <p>
     * Programatically login a user in Applivery, for example if the app has a custom login and don't
     * want to use Applivery's authentication to track the user in the platform
     * <p>
     * - Parameters:
     * - email: The user email. **Required**
     * - firstName: The first name of the user. **Optional**
     * - lastName: The last name of the user. **Optional**
     * - tags: A list of tags linked to the user with group / categorize purpose. **Optional**
     * - callback: @BindUserCallback
     * <p>
     * - Since: 3.0
     * - Version: 3.0
     */
    public static void bindUser(@NonNull String email, @Nullable String firstName,
                                @Nullable String lastName, @Nullable Collection<String> tags,
                                final @Nullable BindUserCallback callback) {
        AppliverySdk.bindUser(email, firstName, lastName, tags, callback);
    }

    /**
     * Logout a previously binded user
     * <p>
     * Programatically logout a user in Applivery from a previous custom login.
     * <p>
     * - Since: 3.0
     * - Version: 3.0
     */
    public static void unbindUser() {
        AppliverySdk.unbindUser();
    }

    /**
     * Check if the application is updated to the latest version available.
     *
     * @param isUpToDateCallback the callback interface with the with the result
     */
    public static void isUpToDate(IsUpToDateCallback isUpToDateCallback) {
        AppliverySdk.isUpToDate(isUpToDateCallback);
    }

    /**
     * <p>
     * Gets the logged user profile if available.
     * </p>
     * - Since: 3.5
     * - Version: 3.5
     *
     * @param getUserCallback the callback interface with the result
     */
    public static void getUser(@NonNull GetUserCallback getUserCallback) {
        AppliverySdk.getUser(getUserCallback);
    }
}
