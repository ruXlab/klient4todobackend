package vc.rux.klinent4todobackend.ui.todos

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import vc.rux.klinent4todobackend.*
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.todoclient.Todo
import vc.rux.todoclient.todoclient.TodoClient
import java.net.UnknownHostException
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
        assertThat(vm.snackbarMessage.getOrAwaitValue())
            .isNull()
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isNull()
    }

    @Test
    fun whenEmplyListOfTodoIsLoadedSplashIsShown() {
        // given
        coEvery { todoClient.all() } returns emptyList()

        // when
        vm.reload(true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .extracting<List<Todo>> { (it as Loadable.Success<List<Todo>>).data }
            .isEqualTo(emptyList<Todo>())
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isEqualTo(R.string.msg_no_todos_yet_create_new)
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
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isNull()
        assertThat(vm.snackbarMessage.getOrAwaitValue())
            .isNull()
    }

    @Test
    fun whenErrorOccurredErrorMessageIsDisplayed() {
        // given
        coEvery { todoClient.all() } throws UnknownHostException("rux.vc")

        // when
        vm.reload(true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Error })
            .isInstanceOf(Loadable.Error::class.java)
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isEqualTo(R.string.msg_no_data_is_due_to_error_need_refresh)
        assertThat(vm.snackbarMessage.getOrAwaitValue())
            .extracting<SnackbarNotification> { it.peekContent() }
            .hasFieldOrPropertyWithValue(SnackbarNotification::stringResId.name, R.string.error_loading_todos)
    }

    @Test
    fun checkCheckbox() {
        // given
        val todo = makeTodo("boo", completed = false)
        val newTodo = todo.copy(completed = true)
        val todosStates = mutableListOf<Loadable<List<Todo>>>()
        coEvery { todoClient.update(todo.id, completed = true) } returns newTodo
        coEvery { todoClient.all() } returns listOf(todo) andThen listOf(newTodo)
        vm.todos.observeForever {
            todosStates += it
        }

        // when
        vm.reload(true)
        vm.check(todo.id, true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)
        coVerify(exactly = 1) { todoClient.update(todo.id, completed = true) }
        coVerify(exactly = 2) { todoClient.all() }
        assertThat(todosStates.first { it is Loadable.Success<List<Todo>> })
            .extracting<Todo> { (it as Loadable.Success<List<Todo>>).data.first() }
            .hasFieldOrPropertyWithValue(Todo::completed.name, false)
        assertThat(todosStates.last { it is Loadable.Success<List<Todo>> })
            .extracting<Todo> { (it as Loadable.Success<List<Todo>>).data.first() }
            .hasFieldOrPropertyWithValue(Todo::completed.name, true)
    }


    private fun makeTodo(
        title: String = "Title ${UUID.randomUUID()}",
        completed: Boolean = false,
        order: Long = 0,
        id: String = UUID.randomUUID().toString()
    ) = Todo(title, completed, order, id)

}
