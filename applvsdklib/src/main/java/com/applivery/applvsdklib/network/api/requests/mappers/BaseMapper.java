package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 8/11/15.
 */
public abstract class BaseMapper<Model extends BusinessObject, Data> {

  public BusinessObject map(ServerResponse<Data> serverResponse) {

    if (serverResponse.getStatus() == true) {
      return mapBusinessObject(serverResponse.getData());
    } else {
      return new ErrorObject(serverResponse.getError());
    }
  }

  protected abstract Model mapBusinessObject(Data serverResponse);
}
