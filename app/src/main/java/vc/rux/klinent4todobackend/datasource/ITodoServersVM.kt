package vc.rux.klinent4todobackend.datasource

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.servers.TodoServer

interface ITodoServersVM {
    val filters: LiveData<Set<String>>
    val todoServers: LiveData<Loadable<List<TodoServer>>>

    fun addFilter(tag: String)
    fun removeFilter(tag: String)
    fun removeFilters()

    fun reloadServerList(isForced: Boolean = false)
}
