package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.datasource.TodoModel
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications

interface ITodosVM : IHasSnackbarNotifications {
    val todos: LiveData<Loadable<List<TodoModel>>>
    val splashMessage: LiveData<Int?>

    fun reload(isForced: Boolean)
}
