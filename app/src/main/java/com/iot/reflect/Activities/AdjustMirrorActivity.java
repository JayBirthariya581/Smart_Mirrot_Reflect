package com.iot.reflect.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iot.reflect.R;

public class AdjustMirrorActivity extends AppCompatActivity {

    private DatabaseReference mirrorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_mirror);

        // Initialize Firebase Database Reference
        mirrorRef = FirebaseDatabase.getInstance().getReference("mirrorPosition");

        findViewById(R.id.card_up).setOnClickListener(v -> updateMirrorDirection("up"));
        findViewById(R.id.card_right).setOnClickListener(v -> updateMirrorDirection("right"));
        findViewById(R.id.card_left).setOnClickListener(v -> updateMirrorDirection("left"));
        findViewById(R.id.card_down).setOnClickListener(v -> updateMirrorDirection("down"));
    }

    private void updateMirrorDirection(String direction) {
        // Create reference to the direction key inside mirrorPosition
        DatabaseReference dirRef = mirrorRef.child(direction);

        // Increment the value in Firebase
        dirRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Get current value and increment by 1

                int currentValue = task.getResult().getValue(Integer.class);
                dirRef.setValue(currentValue + 1);
            } else {
                // If value does not exist, set it to 1
                dirRef.setValue(1);
            }
        });

        // Show feedback
        Toast.makeText(this, "Mirror moved " + direction, Toast.LENGTH_SHORT).show();
    }
}
