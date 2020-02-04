package vc.rux.todoclient.todoclient


interface ITodoClient {
    suspend fun all(): List<Todo>
    suspend fun byId(id: String): Todo?
    suspend fun create(title: String, order: Long, completed: Boolean = false): Todo
    suspend fun update(id: String, title: String? = null, order: Long? = null, completed: Boolean? = null): Todo
    suspend fun delete(id: String)
}