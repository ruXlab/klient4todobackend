package vc.rux.todoclient.todoclient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TodoClient internal constructor(
    private val apiInterface: ITodoClientNetworkApiService
) : ITodoClient {
    override suspend fun create(title: String, order: Long, completed: Boolean): Todo =
        withContext(Dispatchers.IO) {
            apiInterface.create(TodoCreateRequest(title, completed, order)).toDto()
        }

    override suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        apiInterface.delete(id)
    }

    override suspend fun update(
        id: String,
        title: String?,
        order: Long?,
        completed: Boolean?
    ): Todo = withContext(Dispatchers.IO) {
        apiInterface.update(id, TodoUpdateRequest(title, completed, order)).toDto()
    }

    override suspend fun byId(id: String): Todo? = withContext(Dispatchers.IO) {
        apiInterface.byId(id).toDto()
    }

    override suspend fun all(): List<Todo> = withContext(Dispatchers.IO) {
        apiInterface.all().map(TodoResponse::toDto)
    }

    companion object {
        fun create(retrofit: Retrofit) =
            TodoClient(retrofit.create(ITodoClientNetworkApiService::class.java))

        fun create(baseUrl: String) = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .let { create(it) }
    }
}
