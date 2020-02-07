package vc.rux.klinent4todobackend.ui.todoservers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vc.rux.klinent4todobackend.databinding.TodoServerItemBinding
import vc.rux.todoclient.servers.TodoServer

class TodoServersAdapter(
    private val viewModel: ITodoServersVM
) : ListAdapter<TodoServer, TodoServersAdapter.TodoServerVH>(
    TodoServerDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoServerVH =
        TodoServerVH.from(viewModel, parent)

    override fun onBindViewHolder(holder: TodoServerVH, position: Int) =
        holder.bind(getItem(position))


    class TodoServerVH private constructor(
        private val eventSink: ITodoServersVM,
        private val binding: TodoServerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoServer) {
            binding.todoServer = item
            binding.eventSink = eventSink
            binding.executePendingBindings()
        }

        companion object {
            fun from(eventSink: ITodoServersVM, parent: ViewGroup): TodoServerVH {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TodoServerItemBinding.inflate(layoutInflater, parent, false)

                return TodoServerVH(eventSink, binding)
            }
        }
    }

    private class TodoServerDiffCallback : DiffUtil.ItemCallback<TodoServer>() {
        override fun areItemsTheSame(oldItem: TodoServer, newItem: TodoServer): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: TodoServer, newItem: TodoServer): Boolean =
            oldItem == newItem
    }
}
