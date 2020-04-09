package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.datasource.*
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.klinent4todobackend.misc.logger
import vc.rux.todoclient.todoclient.ITodoClient

@FlowPreview
class TodosVM(
    private val todoClient: ITodoClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ITodosVM, ViewModel() {
    private val _snackbarMessage = MutableLiveData<Event<SnackbarNotification?>>(null)
    override val snackbarMessage: LiveData<Event<SnackbarNotification?>> get() = _snackbarMessage

    private val _todos = MutableLiveData<Loadable<TodoModels>>(Loadable.Loading)
    override val todos: LiveData<Loadable<TodoModels>> get() = _todos

    private val _placeFocusOnTaskId = MutableLiveData<TodoId?>(null)
    override val placeFocusOnTaskId: LiveData<TodoId?> get() = _placeFocusOnTaskId

    private val _todoIdUnderFocus = MutableLiveData<TodoId?>(null)
    override val todoIdUnderFocus: LiveData<TodoId?> get() = _todoIdUnderFocus

    private val todoUpdates = MutableLiveData<Pair<TodoId, String>>()
    private val currentTodos: TodoModels? get() = (_todos.value as? Loadable.Success<TodoModels?>)?.data

    override val splashMessage: LiveData<Int?> = todos.map { loadable ->
        when {
            loadable is Loadable.Error -> R.string.msg_no_data_is_due_to_error_need_refresh
            loadable is Loadable.Success<TodoModels> && loadable.data.isEmpty() -> R.string.msg_no_todos_yet_create_new
            else -> null
        }
    }

    init {
        viewModelScope.launch(dispatcher) {
            todoUpdates.asFlow()
                .debounce(200)
                .collect { (taskId, newTitle) ->
                    logger.info("Updating $taskId with new title $taskId")
                    val updatedTodo = todoClient.update(taskId.value, title = newTitle)
                    logger.info("Updated todo: $updatedTodo")
                }
        }
    }

    override fun reload(isForced: Boolean) {
        viewModelScope.launch(dispatcher) {
            val result = _todos.startLoadable { todoClient.all().toModel() }
            if (result is Loadable.Error) {
                logger.error("Error while reloading list of todos", result.exception)

                val notif = SnackbarNotification(
                    R.string.error_loading_todos,
                    stringParams = listOf(result.exception.localizedMessage)
                )
                _snackbarMessage.postValue(Event(notif))
            }
        }
    }

    override fun create() {
        val existingEmptyTodo = currentTodos?.firstOrNull { it.title.isEmpty() }
        if (existingEmptyTodo != null) {
            logger.info("create: found todo#${existingEmptyTodo.id} with empty title - no need to create an another todo")
            _placeFocusOnTaskId.postValue(existingEmptyTodo.id)
            return
        }

        viewModelScope.launch(dispatcher) {
            val oldState = currentTodos
            try {
                val order = (oldState?.maxBy { it.order }?.order ?: 0) + 1
                val newTodo = todoClient.create("", order, false)
                val todoModel = newTodo.toModel(TodoModelState.UPDATING)
                viewModelScope.launch(Dispatchers.Main) {
                    _todos.value = Loadable.Success(oldState.orEmpty() + todoModel)
                    _placeFocusOnTaskId.postValue(todoModel.id)
                }
                reload(true)
            } catch (ex: Exception) {
                logger.error("Error while creating todo", ex)
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

    override fun updateTodoTitle(todoId: TodoId, newTitle: String) {
        todoUpdates.value = todoId to newTitle

        (_todos.value as? Loadable.Success<TodoModels>)?.data?.let { items ->
            items.firstOrNull { it.id == todoId }?.let {
                it.title = newTitle
            }
            _todos.value = Loadable.Success(items)
        }
    }

    override fun todoFocusChanged(todoId: TodoId, isFocused: Boolean) {
        _todoIdUnderFocus.postValue(todoId.takeIf { isFocused })

        if (isFocused) return
        val todo = currentTodos?.firstOrNull { it.id == todoId }
            ?: return

        if (todo.title.isEmpty()) {
            logger.info("todoFocusChanged: todoId#${todo.id} had an empty title - removing it..")
            delete(todoId)
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
                logger.error("Error while removing todo", ex)

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
                logger.error("Error while creating todo", ex)
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
                logger.error("Error while updating isCompleted flag", ex)
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

    companion object {
        private val logger = logger<TodosVM>()
    }
}
