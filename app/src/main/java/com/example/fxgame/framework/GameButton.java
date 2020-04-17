package com.example.fxgame.framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Class used to create a game button in surface view as it is more efficient to do so and better
 * practice in surface view as you are meant to be drawing to canvas not using android objects
 */

public class GameButton {
    //Define setup conditions for the button
    public Matrix btn_matrix = new Matrix();
    public RectF btn_rect;

    private float width;
    private float height;
    Bitmap bg;

    String id;

    /**
     * Create a game button with params
     * @param width
     * @param height
     * @param bg
     * @param id
     */
    public GameButton(float width, float height, Bitmap bg, String id) {
        this.width = width;
        this.height = height;
        this.bg = bg;

        btn_rect = new RectF(0, 0, width, height);
    }

    /**
     * Sets position on page
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        btn_matrix.setTranslate(x,y);
        btn_matrix.mapRect(btn_rect);
    }

    /**
     * Draw the button on page
     * @param canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bg, btn_matrix, null);
    }

    /**
     *
     * @return width of button
     */
    public float getWidth() {
        return this.width;
    }

    /**
     *
     * @return height of button
     */
    public float getHeight() {
        return this.height;
    }
}
