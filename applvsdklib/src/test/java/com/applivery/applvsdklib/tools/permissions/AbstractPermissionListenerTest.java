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

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */

import android.app.Activity;
import android.content.Context;
import com.applivery.applvsdklib.tools.permissions.utils.DummyPermissionListenerImplementation;
import com.applivery.applvsdklib.tools.permissions.utils.StubString;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractPermissionListenerTest {

  @Mock UserPermissionRequestResponseListener userPermissionRequestResponseListener;

  @Mock ContextProvider contextProvider;

  @Mock Context applicationContext;
  @Mock Activity activityContext;

  @Mock PermissionToken permissionToken;

  @Mock StubString stubString;

  private void setUpActivityContextInitializedScenario() {
    when(contextProvider.getApplicationContext()).thenReturn(applicationContext);
    when(contextProvider.getCurrentActivity()).thenReturn(activityContext);
    when(contextProvider.isActivityContextAvailable()).thenReturn(true);
    when(contextProvider.isApplicationContextAvailable()).thenReturn(true);
  }

  @Test
  public void responseListenerIsNotCalledWithNullListenerOnPermissionGranted(){
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    setUpActivityContextInitializedScenario();

    dummyPermissionListenerImplementation.onPermissionDenied(
        new PermissionDeniedResponse(new PermissionRequest("Test"), true));

    verify(userPermissionRequestResponseListener, never()).onPermissionAllowed(false);

  }

  @Test
  public void getPermissionDeniedFeedbackMethodIsCalledForShowingToastOnActivityContextNull(){
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    setUpActivityContextNullButApplicationNotNullScenario();

    try {
      dummyPermissionListenerImplementation.onPermissionDenied(
          new PermissionDeniedResponse(new PermissionRequest("Test"), true));
    }catch (NullPointerException n){

    }

    verify(stubString).getPermissionDeniedFeedback();

  }

  public void getPermissionDeniedFeedbackMethodIsNotCalledForShowingToastOnActivityContextNotNull(){
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    setUpActivityContextInitializedScenario();

      dummyPermissionListenerImplementation.onPermissionDenied(
          new PermissionDeniedResponse(new PermissionRequest("Test"), true));

    verify(stubString,never()).getPermissionDeniedFeedback();

  }

  private void setUpActivityContextNullButApplicationNotNullScenario() {
    when(contextProvider.getApplicationContext()).thenReturn(applicationContext);
    when(contextProvider.getCurrentActivity()).thenReturn(null);
    when(contextProvider.isActivityContextAvailable()).thenReturn(false);
    when(contextProvider.isApplicationContextAvailable()).thenReturn(true);
  }

  @Test public void responseListenerIsNotCalledWithNullListenerOnPermissionDenied() {
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    dummyPermissionListenerImplementation.onPermissionGranted(
        new PermissionGrantedResponse(new PermissionRequest("Test")));

    verify(userPermissionRequestResponseListener, never()).onPermissionAllowed(true);
  }

  @Test public void responseListenerIsCalledWithNotNullListenerOnPermissionDenied() {
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(userPermissionRequestResponseListener,
            contextProvider, stubString);

    setUpActivityContextInitializedScenario();

    dummyPermissionListenerImplementation.onPermissionDenied(
        new PermissionDeniedResponse(new PermissionRequest("Test"), true));

    verify(userPermissionRequestResponseListener).onPermissionAllowed(false);
  }

  @Test public void responseListenerIsCalledWithNotNullListenerOnPermissionGranted() {
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(userPermissionRequestResponseListener,
            contextProvider, stubString);

    dummyPermissionListenerImplementation.onPermissionGranted(
        new PermissionGrantedResponse(new PermissionRequest("Test")));

    verify(userPermissionRequestResponseListener).onPermissionAllowed(true);
  }

  @Test public void rationaleTextMethodsAreCalledOnNotNullActivityContext() {
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    setUpActivityContextInitializedScenario();

    try {
      dummyPermissionListenerImplementation.onPermissionRationaleShouldBeShown(
          new PermissionRequest("Test"), permissionToken);
    }catch (NullPointerException npe){

    }

    verify(stubString).getPermissionRationaleMessage();
    verify(stubString).getPermissionRationaleTitle();

  }

  @Test public void rationaleTextMethodsAreNotCalledOnNullActivityContext() {
    DummyPermissionListenerImplementation dummyPermissionListenerImplementation =
        new DummyPermissionListenerImplementation(contextProvider, stubString);

    setUpActivityContextNullButApplicationNotNullScenario();

    dummyPermissionListenerImplementation.onPermissionRationaleShouldBeShown(
        new PermissionRequest("Test"), permissionToken);

    verify(stubString, never()).getPermissionRationaleMessage();
    verify(stubString, never()).getPermissionRationaleTitle();

  }


}
