package vc.rux.todoclient.servers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yaml.snakeyaml.Yaml

class ServerListApi(
    private val networkApiService: IServerListNetworkApiService
) : IServerListApi {
    @Suppress("UNCHECKED_CAST")
    override suspend fun listAllTodoServers(): List<TodoServer> = withContext(Dispatchers.IO) {
        Yaml()
            .load<Map<String, Any?>>(networkApiService.serverList())
            .map { todoServerEntityFactory(it.key, it.value as Map<String, Any>?) }
    }
}
