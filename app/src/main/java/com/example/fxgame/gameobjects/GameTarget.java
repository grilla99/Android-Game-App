package com.example.fxgame.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Object that character is trying to reach
 */

public class GameTarget {
    private int x;
    private int y;
    protected final int WIDTH;
    protected final int HEIGHT;
    private Bitmap image;

    /**
     * Constructor for a game target
     * @param image
     * @param x
     * @param y
     */
    public GameTarget(Bitmap image, int x, int y) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();
    }

    /**
     * Draw target to canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.image;
        canvas.drawBitmap(bitmap, x, y, null);
    }

    /**
     * Get x location
     * @return x
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get y location
     * @return y
     */
    public int getY() {
        return this.y;
    }
}
