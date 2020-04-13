package com.example.fxgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fxgame.framework.GameButton;
import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.Explosion;
import com.example.fxgame.gameobjects.MainCharacter;

import java.util.ArrayList;
import java.util.List;

public class GameSurfaceThree extends GameSurface implements SurfaceHolder.Callback {
    private final Context mContext;
    private GameThread gameThread;
    private static final String MYPREFERENCES = "MyPrefs";
    private static final String Level = "LevelThree";
    SharedPreferences sharedPreferences;

    //Declare variables to store characters and explosions in the game
    private final List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    private final List<MainCharacter> mainCharacterList = new ArrayList<MainCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();
    private final List<GameButton> gameButtonList = new ArrayList<GameButton>();


    //Variables to deal with sounds within the game
    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private static final String TAG = "GameSurfaceThree";
    private int points;

    //Used to set the scaled font size taking into account pixel density and user preference
    private int scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);
    private SurfaceHolder mHolder;
    private boolean isGameOver = false;
    private GameButton gameOverButton;


    public GameSurfaceThree(Context context) {
        super(context);

        //constructor
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.initSoundPool();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder,format,width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    @Override
    public void initSoundPool() {
        super.initSoundPool();
    }

    @Override
    public void playSoundExplosion() {
        super.playSoundExplosion();
    }

    @Override
    public void playSoundBackground() {
        super.playSoundBackground();

    }

    @Override
    public void update() {
        System.out.println("x");
    }

    public int getHighScoreFromPreferences() {
        sharedPreferences = mContext.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String highScoreString = sharedPreferences.getString("LevelOne", "0");
        return Integer.parseInt(highScoreString);

    }
}
