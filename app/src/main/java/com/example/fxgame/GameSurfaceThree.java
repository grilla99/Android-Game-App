package com.example.fxgame;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceThree extends GameSurface implements SurfaceHolder.Callback {
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
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder,format,width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    @Override
    public void initSoundPool() {
        super.initSoundPool();
    }

    @Override
    public void playSoundExplosion() {
        super.playSoundExplosion();
    }

    @Override
    public void playSoundBackground() {
        super.playSoundBackground();

    }

    @Override
    public void update() {
        System.out.println("x");
    }
}
