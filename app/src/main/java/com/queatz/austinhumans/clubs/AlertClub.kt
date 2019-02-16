package com.queatz.austinhumans.clubs

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.queatz.on.On

class AlertClub(private val on: On) {
    fun show(@StringRes button: Int) {
        AlertDialog.Builder(on<ContextClub>().context)
                .setPositiveButton(button) { _, _ ->  }
                .show()
    }

    fun show(@StringRes message: Int, @StringRes okButton: Int, @StringRes cancelButton: Int, okCallback: () -> Unit) {
        AlertDialog.Builder(on<ContextClub>().context)
                .setMessage(message)
                .setNegativeButton(cancelButton) { _, _ ->  }
                .setPositiveButton(okButton) { _, _ -> okCallback.invoke() }
                .show()
    }
}