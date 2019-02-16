package com.queatz.austinhumans.clubs

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.queatz.austinhumans.PERMISSION_REQUEST_CODE
import com.queatz.on.On

class PermissionClub(private val on: On) {

    private var callbackFor: String?  = null
    private var callback: (() -> Unit)? = null

    fun obtain(permissionName: String, callback: () -> Unit) {
        this.callback = null
        callbackFor = null

        if (has(permissionName)) {
            callback.invoke()
        } else {
            if (on<ContextClub>().context is Activity) {
                this.callback = callback
                callbackFor = permissionName
                ActivityCompat.requestPermissions(on<ContextClub>().context as Activity, arrayOf(permissionName), PERMISSION_REQUEST_CODE)
            }
        }
    }

    fun has(permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(on<ContextClub>().context, permissionName) == PERMISSION_GRANTED
    }

    fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CODE) return
        for (i in 0 until permissions.size) {
            if (permissions[i] == callbackFor && grantResults[i] == PERMISSION_GRANTED) {
                callback?.invoke()
            }
        }
    }
}