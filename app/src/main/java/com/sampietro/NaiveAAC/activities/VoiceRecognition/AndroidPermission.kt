package com.sampietro.NaiveAAC.activities.VoiceRecognition

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object AndroidPermission : Activity() {
    //
    const val ID_REQUEST_PERMISSION_RECORD_AUDIO = 1

    //
    // we check if the permission RECORD_AUDIO is granted
    @JvmStatic
    fun checkPermission(context: Context) {
        val activity = context as Activity?
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // the permission has not yet been granted
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                ID_REQUEST_PERMISSION_RECORD_AUDIO
            )
        }
    } //
}