package com.applivery.android.sdk.feedback

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import com.applivery.android.sdk.domain.Logger
import kotlin.math.sqrt

internal interface ShakeDetector {

    fun start(listener: Listener)

    fun stop()

    interface Listener {
        fun onShake(count: Int)
    }
}

internal class AndroidShakeDetector(
    context: Context,
    private val logger: Logger
) : ShakeDetector, SensorEventListener {

    private val sensorManager by lazy { context.getSystemService<SensorManager>() }

    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0

    private var listener: ShakeDetector.Listener? = null

    override fun start(listener: ShakeDetector.Listener) {
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            logger.log("Accelerometer not found. Shake feedback won't work for this device")
            return
        }
        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        this.listener = listener
    }

    override fun stop() {
        sensorManager?.unregisterListener(this)
        this.listener = null
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values.getOrNull(0) ?: 0f
        val y = event.values.getOrNull(1) ?: 0f
        val z = event.values.getOrNull(2) ?: 0f

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        // gForce will be close to 1 when there is no movement.
        val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return
            }

            // reset the shake count after 3 seconds of no shakes
            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0
            }

            mShakeTimestamp = now
            mShakeCount++

            listener?.onShake(mShakeCount)
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
}
