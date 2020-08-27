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

import android.content.Context;

import com.applivery.applvsdklib.tools.utils.Validate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 25/12/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateUtilsTest {

    @Mock
    Context context;

    //region notNull
    @Test(expected = NullPointerException.class)
    public void notNullValitadionWithNull() throws Exception {
        Validate.notNull(null, "Test null");
    }

    @Test
    public void notNullValitadionWithoutNull() throws Exception {
        Validate.notNull("NOT NULL", "Test null");
        assertTrue(true);
    }

    @Test
    public void notNullValitadionWithEmptyString() throws Exception {
        Validate.notNull("", "Test null");
        assertTrue(true);
    }
    //endregion

    //region empty
    @Test(expected = IllegalArgumentException.class)
    public void notEmptyValitadionWithEmpty() throws Exception {
        List<String> strings = new ArrayList<>();
        Validate.notEmpty(strings, "Empty Array");
    }

    @Test
    public void notEmptyValitadionWithElements() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        Validate.notEmpty(strings, "Test not Empty");
        assertTrue(true);
    }
    //endregion

    //region contains no nulls
    @Test(expected = NullPointerException.class)
    public void containsNoNullsValitadionWithNulls() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add(null);
        strings.add("Hello");
        Validate.containsNoNulls(strings, "null value");
    }

    @Test
    public void containsNoNullsValitadionWithoutNulls() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("Hello 2");
        Validate.containsNoNulls(strings, "no null value");
        assertTrue(true);
    }
    //endregion

    //region contains no nulls or empty
    @Test(expected = NullPointerException.class)
    public void containsNoNullsOrEmptyValitadionWithNulls() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add(null);
        strings.add("Hello");
        Validate.containsNoNullOrEmpty(strings, "null value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void containsNoNullsOrEmptyValitadionWithEmpty() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("");
        Validate.containsNoNullOrEmpty(strings, "empty value");
    }

    @Test
    public void containsNoNullsValitadionWithoutNullsOrEmpty() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("Hello 2");
        Validate.containsNoNullOrEmpty(strings, "no null neither empty value");
        assertTrue(true);
    }
    //endregion

    //region Empty or null
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyOrnullValidationWithEmpty() throws Exception {
        List<String> strings = new ArrayList<>();
        Validate.notEmptyAndContainsNoNulls(strings, "Test Empty");
    }

    @Test(expected = NullPointerException.class)
    public void testEmptyOrnullValidationWithNulls() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add(null);
        Validate.notEmptyAndContainsNoNulls(strings, "Test contains null");
    }

    @Test
    public void testEmptyOrnullValidationSuccess() throws Exception {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("Hello 2");
        Validate.notEmptyAndContainsNoNulls(strings, "Test Success");
        assertTrue(true);
    }
    //endregion

    //endregion

    //region Internet Permissions
    @Test(expected = NullPointerException.class)
    public void tryCheckInternetPermissionsWithNullContext() throws Exception {
        Validate.hasInternetPermissions(null);
    }
    //endregion


    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionCheckInternetPermissionsWithContextNotNullAndNoPermission() throws Exception {
        when(context.checkCallingOrSelfPermission(isA(String.class))).thenReturn(-1);
        Validate.hasInternetPermissions(context);
    }

    @Test
    public void tryCheckInternetPermissionsWithContextAndPermission() {
        when(context.checkCallingOrSelfPermission(isA(String.class))).thenReturn(1);
        Validate.hasInternetPermissions(context);
        assertTrue(true);
    }
}
