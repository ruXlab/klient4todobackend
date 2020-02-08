package vc.rux.klinent4todobackend.ui.todos

import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import vc.rux.klinent4todobackend.InstantExecutorExtension
import vc.rux.klinent4todobackend.SurrogateMainCoroutineExtension
import vc.rux.klinent4todobackend.getOrAwaitForCondition
import vc.rux.klinent4todobackend.getOrAwaitValue
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.todoclient.Todo
import vc.rux.todoclient.todoclient.TodoClient
import java.util.*

@ExtendWith(value = [MockKExtension::class, InstantExecutorExtension::class, SurrogateMainCoroutineExtension::class])
class TodosVMTest {
    @MockK
    private lateinit var todoClient: TodoClient

    @InjectMockKs
    private lateinit var vm: TodosVM

    @Test
    fun checkInitialState() {
        assertThat(vm.todos.getOrAwaitValue())
            .isInstanceOf(Loadable.Loading::class.java)
    }

    @Test
    fun refreshCausesNetworkCall() {
        // given
        val todo = makeTodo("boo")
        coEvery { todoClient.all() } returns listOf(todo)

        // when
        vm.reload(true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)
            .extracting<List<Todo>> { (it as Loadable.Success).data }
            .isEqualTo(listOf(todo))
    }

    private fun makeTodo(
        title: String = "Title ${UUID.randomUUID()}",
        completed: Boolean = false,
        order: Long = 0,
        id: String = UUID.randomUUID().toString()
    ) = Todo(title, completed, order, id)

}
