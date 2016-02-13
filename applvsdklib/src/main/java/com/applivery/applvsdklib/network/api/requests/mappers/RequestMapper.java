package com.applivery.applvsdklib.network.api.requests.mappers;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/12/15.
 */
public interface RequestMapper<Model, Data> {
  Data modelToData(Model model);
}
