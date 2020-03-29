package com.example.fxgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private ChibiCharacter chibi1;


    public GameSurface(Context context) {
        super(context);

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);
    }

    //Draw sprite to canvas
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        this.chibi1.draw(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //fetch chibi1 icon from drawable
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.chibi1);
        this.chibi1 = new ChibiCharacter(this, chibiBitmap1,100,50);

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    public void update() {
        this.chibi1.update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setRunning(false);

                //Parent thread must wait until end of game thread
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //code that handles user interacting with the screen
        //character will run toward where touched

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //get location of touch
            int x = (int)event.getX();
            int y = (int)event.getY();

            int movingVectorX = x-this.chibi1.getX();
            int movingVectorY = y-this.chibi1.getY();

            this.chibi1.setMovingVector(movingVectorX, movingVectorY);

            return true;
        }
        return false;
    }
}
