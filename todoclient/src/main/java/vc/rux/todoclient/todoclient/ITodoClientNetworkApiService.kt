package vc.rux.todoclient.todoclient

import retrofit2.http.*


internal interface ITodoClientNetworkApiService {
    @GET("/todos")
    suspend fun all(): List<TodoResponse>

    @GET("/todos/{id}")
    suspend fun byId(@Path("id") id: String): TodoResponse

    @DELETE("/todos/{id}")
    suspend fun delete(@Path("id") id: String)

    @POST("/todos")
    suspend fun create(@Body newTodo: TodoCreateRequest): TodoResponse

    @PATCH("/todos/{id}")
    suspend fun update(@Path("id") id: String, @Body updates: TodoUpdateRequest): TodoResponse
}
