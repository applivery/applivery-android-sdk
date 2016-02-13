package com.applivery.applvsdklib.domain.exceptions;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 18/1/16.
 */
public class NotForegroundActivityAvailable extends RuntimeException {
  public NotForegroundActivityAvailable(String detailMessage) {
    super(detailMessage);
  }
}
