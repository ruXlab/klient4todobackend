package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.datasource.TodoId
import vc.rux.klinent4todobackend.datasource.TodoModels
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications

interface ITodosVM : IHasSnackbarNotifications {
    val todos: LiveData<Loadable<TodoModels>>
    val splashMessage: LiveData<Int?>

    fun check(todoId: TodoId, isChecked: Boolean)
    fun reload(isForced: Boolean)
    fun delete(id: TodoId)
}
