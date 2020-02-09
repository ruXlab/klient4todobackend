package vc.rux.klinent4todobackend.datasource

inline class TodoId(val value: String)

data class TodoModel(
    val id: TodoId,
    val title: String,
    val completed: Boolean,
    val order: Long,
    val state: TodoModelState = TodoModelState.READY
)

typealias TodoModels = List<TodoModel>

enum class TodoModelState {
    READY, UPDATING
}
