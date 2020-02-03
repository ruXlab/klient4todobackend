package vc.rux.todoclient.servers

import retrofit2.http.GET
import java.io.InputStream

interface IServerListNetworkApiService {
    @GET("/TodoBackend/todo-backend-site/master/data/implementations.yaml")
    fun serverList(): InputStream
}