package vc.rux.todoclient.servers

interface IServerListApi {
    fun listAllTodoServers(): List<TodoServer>
}