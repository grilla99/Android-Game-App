package com.example.fxgame;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceThree extends SurfaceView implements SurfaceHolder.Callback, GameSurfaceInterface {
    private final Context mContext;

    public GameSurfaceThree(Context context) {
        super(context);

        //constructor
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.initSoundPool();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void initSoundPool() {

    }

    @Override
    public void playSoundExplosion() {

    }

    @Override
    public void playSoundBackground() {

    }

    @Override
    public void update() {

    }
}
