package vc.rux.klinent4todobackend.misc

import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

data class SnackbarNotification(
    @StringRes val stringResId: Int,
    val stringParams: List<String> = emptyList(),
    val duration: Int = Snackbar.LENGTH_LONG
)