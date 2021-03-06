package vc.rux.klinent4todobackend.ui.todos

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.databinding.FragmentTodosBinding
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.BaseFragment
import vc.rux.todoclient.todoclient.ITodoClient


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TodosFragment : BaseFragment() {
    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var todoClient: ITodoClient

    private val paramServerName by lazy {
        arguments?.getString(PARAM_NAME)!!
    }

    private val paramServerUrl by lazy {
        arguments?.getString(PARAM_SERVERURL)!!
    }

    @FlowPreview
    private val viewModel: ITodosVM by viewModels<TodosVM> {
        TodosVM.TodosViewModelFactory(paramServerUrl)
    }

    //    private val serversViewModel by viewModels<TodoServersVM> { vmFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.todoservers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_refresh -> {
            viewModel.reload(true)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.reload(false)
        val binding = FragmentTodosBinding.inflate(inflater, container, false).also {
            it.vm = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        val todosAdapter = TodosAdapter(viewModel, viewLifecycleOwner)
        binding.fragmentTodoList.adapter = todosAdapter

        viewModel.todos.observe(viewLifecycleOwner) {
            if (it !is Loadable.Success) return@observe
            todosAdapter.submitList(it.data)
        }

        viewModel.placeFocusOnTaskId.observe(viewLifecycleOwner) { todoId ->
            if (todoId == null) { // clear focus
                binding.fragmentTodoList.requestFocus()
                return@observe
            }

            // try to set focus
            todosAdapter.getTodoPosition(todoId)
                ?.let { binding.fragmentTodoList.smoothScrollToPosition(it) }

            lifecycleScope.launch(Dispatchers.Main) {
                delay(300L)
                todosAdapter.getTodoPosition(todoId)
                    ?.let { binding.fragmentTodoList.getChildAt(it) }
                    ?.findViewById<EditText>(R.id.todoItemTitle)
                    ?.focusAndShowKeyboard()
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setToolbarTitles(paramServerName, paramServerUrl)
    }

    private fun View.focusAndShowKeyboard() {
        this.requestFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        fun create(name: String, serverUrl: String): TodosFragment =
            TodosFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putString(PARAM_SERVERURL, serverUrl)
                    bundle.putString(PARAM_NAME, name)
                }
            }

        private const val PARAM_SERVERURL = "serverUrl"
        private const val PARAM_NAME = "name"

        val FRAGMENT_ID: String = TodosFragment::class.java.name
    }
}
