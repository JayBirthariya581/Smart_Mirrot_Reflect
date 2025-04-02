package com.iot.reflect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iot.reflect.databinding.TodoItemBinding;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {

    private final List<String> todoList;
    private final Context context;

    public TodoAdapter(List<String> todoList, Context context) {
        this.todoList = todoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate using View Binding
        TodoItemBinding binding = TodoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.todo.setText(todoList.get(position)); // Set todo text
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TodoItemBinding binding;

        public MyViewHolder(@NonNull TodoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
