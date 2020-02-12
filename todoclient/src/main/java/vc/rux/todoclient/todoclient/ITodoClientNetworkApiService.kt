package vc.rux.todoclient.todoclient

import retrofit2.http.*


internal interface ITodoClientNetworkApiService {
    @GET(".")
    suspend fun all(): List<TodoResponse>

    @GET("{id}")
    suspend fun byId(@Path("id") id: String): TodoResponse

    @DELETE("{id}")
    suspend fun delete(@Path("id") id: String)

    @POST(".")
    suspend fun create(@Body newTodo: TodoCreateRequest): TodoResponse

    @PATCH("{id}")
    suspend fun update(@Path("id") id: String, @Body updates: TodoUpdateRequest): TodoResponse
}
