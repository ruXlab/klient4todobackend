package vc.rux.todoclient.servers

data class TodoServer(
    val name: String,
    val description: String,
    val liveServerUrl: String,
    val sourceCodeUrl: String,
    val tags: List<String>
)
