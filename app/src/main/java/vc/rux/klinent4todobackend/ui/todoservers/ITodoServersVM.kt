package vc.rux.klinent4todobackend.ui.todoservers

import androidx.lifecycle.LiveData
import vc.rux.klinent4todobackend.misc.Event
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.IHasSnackbarNotifications
import vc.rux.todoclient.servers.TodoServer

interface ITodoServersVM : IHasSnackbarNotifications {
    val filters: LiveData<Set<String>>
    val todoServers: LiveData<Loadable<List<TodoServer>>>
    val serverSelectedEvent: LiveData<Event<TodoServer>>
    val noDataSplash: LiveData<Int?>

    fun addFilter(tag: String)
    fun removeFilter(tag: String)
    fun removeFilters()

    fun reloadServerList(isForced: Boolean = false)

    fun onServerSelected(todoServer: TodoServer)
}
