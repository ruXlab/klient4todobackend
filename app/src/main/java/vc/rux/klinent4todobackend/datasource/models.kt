package vc.rux.klinent4todobackend.datasource

import androidx.databinding.BaseObservable

inline class TodoId(val value: String)

data class TodoModel(
    val id: TodoId,
    var title: String,
    val completed: Boolean,
    val order: Long,
    val state: TodoModelState = TodoModelState.READY
) : BaseObservable()

typealias TodoModels = List<TodoModel>

enum class TodoModelState {
    READY, UPDATING
}
