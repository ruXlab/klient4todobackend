package vc.rux.klinent4todobackend.ui.todos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.datasource.startLoadable
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.todoclient.ITodoClient
import vc.rux.todoclient.todoclient.Todo

class TodosVM(private val todoClient: ITodoClient) : ITodosVM, ViewModel() {
    private val _snackbarMessage = MutableLiveData<Event<SnackbarNotification?>>(null)
    private val _todos = MutableLiveData<Loadable<List<Todo>>>(Loadable.Loading)

    override val todos: LiveData<Loadable<List<Todo>>> get() = _todos
    override val snackbarMessage: LiveData<Event<SnackbarNotification?>> get() = _snackbarMessage

    override fun reload(isForced: Boolean) {
        viewModelScope.launch {
            val result = _todos.startLoadable { todoClient.all() }
            if (result is Loadable.Error) {
                val notif = SnackbarNotification(
                    R.string.error_loading_todos,
                    stringParams = listOf(result.exception.localizedMessage)
                )
                _snackbarMessage.postValue(Event(notif))
            }

            Unit
        }
    }

}