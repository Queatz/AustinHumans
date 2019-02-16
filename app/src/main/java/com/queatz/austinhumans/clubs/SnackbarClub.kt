package com.queatz.austinhumans.clubs

import android.app.Activity
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.queatz.austinhumans.R
import com.queatz.on.On

class SnackbarClub(private val on: On) {
    fun show(@StringRes resId: Int) {
        Snackbar.make((on<ContextClub>().context as Activity).findViewById(R.id.content), resId, Snackbar.LENGTH_SHORT)
                .show()
    }
}