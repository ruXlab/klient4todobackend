package vc.rux.klinent4todobackend.ui.todoservers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.databinding.FragmentTodoServersBinding
import vc.rux.klinent4todobackend.datasource.TodoServersVM
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.servers.ServerListApi

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TodoServersFragment : Fragment() {

    lateinit var layoutBinding: FragmentTodoServersBinding
    private val serversViewModel by viewModels<TodoServersVM> {
        val srvApi = ServerListApi.create("https://raw.githubusercontent.com/")
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TodoServersVM(srvApi) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutBinding = FragmentTodoServersBinding.inflate(inflater, container, false).also {
            it.vm = serversViewModel
        }

        serversViewModel.reloadServerList()

        return layoutBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        layoutBinding.lifecycleOwner = viewLifecycleOwner

        val adapter =
            TodoServersAdapter(
                serversViewModel
            )
        layoutBinding.todoServerList.adapter = adapter
        serversViewModel.todoServers.observe(this) {
            println(it)
            if (it is Loadable.Success) adapter.submitList(it.data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            //            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment("From FirstFragment")
//            findNavController().navigate(action)
        }
    }
}
