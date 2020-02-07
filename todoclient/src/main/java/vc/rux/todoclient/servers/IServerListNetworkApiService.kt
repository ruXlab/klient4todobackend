package vc.rux.todoclient.servers

import okhttp3.ResponseBody
import retrofit2.http.GET

interface IServerListNetworkApiService {
    @GET("/TodoBackend/todo-backend-site/master/data/implementations.yaml")
    suspend fun serverList(): ResponseBody
}
