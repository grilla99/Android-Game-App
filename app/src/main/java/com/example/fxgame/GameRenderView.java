package com.example.fxgame;

import android.view.SurfaceHolder;

public class GameRenderView extends GameThread {
    public GameRenderView(GameSurface gameSurface, SurfaceHolder surfaceHolder){
        super(gameSurface,surfaceHolder);
    }
}
