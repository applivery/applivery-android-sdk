package com.applivery.applvsdklib.network.api.mappers;

import com.applivery.applvsdklib.builders.ApiAndroidBuilder;
import com.applivery.applvsdklib.domain.model.Android;
import com.applivery.applvsdklib.network.api.model.ApiAndroid;
import com.applivery.applvsdklib.network.api.requests.mappers.AndroidMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 24/1/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AndroidMapperTest {

  @Test
  public void shouldReturnAndroidModelInstanceWhenApiAndroidIsNotNull()  {
    ApiAndroid apiAndroid = ApiAndroidBuilder.Builder().build();

    AndroidMapper androidMapper = new AndroidMapper();

    Android android = androidMapper.dataToModel(apiAndroid);

    assertThat(android, instanceOf(Android.class));

    assertEquals(android.getLastBuildId(), ApiAndroidBuilder.LAST_BUILD_ID);
    assertEquals(android.getLastBuildVersion(), ApiAndroidBuilder.LAST_BUILD_VERSION);
    assertEquals(android.getMinVersion(), ApiAndroidBuilder.MIN_VERSION);
    assertEquals(android.getMustUpdateMsg(), ApiAndroidBuilder.MUST_UPDATE_MSG);
    assertEquals(android.getUpdateMsg(), ApiAndroidBuilder.UPDATE_MSG);
    assertEquals(android.isForceUpdate(), ApiAndroidBuilder.FORCE_UPDATE);
    assertEquals(android.isOta(), ApiAndroidBuilder.OTA);

  }

}
