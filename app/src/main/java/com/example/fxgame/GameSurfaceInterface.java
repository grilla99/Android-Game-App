package com.example.fxgame;

//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas
interface GameSurfaceInterface {

    public void initSoundPool();

    public void playSoundExplosion();

    public void playSoundBackground();

    public void update();

}