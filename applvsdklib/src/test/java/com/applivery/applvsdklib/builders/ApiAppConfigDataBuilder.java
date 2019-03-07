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

package com.applivery.applvsdklib.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class ApiAppConfigDataBuilder {

    public static final String DESCRIPTION = "This is the app description";
    public static final int BUILDS_COUNT = 10;
    public static final int CRASHES_COUNT = 10;
    public static final int SITES_COUNT = 10;
    public static final int TOTAL_DOWNLOADS_COUNT = 10;
    public static final int FEEDBACK_COUNT = 10;
    public static final String ID = "00293dscx09299";
    public static final String NAME = "Test App";
    public static final List<String> SO = new ArrayList<>(Arrays.asList("iOS", "Android"));
    public static final String CREATED_AT = "2015-10-21T11:46:13Z";
    public static final String MODIFIED_AT = "2015-10-21T18:41:39Z";

    public static ApiAppConfigDataBuilder Builder() {
      return new ApiAppConfigDataBuilder();
    }

    public ApiAppConfigData build() {
        ApiAppConfigData apiAppConfigData = new ApiAppConfigData();
        apiAppConfigData.setBuildsCount(BUILDS_COUNT);
        apiAppConfigData.setCrashesCount(CRASHES_COUNT);
        apiAppConfigData.setDescription(DESCRIPTION);
        apiAppConfigData.setFeedbackCount(FEEDBACK_COUNT);
        apiAppConfigData.setId(ID);
        apiAppConfigData.setName(NAME);
        apiAppConfigData.setTotalDownloads(TOTAL_DOWNLOADS_COUNT);
        apiAppConfigData.setSitesCount(SITES_COUNT);
        apiAppConfigData.setCreated(CREATED_AT);
        apiAppConfigData.setModified(MODIFIED_AT);
        apiAppConfigData.setSo(SO);
        ApiSdK sdk = new ApiSdK();
        apiAppConfigData.setSdk(sdk);
        return apiAppConfigData;
    }

    }
