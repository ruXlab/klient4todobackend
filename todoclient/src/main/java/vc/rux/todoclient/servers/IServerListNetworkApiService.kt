package vc.rux.todoclient.servers

import java.io.InputStream
import retrofit2.http.GET

interface IServerListNetworkApiService {
    @GET("/TodoBackend/todo-backend-site/master/data/implementations.yaml")
    fun serverList(): InputStream
}
