package vc.rux.todoclient.todoclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.readValues
import kotlinx.coroutines.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class TodoClientIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var client: TodoClient

    private val jackson = jacksonObjectMapper()

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
        assertThat(jackson.readValue<Map<String, String>>(req.body.readUtf8()))
            .containsEntry("completed", "true")
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
        assertThat(jackson.readValue<Map<String, String>>(req.body.readUtf8()))
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
        assertThat(jackson.readValue<Map<String, String>>(req.body.readUtf8()))
            .containsEntry("title", "boo")
            .containsEntry("completed", "false")
            .containsEntry("order", "99")
            .hasSize(3)

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
        assertThat(jackson.readValue<Map<String, String>>(req.body.readUtf8()))
            .containsEntry("title", "title")
            .containsEntry("order", "12345")
            .containsEntry("completed", "true")

        Unit
    }


    @AfterEach
    fun shutDown() {
        mockServer.shutdown()
    }

    suspend fun MockWebServer.takeLastRequest(waitTimeSeconds: Long = 10) =
        withContext(Dispatchers.IO) {
            takeRequest(waitTimeSeconds, TimeUnit.SECONDS)
        }

}