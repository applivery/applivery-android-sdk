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

package com.applivery.applvsdklib.domain.feedback;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.domain.BaseInteractor;
import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.DeviceInfo;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.domain.model.Feedback;
import com.applivery.applvsdklib.domain.model.FeedbackResult;
import com.applivery.applvsdklib.domain.model.PackageInfo;
import com.applivery.applvsdklib.network.api.AppliveryApiService;
import com.applivery.applvsdklib.network.api.requests.FeedbackRequest;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidCurrentAppInfo;
import com.applivery.applvsdklib.tools.androidimplementations.AndroidDeviceDetailsInfo;
import com.applivery.applvsdklib.ui.views.feedback.UserFeedbackPresenter;

import androidx.annotation.NonNull;

public class FeedbackInteractor extends BaseInteractor<FeedbackResult> {

    private final FeedbackRequest feedbackRequest;
    private final InteractorCallback feedbackCallback;

    private FeedbackInteractor(@NonNull AppliveryApiService appliveryApiService,
                               @NonNull Feedback feedback, @NonNull InteractorCallback interactorCallback) {

        this.feedbackRequest = new FeedbackRequest(appliveryApiService, feedback);
        this.feedbackCallback = interactorCallback;
    }

    @Override
    protected void receivedResponse(BusinessObject result) {
        super.receivedResponse(result, FeedbackResult.class);
    }

    @Override
    protected void error(ErrorObject error) {
        feedbackCallback.onError(error);
    }

    @Override
    protected void success(FeedbackResult response) {
        feedbackCallback.onSuccess(response);
    }

    @Override
    protected BusinessObject performRequest() {
        return feedbackRequest.execute();
    }

    public static Runnable getInstance(@NonNull AppliveryApiService service, @NonNull String message,
                                       @NonNull String screenshot, @NonNull String type,
                                       @NonNull UserFeedbackPresenter userFeedbackPresenter) {

        PackageInfo packageInfo =
                AndroidCurrentAppInfo.Companion.getPackageInfo(AppliverySdk.getApplicationContext());

        AndroidDeviceDetailsInfo androidDeviceDetailsInfo = new AndroidDeviceDetailsInfo();
        DeviceInfo deviceInfo = androidDeviceDetailsInfo.getDeviceInfo();

        Feedback feedback = new Feedback(deviceInfo, message, packageInfo, screenshot, type);

        return new FeedbackInteractor(service, feedback, userFeedbackPresenter);
    }
}
