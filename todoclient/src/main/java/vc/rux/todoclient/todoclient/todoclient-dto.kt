package vc.rux.todoclient.todoclient

import com.fasterxml.jackson.annotation.JsonProperty


data class TodoCreateRequest(
    @JsonProperty("title")
    val title: String,
    @JsonProperty("completed")
    val completed: Boolean,
    @JsonProperty("order")
    val order: Long
)

data class TodoResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("completed")
    val completed: Boolean,
    @JsonProperty("order")
    val order: Long
)

data class TodoUpdateCompleted(
    @JsonProperty("completed")
    val completed: Boolean
)