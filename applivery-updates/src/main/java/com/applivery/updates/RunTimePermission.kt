package com.applivery.updates

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class RunTimePermission(private var context: Context) {

    lateinit var permissionCallback: PermissionCallback

    interface PermissionCallback {

        fun onGranted()
        fun onDenied()
    }

    /**
     * TODO request for permission & set callback
     *
     * @param arrPermisionName List of permissions
     * @param permissionCallback provided callback to update about permissions
     */
    fun requestPermission(arrPermisionName: List<String>, permissionCallback: PermissionCallback) {
        this.permissionCallback = permissionCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkAllPermisionGranted(arrPermisionName)) {
                (context as Activity).requestPermissions(
                    arrPermisionName.toTypedArray(),
                    PERMISION_REQUEST
                )
            } else {
                permissionCallback.onGranted()
            }
        } else {
            permissionCallback.onGranted()
        }
    }

    /**
     *
     * @param arrPermisionName array of permissions to check if granted or not
     */
    private fun checkAllPermisionGranted(arrPermisionName: List<String>): Boolean {
        for (i in arrPermisionName.indices) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    arrPermisionName[i]
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * TODO requested permission result passing throgh activity filter permitted permissions & set to callback
     *
     * @param grantResults list of allowed & not granted permissions
     */
    fun onRequestPermissionsResult(grantResults: IntArray) {
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED && null != permissionCallback) {
                permissionCallback.onGranted()

            } else {
                permissionCallback.onDenied()
                break
            }
        }
    }
}