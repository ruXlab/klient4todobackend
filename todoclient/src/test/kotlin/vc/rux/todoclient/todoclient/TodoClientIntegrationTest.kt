package vc.rux.todoclient.todoclient

import com.squareup.moshi.Moshi
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TodoClientIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var client: TodoClient

    private val moshi = Moshi.Builder().build().newBuilder().build()

    @BeforeEach
    fun setUp() {
        mockServer = MockWebServer().also {
            it.start()
        }
        client = TodoClient.create(mockServer.url("/").toString())
    }

    @Test
    fun shouldFetchAllTodos() = runBlocking {
        // given
        mockServer.enqueue(MockResponse().setBody(TODOS_ALL))

        // when
        val todos = client.all()

        // then
        assertThat(todos).hasSize(2)

        val first = todos.single { it.id == "42" }
        assertThat(first.completed).isTrue()
        assertThat(first.order).isEqualTo(0)
        assertThat(first.title).isEqualTo("use kotlin")

        val second = todos.single { it.id == "777" }
        assertThat(second.completed).isFalse()
        assertThat(second.order).isEqualTo(777)
        assertThat(second.title).isEqualTo("Implement UI")

        Unit
    }

    @Test
    fun shouldPerformHttpDeleteRequest() = runBlocking {
        // given
        mockServer.enqueue(MockResponse())

        // when
        client.delete("ID123")

        // then
        val req = mockServer.takeLastRequest()
        assertThat(req.method).isEqualToIgnoringCase("DELETE")
        assertThat(req.path).endsWith("/todos/ID123")

        Unit
    }

    @Test
    fun shouldUpdateCompleteFlagOnlyWhenOtherAreNotProvided() = runBlocking {
        // given
        mockServer.enqueue(MockResponse().setBody(TODOS_SINGLE))

        // when
        client.update("123", completed = true)

        // then
        val req = mockServer.takeLastRequest()
        assertThat(req.path).isEqualTo("/todos/123")

        assertThat(req.mapFromJson())
            .containsEntry("completed", true)
            .doesNotContainKey("order")
            .doesNotContainKey("title")
            .hasSize(1)

        Unit
    }

    @Test
    fun shouldUpdateTitleWhenOtherAreNotProvided() = runBlocking {
        // given
        mockServer.enqueue(MockResponse().setBody(TODOS_SINGLE))

        // when
        client.update("123", title = "boo")

        // then
        val req = mockServer.takeLastRequest()
        assertThat(req.path).isEqualTo("/todos/123")
        assertThat(req.mapFromJson())
            .containsEntry("title", "boo")
            .doesNotContainKey("order")
            .doesNotContainKey("completed")
            .hasSize(1)

        Unit
    }

    @Test
    fun shouldUpdateAllFieldsIfTheyAreNotNull() = runBlocking {
        // given
        mockServer.enqueue(MockResponse().setBody(TODOS_SINGLE))

        // when
        client.update("123", title = "boo", completed = false, order = 99)

        // then
        val req = mockServer.takeLastRequest()
        assertThat(req.method).isEqualToIgnoringCase("PATCH")
        assertThat(req.path).isEqualTo("/todos/123")

        val reqJson = req.fromJson<TodoUpdateRequest>()
        assertThat(reqJson.title).isEqualTo("boo")
        assertThat(reqJson.completed).isFalse()
        assertThat(reqJson.order).isEqualTo(99)

        Unit
    }

    @Test
    fun createPassesAllInformationCorrectly() = runBlocking {
        // given
        mockServer.enqueue(MockResponse().setBody(TODOS_SINGLE))

        // when
        client.create("title", 12345, completed = true)

        // then
        val req = mockServer.takeLastRequest()
        assertThat(req.path).isEqualTo("/todos")
        assertThat(req.method).isEqualToIgnoringCase("POST")

        val reqJson = req.fromJson<TodoCreateRequest>()
        assertThat(reqJson.completed).isTrue()
        assertThat(reqJson.order).isEqualTo(12345)
        assertThat(reqJson.title).isEqualTo("title")

        Unit
    }

    @AfterEach
    fun shutDown() {
        mockServer.shutdown()
    }

    private inline fun <reified T : Any> RecordedRequest.fromJson(): T =
        moshi.adapter<T>(T::class.java).fromJson(this.body)!!

    private fun RecordedRequest.mapFromJson(): Map<String, Any> =
        moshi.adapter<Map<String, Any>>(Map::class.java).fromJson(this.body)!!


    suspend fun MockWebServer.takeLastRequest(waitTimeSeconds: Long = 10) =
        withContext(Dispatchers.IO) {
            takeRequest(waitTimeSeconds, TimeUnit.SECONDS)
        }
}
