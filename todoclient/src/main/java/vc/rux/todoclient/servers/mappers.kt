package vc.rux.todoclient.servers

internal fun TodoServerResponse.toDto(name: String) = TodoServer(
    name = name,
    description = this.description,
    liveServerUrl = this.liveUrl,
    sourceCodeUrl = this.sourceCodeUrl,
    tags = this.tags
)
