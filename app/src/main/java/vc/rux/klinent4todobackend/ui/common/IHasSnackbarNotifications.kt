package vc.rux.klinent4todobackend.ui.common

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.servers.TodoServer

interface IHasSnackbarNotifications {
    val snackbarMessage: LiveData<Event<SnackbarNotification?>>
}
