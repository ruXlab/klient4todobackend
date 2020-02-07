package vc.rux.todoclient.servers

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import java.io.InputStream

interface IServerListNetworkApiService {
    @GET("/TodoBackend/todo-backend-site/master/data/implementations.yaml")
    suspend fun serverList(): ResponseBody
}
