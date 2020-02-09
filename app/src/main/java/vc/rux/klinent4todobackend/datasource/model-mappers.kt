package vc.rux.klinent4todobackend.datasource

import vc.rux.todoclient.todoclient.Todo

fun Todo.toModel(state: TodoModelState = TodoModelState.READY) = TodoModel(
    title, completed, order, id, state
)

fun Iterable<Todo>.toModel() = this.map { it.toModel() }
