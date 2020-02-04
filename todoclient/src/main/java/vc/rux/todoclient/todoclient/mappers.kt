package vc.rux.todoclient.todoclient

internal fun TodoResponse.toDto(): Todo = Todo(
    title = title,
    completed = completed,
    id = id,
    order = order
)
