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
import android.view.ViewGroup;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PermissionsUIViewsTest extends TestCase {

  @Mock
  Activity activity;

  @Mock ViewGroup viewGroup;

  @Test
  public void shouldReturnViewGroupWhenActivityIsNotNull(){

    when(activity.findViewById(anyInt())).thenReturn(viewGroup);

    ViewGroup viewGroup = PermissionsUIViews.getAppContainer(activity);

    assertSame(this.viewGroup, viewGroup);
    verify(activity).findViewById(anyInt());

  }

  @Test(expected = NullContainerException.class)
  public void shouldThrowNullContainerExceptionWhenActivityIstNull(){
    PermissionsUIViews.getAppContainer(null);
  }
}