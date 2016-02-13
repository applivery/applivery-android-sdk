package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.responses.ApiAppliveryServerErrorResponse;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import java.io.IOException;
import retrofit.Call;
import retrofit.Response;

public abstract class ServerRequest {

    public final BusinessObject execute() {
        try {
            return performRequest();
        }catch (RequestHttpException re){
            return new ErrorObject(re.getServerResponse().getError());
        }
    }

    protected abstract BusinessObject performRequest();

    public <T extends ServerResponse> T performRequest(Call<T> call) {

        Response<T> apiResponse = null;

        try{

            apiResponse = call.execute();
            T response = apiResponse.body();
            response.setHttpCode(apiResponse.code());

            return response;
        }catch (Exception exception){
            ServerResponse serverResponse = onException(exception, apiResponse);
            throw new RequestHttpException(serverResponse);
        }
    }

    private ServerResponse onException(Exception exception,
        Response apiResponse) {

        ServerResponse response;

        int httpCode = (apiResponse != null) ? apiResponse.code() :
            ApiAppliveryServerErrorResponse.NO_CONNECTION_HTTP_CODE;
        String httpmsg = (apiResponse != null) ? apiResponse.message() :
            ApiAppliveryServerErrorResponse.NO_CONNECTION_HTTP_MSG;

        response = new ServerResponse();

        response.setStatus(false);
        response.setData(null);
        response.setHttpCode(httpCode);

        if (exception instanceof IOException){
            response.setError(ApiAppliveryServerErrorResponse.
                createNoConnectionErrorInstance(exception.getMessage()));
        }else{
            response.setError(ApiAppliveryServerErrorResponse.
                createErrorInstance(httpCode, httpmsg));
        }

        return response;
    }
}
