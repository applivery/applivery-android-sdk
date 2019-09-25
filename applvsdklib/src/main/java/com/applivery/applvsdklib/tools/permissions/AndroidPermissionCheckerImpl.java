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

package com.applivery.applvsdklib.tools.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.ViewGroup;

import com.applivery.applvsdklib.AppliverySdk;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 15/1/16.
 */
public class AndroidPermissionCheckerImpl implements PermissionChecker {

    @NonNull
    private final ContextProvider contextProvider;

    public AndroidPermissionCheckerImpl(@NonNull ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public void askForPermission(@NonNull Permission permission,
                                 @NonNull UserPermissionRequestResponseListener userResponse, @NonNull Activity activity) {

        PermissionListener[] listeners = createListeners(permission, userResponse, activity);
        Dexter.withActivity(activity)
                .withPermission(permission.getAndroidPermissionStringType())
                .withListener(new CompositePermissionListener(listeners))
                .check();
    }

    private PermissionListener[] createListeners(Permission permission,
                                                 UserPermissionRequestResponseListener userResponse, Activity activity) {

        PermissionListener basicListener = getPermissionListenerImpl(permission, userResponse);

        try {
            ViewGroup viewGroup = PermissionsUIViews.getAppContainer(activity);
            PermissionListener listener = getDefaultDeniedPermissionListener(viewGroup, permission);
            return new PermissionListener[]{basicListener, listener};
        } catch (NullContainerException n) {
            return new PermissionListener[]{basicListener};
        }
    }

    private PermissionListener getDefaultDeniedPermissionListener(ViewGroup rootView,
                                                                  Permission permission) {

        return SnackbarOnDeniedPermissionListener.Builder.with(rootView,
                permission.getPermissionRationaleMessage())
                .withOpenSettingsButton(permission.getPermissionSettingsDeniedFeedback())
                .build();
    }

    private PermissionListener getPermissionListenerImpl(final Permission permission,
                                                         final UserPermissionRequestResponseListener userPermissionRequestResponseListener) {
        return new GenericPermissionListenerImpl(permission, userPermissionRequestResponseListener,
                contextProvider);
    }

    @Override
    public void continuePendingPermissionsRequestsIfPossible() {
        AppliverySdk.Logger.loge("continuePendingPermissionsRequestsIfPossible() not implemented");
    }

    @Override
    public boolean isGranted(Permission permission) {
        Context context = contextProvider.getApplicationContext();
        int permissionGranted =
                ContextCompat.checkSelfPermission(context, permission.getAndroidPermissionStringType());
        return PackageManager.PERMISSION_GRANTED == permissionGranted;
    }
}
