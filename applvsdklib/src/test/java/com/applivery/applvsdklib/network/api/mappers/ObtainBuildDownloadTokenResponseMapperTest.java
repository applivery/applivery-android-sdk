package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.builders.ApiBuildTokenDataBuilder;
import com.applivery.applvsdklib.domain.model.BuildTokenInfo;
import com.applivery.applvsdklib.domain.model.BusinessObject;
import com.applivery.applvsdklib.network.api.model.ApiBuildTokenData;
import com.applivery.applvsdklib.network.api.requests.mappers.ObtainBuildDownloadTokenResponseMapper;
import com.applivery.applvsdklib.network.api.responses.ApiBuildTokenResponse;
import com.applivery.applvsdklib.network.api.responses.ServerResponse;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.applivery.applvsdklib.matchers.IsDateEqualTo.isDateEqualTo;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 24/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ObtainBuildDownloadTokenResponseMapperTest {

  @Test
  public void shouldReturnNotNullBuildTokenModelInstanceWhenApiBuildTokenDataIsNotNull(){

    ApiBuildTokenData apiBuildTokenData = ApiBuildTokenDataBuilder.Builder().build();

    ObtainBuildDownloadTokenResponseMapper mapper = new ObtainBuildDownloadTokenResponseMapper();
    ServerResponse<ApiBuildTokenData> apiBuildTokenDataServerResponse = new ApiBuildTokenResponse();
    apiBuildTokenDataServerResponse.setStatus(true);
    apiBuildTokenDataServerResponse.setData(apiBuildTokenData);
    BusinessObject<BuildTokenInfo> bo = mapper.map(apiBuildTokenDataServerResponse);

    assertNotNull(bo);
    assertThat(bo.getObject(), instanceOf(BuildTokenInfo.class));
    BuildTokenInfo buildTokenInfo = bo.getObject();

    assertEquals(buildTokenInfo.getBuild(), ApiBuildTokenDataBuilder.BUILD_ID);
    assertEquals(buildTokenInfo.getToken(), ApiBuildTokenDataBuilder.BUILD_TOKEN);
    assertThat(buildTokenInfo.getExp(), isDateEqualTo(new Date(ApiBuildTokenDataBuilder.EXPIRATION_TIME)));

  }
}
