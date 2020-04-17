package com.example.fxgame.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.fxgame.surfaces.GameSurfaceOne;

//Launches a surface view

public class GameActivity extends Activity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sets full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set no title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Loads level one
        this.setContentView(new GameSurfaceOne(this));


    }

}
