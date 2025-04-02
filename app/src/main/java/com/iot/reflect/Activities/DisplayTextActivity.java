package com.iot.reflect.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.iot.reflect.R;

public class DisplayTextActivity extends AppCompatActivity {

    private EditText etDisplayText; // Reference to the input field
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_text);


        pd = new ProgressDialog(DisplayTextActivity.this);
        pd.setMessage("Adding...");
        pd.show();
        // Get reference to the EditText field
        etDisplayText = findViewById(R.id.et_display_text);

        findViewById(R.id.btn_send_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextToFirebase();
            }
        });


        pd.show();
        FirebaseDatabase.getInstance().getReference("display").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                } else {
                    pd.dismiss();
                    Toast.makeText(DisplayTextActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void sendTextToFirebase() {
        // Get text from input field
        String message = etDisplayText.getText().toString().trim();

        if (!message.isEmpty()) {
            // Send the text to Firebase under "DisplayText/message"
            FirebaseDatabase.getInstance()
                    .getReference("DisplayText")
                    .child("message")
                    .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(DisplayTextActivity.this, "Text sent to Smart Mirror!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DisplayTextActivity.this,"Some error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Show a confirmation message

        } else {
            // Show error if input is empty
            Toast.makeText(this, "Please enter some text!", Toast.LENGTH_SHORT).show();
        }
    }
}
