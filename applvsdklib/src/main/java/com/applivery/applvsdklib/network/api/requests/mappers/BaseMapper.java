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
