package vc.rux.todoclient.todoclient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonProperty

internal data class TodoCreateRequest(
    @JsonProperty("title")
    val title: String,

    @JsonProperty("completed")
    val completed: Boolean,

    @JsonProperty("order")
    val order: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TodoResponse(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("completed")
    val completed: Boolean,

    @JsonProperty("order")
    val order: Long
)

@JsonInclude(NON_NULL)
internal data class TodoUpdateRequest(
    @JsonProperty("title")
    val title: String? = null,

    @JsonProperty("completed")
    val completed: Boolean? = null,

    @JsonProperty("order")
    val order: Long? = null
)
