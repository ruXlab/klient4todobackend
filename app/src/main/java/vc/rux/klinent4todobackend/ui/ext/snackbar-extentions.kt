package vc.rux.klinent4todobackend.ui.ext

import android.view.View
import com.google.android.material.snackbar.Snackbar
import vc.rux.klinent4todobackend.misc.SnackbarNotification

fun SnackbarNotification.createSnackbar(view: View): Snackbar {
    val message = view.context.getString(this.stringResId, *this.stringParams.toTypedArray())
    return Snackbar.make(view, message, Snackbar.LENGTH_LONG)
}

fun View.createSnackbar(snackbarNotification: SnackbarNotification): Snackbar {
    return snackbarNotification.createSnackbar(this)
}
