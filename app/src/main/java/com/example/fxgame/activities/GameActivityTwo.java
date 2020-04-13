package com.example.fxgame.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.fxgame.GameSurface;
import com.example.fxgame.GameSurfaceTwo;
import com.example.fxgame.R;

//Launches a surface view

public class GameActivityTwo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set no title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(new GameSurfaceTwo(this));

    }
}
