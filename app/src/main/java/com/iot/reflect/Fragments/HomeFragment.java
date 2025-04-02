package com.iot.reflect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iot.reflect.Activities.AdjustMirrorActivity;
import com.iot.reflect.Activities.DisplayTextActivity;
import com.iot.reflect.Activities.MusicActivity;
import com.iot.reflect.Activities.TodoActivity;
import com.iot.reflect.Models.ModelUserServer;
import com.iot.reflect.R;
import com.iot.reflect.SessionManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private View view;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Please wait");
        view.findViewById(R.id.adjust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AdjustMirrorActivity.class));
            }
        });


        view.findViewById(R.id.display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DisplayTextActivity.class));
            }
        });


        view.findViewById(R.id.music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MusicActivity.class));
            }
        });

        view.findViewById(R.id.todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TodoActivity.class));
            }
        });


        view.findViewById(R.id.news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndSaveNews();
            }
        });



        view.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please wait");
                pd.show();
                FirebaseDatabase.getInstance().getReference("display").setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Displaying Image", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Display error", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
            }
        });


        view.findViewById(R.id.affirmation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Getting Affirmations");
                pd.show();
                FirebaseDatabase.getInstance().getReference("display").setValue(3).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Displaying Affirmations", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Display error", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
            }
        });


    }


    private void fetchAndSaveNews() {
        ProgressDialog pdNews = new ProgressDialog(getContext());
        pdNews.setMessage("Fetching news...");
        pdNews.setCancelable(false);
        pdNews.show();

        new Thread(() -> {
            try {
                String apiKey = "d176255b5b594fdab40d3dd88e899ee1";
                String urlStr = "https://newsapi.org/v2/everything?q=ireland&pageSize=5&sortBy=publishedAt&apiKey=" + apiKey;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray articles = obj.getJSONArray("articles");

                    if (articles.length() == 0) {
                        requireActivity().runOnUiThread(() -> {
                            pdNews.dismiss();
                            Toast.makeText(getContext(), "No news found!", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    StringBuilder newsData = new StringBuilder();
                    newsData.append("--------------\nNEWS\n--------------");

                    for (int i = 0; i < articles.length(); i++) {
                        String title = articles.getJSONObject(i).getString("title");

                        // Clean quotes and punctuation
                        title = title.replaceAll("[\"'“”‘’:]", "");
                        title = title.replaceAll("[.,!?]$", "");

                        // Trim to 10 words and capitalize
                        title = trimToWords(title, 15);
//                        title = capitalizeFirst(title);

                        // Add a bullet or emoji and spacing
                        newsData.append("\n").append(i + 1).append("] ").append(title).append("\n\n");
                    }

                    // Remove last "\n-\n" if present
                    if (newsData.toString().endsWith("\n-\n")) {
                        newsData.setLength(newsData.length() - 3); // removes last "\n-\n"
                    }

                    String finalNewsData = newsData.toString();

                    requireActivity().runOnUiThread(() -> FirebaseDatabase.getInstance().getReference("news")
                            .setValue(finalNewsData)
                            .addOnCompleteListener(task -> {
                                pdNews.dismiss();
                                if (task.isSuccessful()) {


                                    FirebaseDatabase.getInstance().getReference("display").setValue(5).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Display error", Toast.LENGTH_SHORT).show();
                                            }
                                            Toast.makeText(getContext(), "News fetched and saved!", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else {
                                    Toast.makeText(getContext(), "Failed to save news", Toast.LENGTH_SHORT).show();
                                }
                            }));

                } else {
                    requireActivity().runOnUiThread(() -> {
                        pdNews.dismiss();
                        Toast.makeText(getContext(), "Server Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    pdNews.dismiss();
                    Toast.makeText(getContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    private String trimToWords(String text, int maxWords) {
        String[] words = text.trim().split("\\s+");
        if (words.length <= maxWords) return text;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            result.append(words[i]).append(" ");
        }
        return result.toString().trim() + "...";
    }

}