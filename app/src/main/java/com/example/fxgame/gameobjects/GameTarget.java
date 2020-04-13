package com.example.fxgame.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class GameTarget{
    private int x;
    private int y;
    protected final int WIDTH;
    protected final int HEIGHT;
    private Bitmap image;


    public GameTarget(Bitmap image, int x, int y) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();
    }
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.image;
        canvas.drawBitmap(bitmap, x,y,null);
    }

    public int getX() {
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}
