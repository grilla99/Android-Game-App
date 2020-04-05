package com.example.fxgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;

    }

    void setRunning(boolean isRunning) {
        running = isRunning;

    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        while (running) {
            Canvas canvas = null;

            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas)  {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
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
            System.out.println("Wait time: "+ waitTime);

            try {
                //Sleep
                this.sleep(waitTime);
            } catch (InterruptedException e ){
                //put error handling
            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
    }
}
