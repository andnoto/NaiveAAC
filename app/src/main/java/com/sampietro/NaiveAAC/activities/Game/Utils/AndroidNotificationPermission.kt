package com.sampietro.NaiveAAC.activities.Game.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object AndroidNotificationPermission : Activity() {
    //
    const val ID_REQUEST_PERMISSION_POST_NOTIFICATIONS = 1

    //
    // we check if the permission RECORD_AUDIO is granted
    @JvmStatic
    fun checkPermission(context: Context) {
        val activity = context as Activity?
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // the permission has not yet been granted
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                ID_REQUEST_PERMISSION_POST_NOTIFICATIONS
            )
        }
    } //
}