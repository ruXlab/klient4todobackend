package vc.rux.klinent4todobackend.datasource

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.servers.IServerListApi
import vc.rux.todoclient.servers.TodoServer

class TodoServersVM(private val api: IServerListApi) : ViewModel(), ITodoServersVM {
    override val filters: LiveData<Set<String>> get() = _filters
    override val todoServers: LiveData<Loadable<List<TodoServer>>> get() = _todoServers

    private val _filters = MutableLiveData<Set<String>>()
    private val _todoServers = MutableLiveData<Loadable<List<TodoServer>>>().apply { value = (Loadable.Loading) }

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
            _todoServers.startLoadable { api.listAllTodoServers() }
        }
    }
}
