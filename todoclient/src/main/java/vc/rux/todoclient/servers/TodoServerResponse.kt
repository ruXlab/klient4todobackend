package vc.rux.todoclient.servers

private inline fun <reified T: Any> Map<String, Any>?.getSafe(key: String): T =
    this?.get(key) as T? ?: throw FormatException()

internal fun todoServerEntityFactory(name: String, properties: Map<String, Any>?): TodoServer {
    return TodoServer(
        name = name,
        description = properties.getSafe("description"),
        tags = properties.getSafe("tags"),
        liveServerUrl = properties.getSafe("live_url"),
        sourceCodeUrl = properties.getSafe("sourcecode_url")
    )
}

class FormatException: Exception("Can't read property, it neither exist nor conform expected format")
