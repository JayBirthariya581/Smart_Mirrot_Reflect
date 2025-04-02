package com.iot.reflect.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iot.reflect.R;

public class AdjustMirrorActivity extends AppCompatActivity {

    private DatabaseReference mirrorRef;
    Integer currentPosition = 0;


    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_mirror);

        pd = new ProgressDialog(AdjustMirrorActivity.this);
        pd.setMessage("Loading");

        // Initialize Firebase Database Reference
        mirrorRef = FirebaseDatabase.getInstance().getReference("mirrorPosition");

        pd.show();
        mirrorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot posSnap) {

                if (posSnap.exists()) {

                    currentPosition = posSnap.getValue(Integer.class);


                }

                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.card_up).setOnClickListener(v -> updateMirrorDirection("up"));
        findViewById(R.id.card_right).setOnClickListener(v -> updateMirrorDirection("right"));
        findViewById(R.id.card_left).setOnClickListener(v -> updateMirrorDirection("left"));
        findViewById(R.id.card_down).setOnClickListener(v -> updateMirrorDirection("down"));
    }

    private void updateMirrorDirection(String direction) {
        // Create reference to the direction key inside mirrorPosition
        pd.show();
        if (direction.equals("up")) {
            if (currentPosition >= 30) {
                Toast.makeText(this, "Max angle reached", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            currentPosition += 10;
        } else {
            if (currentPosition <= -20) {
                Toast.makeText(this, "Max angle reached", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            currentPosition -= 10;
        }

        mirrorRef.setValue(currentPosition).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdjustMirrorActivity.this, "Angle changed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdjustMirrorActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }
        });
    }
}
