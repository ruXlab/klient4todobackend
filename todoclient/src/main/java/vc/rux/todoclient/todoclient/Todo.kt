package vc.rux.todoclient.todoclient

data class Todo(
    val id: String,
    val title: String,
    val completed: Boolean,
    val order: Long
)
