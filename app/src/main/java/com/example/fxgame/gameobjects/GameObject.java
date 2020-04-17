package com.example.fxgame.gameobjects;

import android.graphics.Bitmap;

public class GameObject {

    protected Bitmap image;

    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int width;

    protected final int height;
    protected int x;
    protected int y;

    /**
     * Base class that all objects in the game inherit from
     * @param image
     * @param rowCount
     * @param colCount
     * @param x
     * @param y
     */
    public GameObject(Bitmap image, int rowCount, int colCount, int x, int y) {
        //Defines properties that all children will have
        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.x = x;
        this.y = y;

        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();

        this.width = this.WIDTH / colCount;
        this.height = this.HEIGHT / rowCount;
    }


    //Used to extract movement sequences from bitmap images
    protected Bitmap createSubImageAt(int row, int col) {
        //createBitmap(bitmap, x, y, width, height)
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height, width, height);
        return subImage;
    }

    //Getter methods

    /**
     *
     * @return x
     */
    public int getX() {
        return this.x;
    }

    /**
     *
     * @return y
     */
    public int getY() {
        return this.y;
    }

    /**
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }


}
