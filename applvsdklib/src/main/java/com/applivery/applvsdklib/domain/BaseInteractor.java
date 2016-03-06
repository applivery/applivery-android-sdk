package com.applivery.applvsdklib.domain;

import android.os.Handler;
import android.os.Message;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.domain.model.ErrorObject;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 7/11/15.
 */
public abstract class BaseInteractor<T> implements Runnable {

  Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      receivedResponse((BusinessObject) msg.obj);
    }
  };

  protected abstract void receivedResponse(BusinessObject obj);

  protected void receivedResponse(BusinessObject obj, Class<T> responseClass) {
    try {
      T response = responseClass.cast(obj);
      success(response);
    } catch (ClassCastException classCastException) {
      ErrorObject errorObject = (ErrorObject) obj;
      error(errorObject);
    }
  }

  protected abstract void error(ErrorObject serverResponse);

  protected abstract void success(T response);

  @Override public void run() {
    Message message = new Message();
    message.obj = performRequest();
    handler.sendMessage(message);
  }

  public void sendDelayedResponse(T response) {
    Message message = new Message();
    message.obj = response;
    handler.sendMessage(message);
  }

  protected abstract BusinessObject performRequest();
}
