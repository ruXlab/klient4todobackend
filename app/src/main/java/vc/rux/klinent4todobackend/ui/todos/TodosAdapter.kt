package vc.rux.klinent4todobackend.ui.todos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vc.rux.klinent4todobackend.databinding.TodoItemBinding
import vc.rux.klinent4todobackend.datasource.TodoId
import vc.rux.klinent4todobackend.datasource.TodoModel

class TodosAdapter(
    private val viewModel: ITodosVM,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<TodoModel, TodosAdapter.TodoVH>(TodoDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoVH {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context)).also {
            it.lifecycleOwner = lifecycleOwner
        }
        return TodoVH(binding, viewModel)
    }


    override fun onBindViewHolder(holder: TodoVH, position: Int) =
        holder.bind(getItem(position))

    fun getTodoPosition(todoId: TodoId): Int? {
        return (0 until itemCount).find { getItem(it).id == todoId }
    }

    class TodoVH(
        private val binding: TodoItemBinding,
        private val viewModel: ITodosVM
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoModel) {
            binding.todo = todo
            binding.vm = viewModel
            binding.executePendingBindings()
        }
    }

    private class TodoDiffCallback : DiffUtil.ItemCallback<TodoModel>() {
        override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
            oldItem == newItem
    }
}
