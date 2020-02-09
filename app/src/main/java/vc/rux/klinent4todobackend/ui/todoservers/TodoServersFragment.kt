package vc.rux.klinent4todobackend.ui.todoservers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.databinding.FragmentTodoServersBinding
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.klinent4todobackend.ui.ext.createSnackbar
import vc.rux.todoclient.servers.ServerListApi

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TodoServersFragment : Fragment() {

    lateinit var layoutBinding: FragmentTodoServersBinding
    private val serversViewModel by viewModels<TodoServersVM> {
        val srvApi = ServerListApi.create("https://raw.githubusercontent.com/")
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoServersVM(srvApi) as T
            }
        }
    }

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
        }

        serversViewModel.reloadServerList()

        return layoutBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        layoutBinding.lifecycleOwner = viewLifecycleOwner

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
