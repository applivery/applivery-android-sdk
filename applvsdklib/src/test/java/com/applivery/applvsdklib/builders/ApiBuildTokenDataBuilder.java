package com.applivery.applvsdklib.builders;

import com.applivery.applvsdklib.network.api.model.ApiBuildTokenData;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 2/1/16.
 */
public class ApiBuildTokenDataBuilder {

    public static final String BUILD_TOKEN = "dflnasdjnaejw49u93ienfdmasnf32y8";
    public static final String BUILD_ID = "jfkef38jer328jfdke89";
    public static final long EXPIRATION_TIME = 230942347012l;

    public static ApiBuildTokenDataBuilder Builder() {
      return new ApiBuildTokenDataBuilder();
    }

    public ApiBuildTokenData build() {
        ApiBuildTokenData apiBuildTokenData = new ApiBuildTokenData();
        apiBuildTokenData.setBuild(BUILD_ID);
        apiBuildTokenData.setExp(EXPIRATION_TIME);
        apiBuildTokenData.setToken(BUILD_TOKEN);
        return apiBuildTokenData;
    }

    }
