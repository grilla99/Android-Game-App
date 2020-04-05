package com.example.fxgame;

import android.view.SurfaceHolder;

public class GameRenderView extends GameThread {
    public GameRenderView(GameSurfaceInterface gameSurface, SurfaceHolder surfaceHolder){
        super(gameSurface,surfaceHolder);
    }
}
