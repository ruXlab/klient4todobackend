package vc.rux.todoclient.servers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ServerListApiUnitTest {

    @MockK
    private lateinit var networkApiService: IServerListNetworkApiService

    @InjectMockKs
    private lateinit var api: ServerListApi

    @Test
    fun canParseResponse() = runBlocking {
        // given
        coEvery { networkApiService.serverList() } returns sampleYaml.byteInputStream()

        // when
        val servers = api.listAllTodoServers()

        // then
        coVerify(exactly = 1) { networkApiService.serverList() }
        assertTrue(servers.isNotEmpty())
        assertEquals(2, servers.size)

        val thingA = servers.single { it.name == "ThingA" }
        assertEquals("descrA A A A A", thingA.description)
        assertEquals("https://aaa.com/", thingA.sourceCodeUrl)
        assertEquals("https://aaa.server.com/", thingA.liveServerUrl)
        assertArrayEquals(arrayOf("kotlin", "java"), thingA.tags.toTypedArray())

        val thingB = servers.single { it.name == "Thing B" }
        assertTrue("description B" in thingB.description, "Should have first line of description")
        assertTrue("multi line text" in thingB.description, "Should have second line of description")
        assertEquals("https://bbb.com/", thingB.sourceCodeUrl)
        assertEquals("https://bbb.server.com/v1/todos", thingB.liveServerUrl)
        assertArrayEquals(arrayOf("msdos", "emm386", "keyb"), thingB.tags.toTypedArray())
    }
}
