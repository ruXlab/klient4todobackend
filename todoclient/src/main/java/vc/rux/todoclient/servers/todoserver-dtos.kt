package vc.rux.todoclient.servers

import com.fasterxml.jackson.annotation.JsonProperty

internal data class TodoServerResponse(
    @JsonProperty("description")
    val description: String,

    @JsonProperty("sourcecode_url")
    val sourceCodeUrl: String,

    @JsonProperty("live_url")
    val liveUrl: String,

    @JsonProperty("tags")
    val tags: List<String>
)
