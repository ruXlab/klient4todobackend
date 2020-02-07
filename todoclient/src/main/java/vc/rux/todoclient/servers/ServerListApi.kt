package vc.rux.todoclient.servers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yaml.snakeyaml.Yaml
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ServerListApi(
    private val networkApiService: IServerListNetworkApiService
) : IServerListApi {
    @Suppress("UNCHECKED_CAST")
    override suspend fun listAllTodoServers(): List<TodoServer> = withContext(Dispatchers.IO) {
        Yaml()
            .load<Map<String, Any?>>(networkApiService.serverList().byteStream())
            .map { todoServerEntityFactory(it.key, it.value as Map<String, Any>?) }
    }

    companion object {
        fun create(okhttp: Retrofit) =
            ServerListApi(okhttp.create(IServerListNetworkApiService::class.java))

        fun create(baseUrl: String) = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .let { create(it) }
    }
}
