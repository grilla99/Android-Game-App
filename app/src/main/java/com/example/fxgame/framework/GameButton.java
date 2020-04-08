package com.example.fxgame.framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class GameButton {
    public Matrix btn_matrix = new Matrix();
    public RectF btn_rect;

    private float width;
    private float height;
    Bitmap bg;

    String id;

    public GameButton(float width, float height, Bitmap bg, String id) {
        this.width = width;
        this.height = height;
        this.bg = bg;

        btn_rect = new RectF(0, 0, width, height);
    }

    public void setPosition(float x, float y) {
        btn_matrix.setTranslate(x,y);
        btn_matrix.mapRect(btn_rect);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bg, btn_matrix, null);
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}
