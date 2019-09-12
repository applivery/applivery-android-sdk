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

package com.applivery.applvsdklib.tools.androidimplementations;

import android.os.Environment;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.download.app.ExternalStorageReader;
import com.applivery.applvsdklib.domain.download.permissions.ReadExternalPermission;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;

import java.io.File;

import androidx.annotation.NonNull;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class AndroidExternalStorageReaderImpl implements ExternalStorageReader {

    public static final String FILE_NOT_FOUND = "N/A";
    private final PermissionChecker permissionRequestExecutor;
    private final ReadExternalPermission readExternalPermission;

    public AndroidExternalStorageReaderImpl() {
        this.permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
        readExternalPermission = new ReadExternalPermission();
    }

    @Override
    public boolean fileExists(String apkFileName) {
        String apkPath = getAppPath(apkFileName);
        File f = new File(apkPath);
        return f.exists();
    }

    @Override
    public void getPath(String apkFileName, AppPathReceiver pathReceiver) {
        if (permissionRequestExecutor.isGranted(readExternalPermission)) {
            askForPermmission(apkFileName, pathReceiver);
        } else {
            checkPath(apkFileName, pathReceiver);
        }
    }

    private void checkPath(String apkFileName, AppPathReceiver pathReceiver) {
        String path = FILE_NOT_FOUND;

        if (fileExists(apkFileName)) {
            path = getAppPath(apkFileName);
        }

        pathReceiver.onPathReady(path);
    }

    private void askForPermmission(final String apkFileName, final AppPathReceiver pathReceiver) {
        permissionRequestExecutor.askForPermission(readExternalPermission,
                new UserPermissionRequestResponseListener() {
                    @Override
                    public void onPermissionAllowed(boolean permissionAllowed) {
                        if (permissionAllowed) {
                            checkPath(apkFileName, pathReceiver);
                        }
                    }
                }, AppliverySdk.getCurrentActivity());
    }

    private String getExternalStoragePath() {
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return f.getAbsolutePath();
    }

    @NonNull
    private String getAppPath(String apkFileName) {
        return getExternalStoragePath() + "/" + apkFileName + ".apk";
    }
}
