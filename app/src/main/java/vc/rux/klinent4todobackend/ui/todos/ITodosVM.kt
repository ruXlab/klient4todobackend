package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications
import vc.rux.todoclient.todoclient.Todo

interface ITodosVM: IHasSnackbarNotifications {
    val todos: LiveData<Loadable<List<Todo>>>
    val splashMessage: LiveData<Int?>

    fun reload(isForced: Boolean)
}