package com.example.fxgame;

import android.content.Context;
import android.view.SurfaceHolder;

public class GameRenderView extends GameThread {
    public GameRenderView(GameSurface gameSurface, SurfaceHolder surfaceHolder){
        super(gameSurface,surfaceHolder);
    }
}
