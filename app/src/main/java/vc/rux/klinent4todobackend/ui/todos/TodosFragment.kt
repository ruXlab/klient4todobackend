package vc.rux.klinent4todobackend.ui.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import vc.rux.klinent4todobackend.databinding.FragmentTodosBinding
import vc.rux.klinent4todobackend.misc.Loadable
import vc.rux.todoclient.todoclient.ITodoClient

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TodosFragment : DaggerFragment() {
    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var todoClient: ITodoClient

//    private val serversViewModel by viewModels<TodoServersVM> { vmFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = TodosVM(todoClient)
        vm.reload(false)
        val binding = FragmentTodosBinding.inflate(inflater, container, false).also {
            it.vm = vm
            it.lifecycleOwner = viewLifecycleOwner
        }

        val todosAdapter = TodosAdapter(vm)
        binding.fragmentTodoList.adapter = todosAdapter

        vm.todos.observe(viewLifecycleOwner) {
            if (it !is Loadable.Success) return@observe
            todosAdapter.submitList(it.data)
            println(it.data)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(layoutInflater.context, "-- ${requireArguments().get(PARAM_SERVERURL)} $view", Toast.LENGTH_LONG).show()


//
//        view.findViewById<TextView>(R.id.textview_second).text =
//                getString(R.string.hello_second_fragment, "args.myArg")
//
//        view.findViewById<Button>(R.id.button_second).setOnClickListener {
// //            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }
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
