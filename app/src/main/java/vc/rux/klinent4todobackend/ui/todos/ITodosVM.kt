package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.datasource.TodoId
import vc.rux.klinent4todobackend.datasource.TodoModels
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications

interface ITodosVM : IHasSnackbarNotifications {
    val todos: LiveData<Loadable<TodoModels>>
    val splashMessage: LiveData<Int?>
    val placeFocusOnTaskId: LiveData<TodoId?>
    val todoIdUnderFocus: LiveData<TodoId?>

    fun check(todoId: TodoId, isCompleted: Boolean)
    fun reload(isForced: Boolean)
    fun delete(id: TodoId)
    fun create(title: String, isCompleted: Boolean = false, order: Long = 0)
    fun create()
    fun updateTodoTitle(todoId: TodoId, newTitle: String)
    fun todoFocusChanged(todoId: TodoId, isFocused: Boolean)

    // workarounds due to invisibility of the mangled method names in java world
    // see https://kotlinlang.org/docs/reference/inline-classes.html#mangling
    fun check(todoStringId: String, isCompleted: Boolean) =
        check(TodoId(todoStringId), isCompleted)

    fun updateTodoTitle(todoStringId: String, newTitle: String) =
        updateTodoTitle(TodoId(todoStringId), newTitle)

    fun todoFocusChanged(todoId: String, isFocused: Boolean) =
        todoFocusChanged(TodoId(todoId), isFocused)

    fun delete(id: String) =
        delete(TodoId(id))
}

