package com.applivery.applvsdklib.network.api.requests.mappers;

import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.network.api.model.ApiBuildTokenData;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class ObtainBuildDownloadTokenResponseMapper
    extends BaseMapper<BuildTokenInfo,ApiBuildTokenData> {

  @Override protected BuildTokenInfo mapBusinessObject(ApiBuildTokenData apiBuildTokenData) {

    BuildTokenInfo buildTokenInfo = new BuildTokenInfo();
    buildTokenInfo.setBuild(apiBuildTokenData.getBuild());
    buildTokenInfo.setToken(apiBuildTokenData.getToken());

    Calendar c = Calendar.getInstance();
    long seconds = c.get(Calendar.SECOND) + apiBuildTokenData.getExp();
    buildTokenInfo.setExp(new Date(seconds));

    return buildTokenInfo;
  }
}
