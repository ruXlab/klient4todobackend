package vc.rux.klinent4todobackend.ui.todos

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import java.net.UnknownHostException
import java.util.*
import kotlinx.coroutines.Dispatchers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import vc.rux.klinent4todobackend.*
import vc.rux.klinent4todobackend.datasource.*
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.misc.SnackbarNotification
import vc.rux.todoclient.todoclient.Todo
import vc.rux.todoclient.todoclient.TodoClient

@ExtendWith(value = [MockKExtension::class, InstantExecutorExtension::class, SurrogateMainCoroutineExtension::class])
class TodosVMTest {
    @MockK
    private lateinit var todoClient: TodoClient

    @InjectMockKs
    private lateinit var todosStates: ArrayList<Loadable<TodoModels>>

    private lateinit var vm: ITodosVM


    @BeforeEach
    fun beforeEach() {
        vm = TodosVM(todoClient, Dispatchers.Main)
    }

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
    fun whenEmptyListOfTodoIsLoadedSplashIsShown() {
        // given
        coEvery { todoClient.all() } returns emptyList()

        // when
        vm.reload(true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .extracting<TodoModels> { (it as Loadable.Success<TodoModels>).data }
            .isEqualTo(emptyList<TodoModels>())
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isEqualTo(R.string.msg_no_todos_yet_create_new)
    }

    @Test
    fun refreshCausesNetworkCall() {
        // given
        val todo = makeTodo("boo")
        val todoModel = todo.toModel()
        coEvery { todoClient.all() } returns listOf(todo)

        // when
        vm.reload(true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)
            .extracting<TodoModels> { (it as Loadable.Success).data }
            .isEqualTo(listOf(todoModel))
        assertThat(vm.splashMessage.getOrAwaitValue())
            .isNull()
        assertThat(vm.snackbarMessage.getOrAwaitValue())
            .isNull()
    }

    @Test
    fun whenErrorOccurredWhileFetchingTodosMessageIsDisplayed() {
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
            .hasFieldOrPropertyWithValue(
                SnackbarNotification::stringResId.name,
                R.string.error_loading_todos
            )
    }

    @Test
    fun checkCheckbox() {
        // given
        val todoDto = makeTodo("boo", completed = false)
        val newTodoDto = todoDto.copy(completed = true)
        coEvery { todoClient.update(todoDto.id, completed = true) } returns newTodoDto
        coEvery { todoClient.all() } returns listOf(todoDto) andThen listOf(newTodoDto)
        vm.todos.observeForever { todosStates.add(it) }

        // when
        vm.reload(true)
        vm.check(todoDto.toModel().id, true)

        // then
        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)
        coVerify(exactly = 1) { todoClient.update(todoDto.id, completed = true) }
        coVerify(exactly = 2) { todoClient.all() }

        assertThat(todosStates.first { it is Loadable.Success<TodoModels> })
            .extracting<TodoModel> { (it as Loadable.Success<TodoModels>).data.first() }
            .hasFieldOrPropertyWithValue(TodoModel::completed.name, false)
        assertThat(todosStates.last { it is Loadable.Success<TodoModels> })
            .extracting<TodoModel> { (it as Loadable.Success<TodoModels>).data.first() }
            .hasFieldOrPropertyWithValue(TodoModel::completed.name, true)
        val statesSequence = todosStates.filterIsInstance<Loadable.Success<TodoModels>>()
            .map { it.data.single().state }
        assertThat(statesSequence)
            .containsExactly(TodoModelState.READY, TodoModelState.UPDATING, TodoModelState.READY)
    }

    @Test
    fun shouldPerformDelete() {
        // given
        val todoDto = makeTodo("boo", completed = false)
        coEvery { todoClient.delete(todoDto.id) } just Runs
        coEvery { todoClient.all() } returns listOf(todoDto) andThen listOf()

        vm.todos.observeForever { todosStates.add(it) }

        // when
        vm.reload(true)
        vm.delete(todoDto.toModel().id)

        // then
        coVerify(exactly = 1, timeout = 1000L) { todoClient.delete(todoDto.id) }
        coVerify(exactly = 2, timeout = 1000L) { todoClient.all() }

        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)

        val states = todosStates.filterIsInstance<Loadable.Success<TodoModels>>().map { it.data }
        assertThat(states).hasSize(3) // two success states recorded
        assertThat(states[0]).hasSize(1) // first - initial state with single item
        assertThat(states[1]).isEmpty() // second - temp state - locally triggered
        assertThat(states[2]).isEmpty() // third - from the server

        assertThat(todosStates.last())
            .isInstanceOf(Loadable.Success::class.java)
    }

    @Test
    fun whenRemoteDeleteFailsDoRollback() {
        // given
        val todoDto = makeTodo("boo", completed = false)
        coEvery { todoClient.delete(todoDto.id) } throws UnknownHostException("rux.vc")
        coEvery { todoClient.all() } returns listOf(todoDto) andThen listOf(todoDto)

        vm.todos.observeForever { todosStates.add(it) }

        // when
        vm.reload(true)
        vm.delete(todoDto.toModel().id)

        // then
        coVerify(exactly = 1, timeout = 1000L) { todoClient.delete(todoDto.id) }
        coVerify(exactly = 1, timeout = 1000L) { todoClient.all() }

        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)

        val states = todosStates.filterIsInstance<Loadable.Success<TodoModels>>().map { it.data }
        assertThat(states).hasSize(3) // 3 success states recorded
        assertThat(states[0]).hasSize(1) // first - initial state with single item
        assertThat(states[1]).isEmpty() // second - temp state - locally triggered
        assertThat(states[2]).hasSize(1) // third - from the server

        assertThat(todosStates.last())
            .isInstanceOf(Loadable.Success::class.java)
        assertThat(vm.snackbarMessage.getOrAwaitValue()?.peekContent()?.stringResId)
            .isEqualTo(R.string.error_cant_delete_todo)
    }

    @Test
    fun shouldCreateTodo() {
        // given
        val oldTodoDto = makeTodo("old", completed = true)
        lateinit var newTodoDto: Todo
        coEvery { todoClient.all() } returns
                listOf(oldTodoDto) andThen
                { listOf(oldTodoDto, newTodoDto) }
        coEvery { todoClient.create(any(), any(), any()) } answers {
            newTodoDto = Todo("newId", title = firstArg(), order = secondArg(), completed = thirdArg())
            newTodoDto
        }

        vm.todos.observeForever { todosStates.add(it) }

        // when
        vm.reload(true)
        vm.create(title = "buy a milk", order = 42, isCompleted = true)

        // then
        coVerify(exactly = 1, timeout = 2000L) { todoClient.create("buy a milk", 42, true) }
        coVerify(exactly = 2, timeout = 2000L) { todoClient.all() }

        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)

        val states = todosStates.filterIsInstance<Loadable.Success<TodoModels>>().map { it.data }
        assertThat(states).hasSize(3) // two success states recorded
        assertThat(states[0]).hasSize(1) // first - initial state with single previously created item
        assertThat(states[1]) // 2nd - locally updated after successful creation
            .hasSize(2)
            .anySatisfy {
                assertThat(it.id).isEqualTo(TodoId("newId"))
                assertThat(it.state).isEqualTo(TodoModelState.UPDATING)
            }
        assertThat(states[2]) // 3rd - from the server
            .hasSize(2)
            .anySatisfy {
                assertThat(it.id).isEqualTo(TodoId("newId"))
                assertThat(it.state).isEqualTo(TodoModelState.READY)
            }
    }

    @Test
    fun shouldDisplayErrorWhenCreateFailed() {
        // given
        coEvery { todoClient.all() } returns emptyList()
        coEvery { todoClient.create(any(), any(), any()) } throws UnknownHostException("rux.vc")

        vm.todos.observeForever { todosStates.add(it) }

        // when
        vm.reload(true)
        vm.create(title = "title")

        // then
        coVerify(exactly = 1, timeout = 2000L) { todoClient.create("title") }
        coVerify(exactly = 1, timeout = 2000L) { todoClient.all() }

        assertThat(vm.todos.getOrAwaitForCondition { it is Loadable.Success })
            .isInstanceOf(Loadable.Success::class.java)

        assertThat(vm.snackbarMessage.getOrAwaitValue().peekContent()?.stringResId)
            .isEqualTo(R.string.error_cant_create_todo)

        val states = todosStates.filterIsInstance<Loadable.Success<TodoModels>>().map { it.data }
        assertThat(states).hasSize(2) // two success states recorded
        assertThat(states[0]).isEmpty() // first - initial state - no record
        assertThat(states[1]).isEmpty() // second - after failed creation - still no record
    }

    private fun makeTodo(
        title: String = "Title ${UUID.randomUUID()}",
        completed: Boolean = false,
        order: Long = 0,
        id: String = UUID.randomUUID().toString()
    ) = Todo(id, title, completed, order)
}
