package com.example.fxgame.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.fxgame.surfaces.GameSurfaceThree;

//Launches a surface view

public class GameActivityThree extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set no title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Load level three view
        this.setContentView(new GameSurfaceThree(this));

    }
}
