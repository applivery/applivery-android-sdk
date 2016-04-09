package com.applivery.applvsdklib.tools.androidimplementations.sensors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/4/16.
 */
public class SensorsAccessor {
  private final Map<String, AndroidSensorWrapper> sensors;

  public SensorsAccessor(SensorEventsReceiver sensorEventsReceiver) {
    sensors = new HashMap();
    initSensors(sensorEventsReceiver);
  }

  private void initSensors(SensorEventsReceiver sensorEventsReceiver) {
    //init Accelerometer sensor SHAKE Detection
    sensors.put(AppliverySensor.ACCELEROMETER.getStringValue(),
        new AndroidSensorWrapper(AppliverySensor.ACCELEROMETER,
            new ShakeDetector(sensorEventsReceiver)));

    //init Init all required sensors here


  }

  public boolean isSensorAvailable(AppliverySensor appliverySensor){
    if (sensors.containsKey(appliverySensor.getStringValue())){
      AndroidSensorWrapper androidSensorWrapper = sensors.get(appliverySensor.getStringValue());
      return androidSensorWrapper.isEnabled();
    }else{
      return false;
    }
  }

  public void setSensorAsAvailable(AppliverySensor appliverySensor){
    if (!isSensorAvailable(appliverySensor) &&
        sensors.containsKey(appliverySensor.getStringValue())){
      AndroidSensorWrapper androidSensorWrapper = sensors.get(appliverySensor.getStringValue());
      androidSensorWrapper.enableSensor();
    }
  }

  public void enableSensor(int type) {
    AppliverySensor appliverySensor = AppliverySensor.toAppliverySensor(type);
    setSensorAsAvailable(appliverySensor);
  }

  public boolean hasAvailableSensors() {

    Iterator it = sensors.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      AndroidSensorWrapper androidSensorWrapper = (AndroidSensorWrapper) pair.getValue();
      if (androidSensorWrapper.isEnabled()){
        return true;
      }
    }

    return false;
  }

  public AppliverySensorIterator getIterator() {
    return new AppliverySensorIterator();
  }

  protected class AppliverySensorIterator {

    private Set<String> keys;
    private Iterator<String> internalIterator;

    public AppliverySensorIterator() {
      this.keys = filterEnabledSensorsKeySet();
      this.internalIterator = keys.iterator();
    }

    private Set<String> filterEnabledSensorsKeySet() {
      Set<String> filteredSet = new HashSet<>();

      for (String s : sensors.keySet()) {
        if (sensors.get(s).isEnabled()){
          filteredSet.add(s);
        }
      }

      return filteredSet;
    }

    public boolean hasNext() {
      return internalIterator.hasNext();
    }

    public AndroidSensorWrapper next() {
      String key = internalIterator.next();
      AndroidSensorWrapper androidSensorWrapper = sensors.get(key);
      return androidSensorWrapper;
    }
  }
}
