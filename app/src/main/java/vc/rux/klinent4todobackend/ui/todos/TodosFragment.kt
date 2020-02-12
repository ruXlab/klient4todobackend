package vc.rux.klinent4todobackend.ui.todos

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import dagger.android.support.DaggerFragment
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.databinding.FragmentTodosBinding
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.todoclient.ITodoClient
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TodosFragment : DaggerFragment() {
    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var todoClient: ITodoClient

    private val viewModel by lazy { TodosVM(todoClient) }

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

        val todosAdapter = TodosAdapter(viewModel)
        binding.fragmentTodoList.adapter = todosAdapter

        viewModel.todos.observe(viewLifecycleOwner) {
            if (it !is Loadable.Success) return@observe
            todosAdapter.submitList(it.data)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        fun create(serverUrl: String): TodosFragment =
            TodosFragment().also { fragment ->
                fragment.arguments = Bundle().also { bundle ->
                    bundle.putString(PARAM_SERVERURL, serverUrl)
                }
            }

        private const val PARAM_SERVERURL = "serverUrl"
        val FRAGMENT_ID: String = TodosFragment::class.java.name
    }
}
