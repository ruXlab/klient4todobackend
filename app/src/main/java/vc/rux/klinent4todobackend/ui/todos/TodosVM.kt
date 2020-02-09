package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.*
import java.lang.Exception
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.datasource.*
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.todoclient.ITodoClient

class TodosVM(
    private val todoClient: ITodoClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ITodosVM, ViewModel() {
    private val _snackbarMessage = MutableLiveData<Event<SnackbarNotification?>>(null)
    private val _todos = MutableLiveData<Loadable<TodoModels>>(Loadable.Loading)

    private val currentTodos: TodoModels? get() = (_todos.value as? Loadable.Success<TodoModels?>)?.data

    override val todos: LiveData<Loadable<TodoModels>> get() = _todos
    override val snackbarMessage: LiveData<Event<SnackbarNotification?>> get() = _snackbarMessage
    override val splashMessage: LiveData<Int?> = todos.map { loadable ->
        when {
            loadable is Loadable.Error -> R.string.msg_no_data_is_due_to_error_need_refresh
            loadable is Loadable.Success<TodoModels> && loadable.data.isEmpty() -> R.string.msg_no_todos_yet_create_new
            else -> null
        }
    }

    override fun reload(isForced: Boolean) {
        viewModelScope.launch(dispatcher) {
            val result = _todos.startLoadable { todoClient.all().toModel() }
            if (result is Loadable.Error) {
                val notif = SnackbarNotification(
                    R.string.error_loading_todos,
                    stringParams = listOf(result.exception.localizedMessage)
                )
                _snackbarMessage.postValue(Event(notif))
            }
        }
    }

    override fun delete(id: TodoId) {
        viewModelScope.launch(dispatcher) {
            val oldState = currentTodos
            try {
                if (oldState != null)
                    _todos.postValue(Loadable.Success(oldState.filterNot { it.id == id }))
                todoClient.delete(id.value)
                reload(true)
            } catch (ex: Exception) {
                val notif = SnackbarNotification(
                    R.string.error_cant_delete_todo,
                    stringParams = listOf(ex.message.toString())
                )
                _snackbarMessage.postValue(Event(notif))
                if (oldState != null) // revert state back
                    _todos.postValue(Loadable.Success(oldState))
            }
        }
    }

    override fun create(title: String, isCompleted: Boolean, order: Long) {
        viewModelScope.launch(dispatcher) {
            val oldState = currentTodos
            try {
                val newTodo = todoClient.create(title, order, isCompleted)
                if (oldState != null)
                    _todos.postValue(Loadable.Success(oldState + newTodo.toModel(TodoModelState.UPDATING)))
                reload(true)
            } catch (ex: Exception) {
                val notif = SnackbarNotification(
                    R.string.error_cant_create_todo,
                    stringParams = listOf(ex.message.toString())
                )
                _snackbarMessage.postValue(Event(notif))
                if (oldState != null)
                    _todos.postValue(Loadable.Success(oldState))
            }
        }
    }

    override fun check(todoId: TodoId, isCompleted: Boolean) {
        viewModelScope.launch(dispatcher) {
            try {
                val updatedTodo = todoClient.update(todoId.value, completed = isCompleted)
                    .toModel(state = TodoModelState.UPDATING)
                replaceSingleTodoAndPostUpdate(updatedTodo)
                reload(true)
            } catch (ex: Exception) {
                val notif = SnackbarNotification(
                    R.string.error_cant_update_todo,
                    stringParams = listOf(ex.message.toString())
                )
                _snackbarMessage.postValue(Event(notif))
            }
        }
    }

    private fun replaceSingleTodoAndPostUpdate(updatedTodo: TodoModel) {
        currentTodos?.let { oldList ->
            _todos.postValue(Loadable.Success(oldList.map {
                if (it.id == updatedTodo.id) updatedTodo else it
            }))
        }
    }
}
