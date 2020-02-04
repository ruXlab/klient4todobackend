package vc.rux.todoclient.servers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

class ServerListApi(
    private val networkApiService: IServerListNetworkApiService
) : IServerListApi {
    private val objMapper by lazy { ObjectMapper(YAMLFactory()) }

    override fun listAllTodoServers(): List<TodoServer> =
        objMapper.readValue(networkApiService.serverList(), jacksonTypeRef<Map<String, TodoServerResponse>>())
            .map {
                TodoServer(
                    name = it.key,
                    description = it.value.description,
                    liveServerUrl = it.value.liveUrl,
                    sourceCodeUrl = it.value.sourceCodeUrl,
                    tags = it.value.tags
                )
            }


}