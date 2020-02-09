package vc.rux.klinent4todobackend.datasource

data class TodoModel(
    val title: String,
    val completed: Boolean,
    val order: Long,
    val id: String,
    val state: TodoModelState = TodoModelState.READY
)

typealias TodoModels = List<TodoModel>

enum class TodoModelState {
    READY, UPDATING
}
