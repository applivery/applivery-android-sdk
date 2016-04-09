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

package com.applivery.applvsdklib.tools.permissions.utils;

import com.applivery.applvsdklib.tools.permissions.AbstractPermissionListener;
import com.applivery.applvsdklib.tools.permissions.ContextProvider;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
public class DummyPermissionListenerImplementation extends AbstractPermissionListener {

  private final StubString stubString;

  public DummyPermissionListenerImplementation(ContextProvider contextProvider,
      StubString stubString) {
    super(contextProvider);
    this.stubString = stubString;
  }

  public DummyPermissionListenerImplementation(UserPermissionRequestResponseListener
      userPermissionRequestResponseListener, ContextProvider contextProvider,
      StubString stubString) {
    super(userPermissionRequestResponseListener, contextProvider);
    this.stubString = stubString;
  }

  @Override public int getPermissionDeniedFeedback() {
    return stubString.getPermissionDeniedFeedback();
  }

  @Override public int getPermissionRationaleMessage() {
    return stubString.getPermissionRationaleMessage();
  }

  @Override public int getPermissionRationaleTitle() {
    return stubString.getPermissionRationaleTitle();
  }

}
