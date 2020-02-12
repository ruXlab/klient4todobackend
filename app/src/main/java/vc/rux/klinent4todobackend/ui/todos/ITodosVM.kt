package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.datasource.TodoId
import vc.rux.klinent4todobackend.datasource.TodoModels
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications

interface ITodosVM : IHasSnackbarNotifications {
    val todos: LiveData<Loadable<TodoModels>>
    val splashMessage: LiveData<Int?>

    fun check(todoId: TodoId, isCompleted: Boolean)
    fun reload(isForced: Boolean)
    fun delete(id: TodoId)
    fun create(title: String, isCompleted: Boolean = false, order: Long = 0)

    // workaround due to invisibility of the mangled method names
    // see https://kotlinlang.org/docs/reference/inline-classes.html#mangling
    fun check(todoStringId: String, isCompleted: Boolean) =
            check(TodoId(todoStringId), isCompleted)
}

