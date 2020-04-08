package vc.rux.klinent4todobackend.ui.todoservers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import javax.inject.Inject
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.databinding.FragmentTodoServersBinding
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.common.BaseFragment
import vc.rux.klinent4todobackend.ui.common.ICanAddFragment
import vc.rux.klinent4todobackend.ui.ext.createSnackbar
import vc.rux.klinent4todobackend.ui.todos.TodosFragment

/**
 * List of the "backendtodo" servers
 */
class TodoServersFragment : BaseFragment() {
    lateinit var layoutBinding: FragmentTodoServersBinding

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private val serversViewModel by viewModels<TodoServersVM> { vmFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.todoservers, menu)
    }


    override fun onResume() {
        super.onResume()
        setToolbarTitles(getString(R.string.title_todoservers))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_refresh -> {
            serversViewModel.reloadServerList(true)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = FragmentTodoServersBinding.inflate(inflater, container, false).also {
            it.vm = serversViewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        serversViewModel.reloadServerList()

        return layoutBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        serversViewModel.snackbarMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { snackbarNotification ->
                view?.createSnackbar(snackbarNotification)?.show()
            }
        }

        val adapter = TodoServersAdapter(serversViewModel)
        layoutBinding.todoServerList.adapter = adapter
        serversViewModel.todoServers.observe(this) {
            if (it is Loadable.Success) adapter.submitList(it.data)
        }

        serversViewModel.serverSelectedEvent.observe(this) { event ->
            event?.getContentIfNotHandled()?.let { todoServer ->
                val todosFragment = TodosFragment.create(todoServer.name, todoServer.liveServerUrl)
                (activity as? ICanAddFragment)?.addFragment(todosFragment)
                    ?: throw Exception("Somehow fragment is mounted to the wrong activity")
            }
        }
    }

    companion object {
        fun create() = TodoServersFragment()
        val FRAGMENT_TAG = TodoServersFragment::class.java.canonicalName
    }
}
