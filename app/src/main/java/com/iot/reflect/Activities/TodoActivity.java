package com.iot.reflect.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iot.reflect.Adapters.TodoAdapter;
import com.iot.reflect.R;
import com.iot.reflect.databinding.ActivityTodoBinding;
import com.iot.reflect.databinding.TodoCardBinding;

import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {


    TodoAdapter todoAdapter;

    List<String> todoList;
    ActivityTodoBinding binding;

    String start = "--------------\nTODO\n--------------";
    ProgressDialog pd;

    DatabaseReference todoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        todoList = new ArrayList<String>();

        pd = new ProgressDialog(TodoActivity.this);
        pd.setMessage("Loading");

        todoAdapter = new TodoAdapter(todoList, TodoActivity.this);

        binding.rv.setAdapter(todoAdapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(TodoActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rv.setHasFixedSize(true);


        todoRef = FirebaseDatabase.getInstance().getReference("todo");


        TodoCardBinding tcb = TodoCardBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(TodoActivity.this);
        dialog.setContentView(tcb.getRoot());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);


        tcb.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newTodo = tcb.value.getText().toString().trim();

                if (newTodo.isEmpty()) {
                    Toast.makeText(TodoActivity.this, "Invalid text", Toast.LENGTH_SHORT).show();
                    return;
                }

                pd.setMessage("Adding...");
                pd.show();

                todoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String existingTodos = snapshot.getValue(String.class);

                        if (existingTodos == null || existingTodos.isEmpty()) {
                            existingTodos = start;  // if empty, add the header
                        }

                        // Split existing todos to count current todo items
                        String[] lines = existingTodos.split("\n");

                        // Count current todos (excluding the first three header lines)
                        int todoCount = lines.length - 3;

                        // Add numbering to the new todo
                        String numberedTodo = (todoCount + 1) + "] " + newTodo;

                        // Append the new numbered todo
                        existingTodos = existingTodos + "\n\n" + numberedTodo;

                        todoRef.setValue(existingTodos).addOnCompleteListener(task -> {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(TodoActivity.this, "Todo Added!", Toast.LENGTH_SHORT).show();
                                tcb.value.setText("");
                                dialog.dismiss();
                                makeList();  // Refresh list
                            } else {
                                Toast.makeText(TodoActivity.this, "Failed to add todo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pd.dismiss();
                        Toast.makeText(TodoActivity.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        pd.show();
        FirebaseDatabase.getInstance().getReference("display").setValue(4).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeList();
                } else {
                    Toast.makeText(TodoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }

    private void makeList() {
        pd.show();
        todoList.clear();

        todoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot todoSnap) {
                pd.dismiss();
                if (todoSnap.exists()) {
                    String td = todoSnap.getValue(String.class);

                    if (td == null || td.isEmpty()) {
                        Toast.makeText(TodoActivity.this, "No todo found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Split the string by newline character
                    String[] todos = td.split("\n");

                    // Check if there's enough data to skip header
                    if (todos.length <= 3) {
                        Toast.makeText(TodoActivity.this, "No todos available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Start adding todos from index 3 onwards
                    for (int i = 3; i < todos.length; i++) {
                        String item = todos[i].trim();
                        if (!item.isEmpty()) {
                            todoList.add(item);
                        }
                    }

                    // Notify the adapter after data change
                    todoAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(TodoActivity.this, "No todo found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(TodoActivity.this, "Failed to load todos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}