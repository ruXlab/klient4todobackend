package vc.rux.todoclient.servers

interface IServerListApi {
    suspend fun listAllTodoServers(): List<TodoServer>
}
