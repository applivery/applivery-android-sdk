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

package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.applivery.applvsdklib.AppliverySdk;
import java.util.List;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class SensorEventsController {
  private static final int REGISTER = 1;
  private static final int UNREGISTER = 2;
  private final SensorManager sensorManager;
  private final SensorEventsReceiver sensorEventsReceiver = new SensorEventsReceiver();
  private final SensorsAccessor sensorsAccessor = new SensorsAccessor(sensorEventsReceiver);


  public static SensorEventsController getInstance(Context applicationContext){
    try {
      return getWorkingInstance(applicationContext);
    }catch (AppliveryNotHasNoSensorsToManageEventsException appliveryException){
      AppliverySdk.Logger.log(appliveryException.getMessage());
      return getEmptyInstance();
    }
  }

  public void unRegisterAllSensorsForApplication() {
    sensorAction(REGISTER);
  }

  private void sensorAction(int action){

    SensorsAccessor.AppliverySensorIterator iterator = sensorsAccessor.getIterator();

    while (iterator.hasNext()){
      AndroidSensorWrapper androidSensorWrapper = iterator.next();
      SensorEventListener sensorEventListener = androidSensorWrapper.getSensorEventListener();
      Sensor sensor = sensorManager.getDefaultSensor(androidSensorWrapper.getAndroidSensorType());
      if (action == REGISTER){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
      }else if (action == UNREGISTER){
        sensorManager.unregisterListener(sensorEventListener, sensor);
      }

    }
  }

  public void registerAllSensorsForApplication() {
    sensorAction(REGISTER);
  }

  private SensorEventsController(Context context){
    sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
  }

  private SensorEventsController() {
    sensorManager = null;
  }

  private static SensorEventsController getWorkingInstance(Context applicationContext){
    SensorEventsController sensorEventsController = new SensorEventsController(applicationContext);
    sensorEventsController.checkAvalableSensors();

    if (!sensorEventsController.sensorsAccessor.hasAvailableSensors()){
      throw new AppliveryNotHasNoSensorsToManageEventsException("There are not registered sensors, "
          + "some features like shake feedback can be not available");
    }

    return sensorEventsController;
  }

  private static SensorEventsController getEmptyInstance() {
    return new SensorEventsController();
  }

  private void checkAvalableSensors() {
    List<Sensor> listOfSensorsOnDevice = sensorManager.getSensorList(Sensor.TYPE_ALL);
    for (int i = 0; i < listOfSensorsOnDevice.size(); i++) {
      if (listOfSensorsOnDevice.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {
        AppliverySdk.Logger.log("ACCELEROMETER sensor is available on device");
        sensorsAccessor.enableSensor(listOfSensorsOnDevice.get(i).getType());
      }
    }
  }
}
