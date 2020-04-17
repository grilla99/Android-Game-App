package com.example.fxgame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fxgame.R;
import com.example.fxgame.surfaces.GameSurfaceThree;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HighScoreActivity extends AppCompatActivity {
    public Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_score);

        Button submitHighScore = findViewById(R.id.submit_high_scores);
        final EditText userEditText = findViewById(R.id.name_field);


        TextView highscore = (TextView) findViewById(R.id.score_text);
        Bundle bundle = getIntent().getExtras();
        final int points = bundle.getInt("Score");
        highscore.setText("" + points);


        submitHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Access a Cloud Firestore instance from your Activity
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String name = userEditText.getText().toString();

                Map<String,Object> highscore = new HashMap<>();

                highscore.put("Name", name);
                highscore.put("Score", points);

                // Add a new document with a generated ID
                db.collection("highscores")
                        .add(highscore)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent = new Intent(mContext, LoadHighScoreActivity.class);
                                mContext.startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                            }
                        });
            }
        });

    }
}