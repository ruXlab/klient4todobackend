package vc.rux.klinent4todobackend.ui.todoservers

import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.lang.Exception
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import vc.rux.klinent4todobackend.*
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.servers.IServerListApi
import vc.rux.todoclient.servers.TodoServer

@ExperimentalCoroutinesApi
@ExtendWith(value = [MockKExtension::class, InstantExecutorExtension::class, SurrogateMainCoroutineExtension::class])
internal class TodoServersVMTest {
    @MockK
    private lateinit var api: IServerListApi

    @InjectMockKs
    private lateinit var vm: TodoServersVM

    private val todoRecord = TodoServer(
        "Totoro",
        "Big totoro framework",
        "http://todotodoro",
        "totorohuburu",
        listOf("totoro")
    )

    @Test
    fun initialStateForTheTodoServerListIsLoading() {
        assertThat(vm.todoServers.getOrAwaitValue())
            .isInstanceOf(Loadable.Loading::class.java)
        assertThat(vm.filters.getOrAwaitValue())
            .isEmpty()
        assertThat(vm.snackbarMessage.getOrAwaitValue().peekContent())
            .isNull()
        assertThat(vm.serverSelectedEvent.getOrAwaitValue())
            .isNull()
    }

    @Test
    fun nonForcedRefreshCausesSingleApiCallAndStateChanges() {
        // given
        val todoRecord = TodoServer("a", "b", "c", "d", listOf("e"))
        coEvery { api.listAllTodoServers() } returns listOf(todoRecord)

        // when
        vm.reloadServerList(false)

        // then
        assertThat(vm.noDataSplash.getOrAwaitValue()).isNull()

        val loadable = vm.todoServers.getOrAwaitValue()
        assertThat(loadable).isInstanceOf(Loadable.Success::class.java)
        assertThat((loadable as Loadable.Success<List<TodoServer>>).data)
            .containsOnly(todoRecord)
    }

    @Test
    fun networkErrorCausesErrorDisplay() {
        // given
        coEvery { api.listAllTodoServers() } throws UnknownHostException("boo!")

        // when
        vm.reloadServerList(false)

        // then
        assertThat(vm.todoServers.getOrAwaitForCondition { it is Loadable.Error })
            .isInstanceOf(Loadable.Error::class.java)
            .extracting<Exception> { (it as Loadable.Error).exception }
            .isInstanceOf(UnknownHostException::class.java)

        assertThat(vm.noDataSplash.getOrAwaitValue())
            .isNotNull()
            .isEqualTo(R.string.msg_no_data_is_due_to_error_need_refresh)

        assertThat(vm.snackbarMessage.getOrAwaitValue())
            .isNotNull()
            .extracting<SnackbarNotification> { it.peekContent() }
            .isNotNull()
            .hasFieldOrPropertyWithValue(
                SnackbarNotification::stringResId.name,
                R.string.error_loading_server_list
            )
    }
}
