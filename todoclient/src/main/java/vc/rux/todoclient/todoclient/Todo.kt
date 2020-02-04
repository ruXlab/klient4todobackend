package vc.rux.todoclient.todoclient

data class Todo (
    val title: String,
    val completed: Boolean,
    val order: Long,
    val id: String
)