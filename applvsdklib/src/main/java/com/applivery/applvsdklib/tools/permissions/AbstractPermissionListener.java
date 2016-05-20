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

import android.content.Context;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 17/1/16.
 */
public abstract class AbstractPermissionListener implements PermissionListener {

  private UserPermissionRequestResponseListener userPermissionRequestResponseListener;
  private ContextProvider contextProvider;

  public AbstractPermissionListener(ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  public AbstractPermissionListener(
      UserPermissionRequestResponseListener userPermissionRequestResponseListener,
      ContextProvider contextProvider) {
    this.userPermissionRequestResponseListener = userPermissionRequestResponseListener;
    this.contextProvider = contextProvider;
  }

  @Override public void onPermissionGranted(PermissionGrantedResponse response) {
    if (userPermissionRequestResponseListener != null) {
      userPermissionRequestResponseListener.onPermissionAllowed(true);
    }
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    if (!contextProvider.isActivityContextAvailable()) {
      PermissionsUIViews.showPermissionToast(contextProvider.getApplicationContext(),
          getPermissionDeniedFeedback());
    }

    if (userPermissionRequestResponseListener != null) {
      userPermissionRequestResponseListener.onPermissionAllowed(false);
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
      PermissionToken token) {

    if (contextProvider.isActivityContextAvailable()) {
      Context context = contextProvider.getCurrentActivity();
      PermissionsUIViews.showRationaleView(createRationaleResponseInstance(token), context,
          getPermissionRationaleTitle(), getPermissionRationaleMessage());
    }
  }

  private RationaleResponse createRationaleResponseInstance(final PermissionToken token) {
    return new RationaleResponse() {
      @Override public void cancelPermissionRequest() {
        token.cancelPermissionRequest();
      }

      @Override public void continuePermissionRequest() {
        token.continuePermissionRequest();
      }
    };
  }

  public abstract int getPermissionDeniedFeedback();

  public abstract int getPermissionRationaleMessage();

  public abstract int getPermissionRationaleTitle();
}
