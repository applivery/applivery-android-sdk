package com.applivery.android.sdk.domain

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.telephony.TelephonyManager
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.applivery.android.sdk.domain.model.BatteryStatus
import com.applivery.android.sdk.domain.model.Device
import com.applivery.android.sdk.domain.model.DeviceInfo
import com.applivery.android.sdk.domain.model.Os
import com.applivery.android.sdk.domain.model.RamStatus
import com.applivery.android.sdk.domain.model.ScreenResolution
import kotlin.math.roundToInt

internal interface DeviceInfoProvider {

    val deviceInfo: DeviceInfo
}

internal class DeviceInfoProviderImpl(
    private val context: Context
) : DeviceInfoProvider {

    private val telephonyManager get() = context.getSystemService<TelephonyManager>()
    private val windowManager get() = context.getSystemService<WindowManager>()
    private val activityManager get() = context.getSystemService<ActivityManager>()
    private val connectivityManager get() = context.getSystemService<ConnectivityManager>()

    override val deviceInfo: DeviceInfo get() = DeviceInfo(device, os)

    private val device: Device
        get() = Device(
            batteryStatus = getBatteryStatus(),
            availableDiskPercentage = freeDiskPercentage,
            model = model,
            networkType = networkType,
            orientation = screenOrientation,
            ramStatus = getRamStatus(),
            screenResolution = screenResolution,
            deviceType = deviceType,
            vendor = vendor
        )

    private val os: Os get() = Os(OS_NAME, Build.VERSION.RELEASE)

    private val vendor: String = Build.MANUFACTURER

    private val model: String = Build.MODEL

    private val deviceType: String
        get() {
            val screenLayout: Int = context.resources.configuration.screenLayout
            val isTablet: Boolean =
                (screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
            return if (isTablet) TABLET else MOBILE
        }

    private val batteryPercentage: Int
        get() {
            val batteryStatusIntent = getBatteryStatusIntent() ?: return 0
            val level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            return ((level / scale.toFloat()) * 100).roundToInt()
        }

    private val isBatteryCharging: Boolean
        get() {
            val batteryStatusIntent = getBatteryStatusIntent() ?: return false
            val plugged: Int = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val isChargingAC = plugged == BatteryManager.BATTERY_PLUGGED_AC
            val isChargingUSB = plugged == BatteryManager.BATTERY_PLUGGED_USB
            val isChargingWireless = plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
            return isChargingAC || isChargingUSB || isChargingWireless
        }

    private val networkType: String
        get() {
            val manager = connectivityManager ?: return NETWORK_UNKNOWN

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = manager.activeNetwork ?: return NETWORK_UNKNOWN
                val capabilities = manager.getNetworkCapabilities(network) ?: return NETWORK_UNKNOWN

                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NETWORK_WIFI
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NETWORK_ETHERNET
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        telephonyManager?.getMobileNetworkType() ?: NETWORK_UNKNOWN
                    }

                    else -> NETWORK_UNKNOWN
                }
            } else @Suppress("DEPRECATION") {
                val networkInfo = manager.activeNetworkInfo ?: return NETWORK_UNKNOWN
                if (!networkInfo.isConnected) return NETWORK_UNKNOWN

                return when (networkInfo.type) {
                    ConnectivityManager.TYPE_WIFI -> NETWORK_WIFI
                    ConnectivityManager.TYPE_ETHERNET -> NETWORK_ETHERNET
                    ConnectivityManager.TYPE_MOBILE -> {
                        telephonyManager?.getMobileNetworkType() ?: NETWORK_UNKNOWN
                    }

                    else -> NETWORK_UNKNOWN
                }
            }
        }

    private fun TelephonyManager.getMobileNetworkType(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return when (dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_GSM -> NETWORK_2G

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> NETWORK_3G

                TelephonyManager.NETWORK_TYPE_LTE,
                TelephonyManager.NETWORK_TYPE_IWLAN -> NETWORK_4G

                TelephonyManager.NETWORK_TYPE_NR -> NETWORK_5G
                else -> NETWORK_UNKNOWN
            }
        } else {
            @Suppress("DEPRECATION")
            return when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G

                TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G

                TelephonyManager.NETWORK_TYPE_NR -> NETWORK_5G

                else -> NETWORK_UNKNOWN
            }
        }
    }

    private val screenResolution: ScreenResolution
        get() {
            val rect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowManager = windowManager ?: return ScreenResolution.empty()
                windowManager.currentWindowMetrics.bounds
            } else {
                val display = windowManager?.defaultDisplay ?: return ScreenResolution.empty()
                val size = Point().apply { display.getSize(this) }
                Rect(0, 0, size.x, size.y)
            }

            return ScreenResolution(width = rect.width(), height = rect.height())
        }

    private val usedRam: Long?
        get() {
            val activityManager = activityManager ?: return null
            return activityManager.runningAppProcesses.foldRight(0L) { p, acc ->
                val memory = activityManager.getProcessMemoryInfo(intArrayOf(p.pid))
                acc + memory.sumOf { it.totalPss }
            } / 1024
        }

    private val totalRam: Long?
        get() {
            val activityManager = activityManager ?: return null
            val memoryInfo = ActivityManager.MemoryInfo().apply {
                activityManager.getMemoryInfo(this)
            }
            return (memoryInfo.totalMem / 1024) / 1024
        }

    private val freeDiskPercentage: Long
        get() {
            val rootDirectory = Environment.getRootDirectory()
            val fileSystemData = StatFs(rootDirectory.path)
            val blockSize = fileSystemData.blockSizeLong
            val totalSize = fileSystemData.blockCountLong * blockSize
            val availableSize = fileSystemData.availableBlocksLong * blockSize
            return availableSize * 100 / totalSize
        }

    private val screenOrientation: String
        get() {
            return if ((context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                ORIENTATION_LANDSCAPE
            } else {
                ORIENTATION_PORTRAIT
            }
        }

    private fun getBatteryStatus(): BatteryStatus {
        return BatteryStatus(
            percentage = batteryPercentage,
            isCharging = isBatteryCharging
        )
    }

    private fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun getRamStatus(): RamStatus {
        return RamStatus(
            total = totalRam ?: return RamStatus.empty(),
            used = usedRam ?: return RamStatus.empty()
        )
    }

    companion object {
        private const val OS_NAME: String = "android"
        private const val TABLET: String = "tablet"
        private const val MOBILE: String = "mobile"
        private const val NETWORK_UNKNOWN: String = "unknown"
        private const val NETWORK_WIFI: String = "wifi"
        private const val NETWORK_ETHERNET: String = "ethernet"
        private const val NETWORK_2G: String = "gprs"
        private const val NETWORK_3G: String = "3g"
        private const val NETWORK_4G: String = "4g"
        private const val NETWORK_5G: String = "5g"
        private const val ORIENTATION_PORTRAIT: String = "portrait"
        private const val ORIENTATION_LANDSCAPE: String = "landscape"
    }
}
