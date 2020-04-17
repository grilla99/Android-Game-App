package com.example.fxgame.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.fxgame.surfaces.GameSurface;

/**
 * Used to create explosions in the game
 */
public class Explosion extends GameObject {
    private int rowIndex = 0;
    private int colIndex = -1;

    private boolean finish = false;
    private GameSurface gameSurface;

    /**
     * Constructor to create an explosion
     * @param GameSurface
     * @param image
     * @param x
     * @param y
     */
    public Explosion(GameSurface GameSurface, Bitmap image, int x, int y) {
        super(image, 5, 5, x, y);

        this.gameSurface = GameSurface;
    }

    /**
     * Defines the lifeycle of an explosion
     */
    public void update() {
        this.colIndex++;

        //Play sound explosion noise
        if (this.colIndex == 0 && this.rowIndex == 0) {
            this.gameSurface.playSoundExplosion();
        }

        if (this.colIndex >= this.colCount) {
            this.colIndex = 0;
            this.rowIndex++;

            if (this.rowIndex >= this.rowCount) {
                this.finish = true;
            }
        }
    }

    /**
     * Draws the explosion to canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (!finish) {
            Bitmap bitmap = this.createSubImageAt(rowIndex, colIndex);
            canvas.drawBitmap(bitmap, this.x, this.y, null);
        }
    }

    /**
     * Used to get whether explosion is finished or not
     * @return finish
     */
    public boolean isFinish() {
        return finish;
    }
}
