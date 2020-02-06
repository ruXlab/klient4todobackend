package vc.rux.todoclient.todoclient

import com.squareup.moshi.Json

internal data class TodoCreateRequest(
    @Json(name = "title")
    val title: String,

    @Json(name = "completed")
    val completed: Boolean,

    @Json(name = "order")
    val order: Long
)

//@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TodoResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "completed")
    val completed: Boolean,

    @Json(name = "order")
    val order: Long
)

//@JsonInclude(NON_NULL)
internal data class TodoUpdateRequest(
    @Json(name = "title")
    val title: String? = null,

    @Json(name = "completed")
    val completed: Boolean? = null,

    @Json(name = "order")
    val order: Long? = null
)
