package com.applivery.applvsdklib.domain;

import com.applivery.applvsdklib.domain.model.ErrorObject;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public interface InteractorCallback<Data> {
  void onSuccess(Data businessObject);
  void onError(ErrorObject error);
}
