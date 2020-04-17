package com.example.fxgame.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fxgame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoadHighScoreActivity extends AppCompatActivity {

    public Context mContext = this;
    public int counter = 0 ;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores);

        //Get instance of high score db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference highScoreReference = db.collection("highscores");
        // Return fields ordered by score, limited to 3
        highScoreReference.orderBy("Score", Query.Direction.ASCENDING).limit(3)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Assign result to variable as they are inside an object in document
                                String name = document.getString("Name");
                                String score = document.getString("Score");

                                //Populate fields in the view
                                populateTextView(counter, name, score);
                                counter++;
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Used to populate text in the view with high scores
     * @param counter
     * @param name
     * @param score
     */
    public void populateTextView(int counter, String name, String score){
        if (counter == 0) {
            TextView scoreOne = (TextView) findViewById(R.id.rankOne);
            scoreOne.setText(name + "..." + score);
        } else if (counter == 1) {
            TextView scoreTWo = (TextView) findViewById(R.id.rankTwo);
            scoreTWo.setText(name + "..." + score);
        } else {
            TextView scoreThree = (TextView) findViewById(R.id.rankThree);
            scoreThree.setText(name + "..." + score);
        }
    }
}