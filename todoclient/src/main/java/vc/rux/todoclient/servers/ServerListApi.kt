package vc.rux.todoclient.servers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerListApi(
    private val networkApiService: IServerListNetworkApiService
) : IServerListApi {
    private val objMapper by lazy { ObjectMapper(YAMLFactory()) }

    override suspend fun listAllTodoServers(): List<TodoServer> = withContext(Dispatchers.IO) {
        objMapper.readValue(
            networkApiService.serverList(),
            jacksonTypeRef<Map<String, TodoServerResponse>>()
        ).map { it.value.toDto(name = it.key) }
    }
}
