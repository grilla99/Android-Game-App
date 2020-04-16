package com.example.fxgame.surfaces;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private volatile boolean canDraw = false;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;
    private int threadId;
    private static int threadCounter = 0;
    private Context mContext;

    GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
        this.threadId = threadCounter;
        threadCounter++;
    }

    void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }


    @Override
    public void run() {
        long startTime = System.nanoTime();

        while (canDraw) {
            Canvas canvas = null;

            Log.v("" + threadId, "is running");

            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();


                // Synchronized
                synchronized (canvas) {
                    this.gameSurface.update();
                    this.gameSurface.doDraw(canvas);
                }
            } catch (Exception e) {
                // Do nothing.
            } finally {
                if (canvas != null) {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            long now = System.nanoTime();
            //Interval to redraw game
            //Change ns to ms
            long waitTime = (now - startTime) / 1000000;
            if (waitTime < 10) {
                waitTime = 10; //ms
            }
            System.out.println("Wait time: " + waitTime);

            //Send thread to sleep for short period
            // Is useful for battery saving
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            startTime = System.nanoTime();
            System.out.print(".");
        }
    }
}