package com.example.fxgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.locks.ReentrantLock;

public class GameThread extends Thread {
    private volatile boolean canDraw = false;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;
    private Context mContext;

    GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;

    }


    @Override
    public void run() {
        long startTime = System.nanoTime();

        if (interrupted()) {
            return;
        }

        while (canDraw) {
            Canvas canvas = null;

            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas) {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
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

            try {
                //Sleep
                this.sleep(waitTime);
            } catch (InterruptedException e) {
                //put error handling
            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
    }
}