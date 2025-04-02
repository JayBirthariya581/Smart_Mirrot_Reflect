package com.iot.reflect.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iot.reflect.R;
import com.iot.reflect.databinding.ActivityMusicBinding;

public class MusicActivity extends AppCompatActivity {

    ActivityMusicBinding binding;
    ProgressDialog pd;


    DatabaseReference audioRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pd = new ProgressDialog(MusicActivity.this);
        pd.setMessage("Loading");


        audioRef = FirebaseDatabase.getInstance().getReference("audio");


        binding.m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(0);
            }
        });

        binding.m2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(1);
            }
        });

        binding.m3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(2);
            }
        });

        binding.m4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(3);
            }
        });


        binding.m5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(4);
            }
        });


        binding.m6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMelody(5);
            }
        });

        binding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseMelody();
            }
        });



    }

    private void playMelody(int i) {
        pd.setMessage("Playing Melody");
        pd.show();


        audioRef.setValue(i).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MusicActivity.this, "Playing Melody "+String.valueOf(i+1), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }
        });



    }

    private void pauseMelody() {
        pd.setMessage("Pausing Melody");
        pd.show();


        audioRef.setValue(-1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MusicActivity.this, "Melody Paused", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }
        });



    }


}