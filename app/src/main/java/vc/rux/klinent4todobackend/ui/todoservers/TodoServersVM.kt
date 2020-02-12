package vc.rux.klinent4todobackend.ui.todoservers

import android.util.MutableDouble
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.datasource.startLoadable
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.servers.IServerListApi
import vc.rux.todoclient.servers.TodoServer

class TodoServersVM(
    private val api: IServerListApi
) : ViewModel(), ITodoServersVM {
    override val filters: LiveData<Set<String>> get() = _filters
    override val todoServers: LiveData<Loadable<List<TodoServer>>> get() = _todoServers
    override val serverSelectedEvent: LiveData<Event<TodoServer>?> get() = _selectedServer
    override val snackbarMessage: LiveData<Event<SnackbarNotification?>> get() = _snackbarMessage
    override val noDataSplash: LiveData<Int?> get() = _todoServers.map { loadable ->
        when (loadable) {
            is Loadable.Error -> R.string.msg_no_data_is_due_to_error_need_refresh
            else -> null
        }
    }


    private val _todoServers = MutableLiveData<Loadable<List<TodoServer>>>(Loadable.Loading)
    private val _filters = MutableLiveData<Set<String>>(emptySet())
    private val _selectedServer = MutableLiveData<Event<TodoServer>?>(null)
    private val _snackbarMessage = MutableLiveData<Event<SnackbarNotification?>>(Event(null))

    override fun addFilter(tag: String) = _filters.postValue(_filters.value.orEmpty() + tag)

    override fun removeFilter(tag: String) = _filters.postValue(_filters.value.orEmpty() - tag)

    override fun removeFilters() = _filters.postValue(emptySet())


    init {
    }

    override fun reloadServerList(isForced: Boolean) {
        if (_todoServers.value is Loadable.Success && !isForced)
            return
        viewModelScope.launch {
            _filters.postValue(emptySet())
            val result = _todoServers.startLoadable { api.listAllTodoServers() }
            if (result is Loadable.Error) {
                logger.error("Can't reload server list", result.exception)
                val notif = SnackbarNotification(
                    R.string.error_loading_server_list,
                    stringParams = listOf(result.exception.localizedMessage)
                )
                _snackbarMessage.postValue(Event(notif))
            }
        }
    }

    override fun onServerSelected(todoServer: TodoServer) {
        _selectedServer.postValue(Event(todoServer))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TodoServersVM::class.java)
    }
}
