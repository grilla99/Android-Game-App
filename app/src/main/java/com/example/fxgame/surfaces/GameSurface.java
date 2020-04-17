package com.example.fxgame.surfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fxgame.R;
import com.example.fxgame.framework.GameButton;
import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.Explosion;
import com.example.fxgame.gameobjects.GameObject;
import com.example.fxgame.gameobjects.MainCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas


public abstract class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    //Game thread to use to draw
    private GameThread gameThread;

    private final Context mContext;
    private SurfaceHolder mHolder;

    //Declare variables to store levels
    GameSurfaceOne levelOne;
    GameSurfaceTwo levelTwo;
    GameSurfaceThree levelThree;

    //Declare variables to store characters and explosions in the game
    protected List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    protected List<MainCharacter> mainCharacterList = new ArrayList<MainCharacter>();
    protected List<Explosion> explosionList = new ArrayList<Explosion>();
    protected List<GameButton> gameButtonList = new ArrayList<GameButton>();

    //Variables to deal with sounds within the game
    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    //Used to set the scaled font size taking into account pixel density and user preference
    private int scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

    /**
     * Constructor for new surface
      * @param context
     */
    public GameSurface(Context context) {
        super(context);
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.initSoundPool();
    }

    /**
     * Creates the sound effects within game
     */
    public void initSoundPool() {
        //with android api >= v21
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                AudioAttributes audioAttrib = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

                this.soundPool = builder.build();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //with android api < 21
        } else {
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        //When SoundPool load complete
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;

                //playing background sound
                playSoundBackground();
            }
        });


        //Load the sound background.mp3 into soundPool1
        this.soundIdBackground = this.soundPool.load(this.getContext(), R.raw.background, 1);

        this.soundIdExplosion = this.soundPool.load(this.getContext(), R.raw.explosion, 1);
    }

    /**
     * Plays sound effects within game
     */
    public void playSoundExplosion() {
        if (this.soundPoolLoaded) {
            float leftVol = 0.8f;
            float rightVol = 0.8f;

            //play explosion sound
            int streamId = this.soundPool.play(this.soundIdExplosion, leftVol, rightVol, 1, 0, 1f);
        }
    }

    /**
     * Used to play backgriound sound
     */
    public void playSoundBackground() {
        if (this.soundPoolLoaded) {
            float leftVol = 0.8f;
            float rightVol = 0.8f;

            //play background sound
            int streamId = this.soundPool.play(this.soundIdExplosion, leftVol, rightVol, 1, 0, 1f);
        }
    }

    /**
     * Used to draw sprite to canvas - to be overrriden
     * @param canvas
     */
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    // Implements method of SurfaceHolder.Callback

    /**
     * Used to create and start a new game thread with the current game surface
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Create and start the game thread
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setCanDraw(true);
        this.gameThread.start();
    }

    //Defines abstract methods to be implemented in child classes

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    };

    @Override
    public  void surfaceDestroyed(SurfaceHolder holder){

    };

    @Override
    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void update();

    //A function to generate a random number within a range
    //Used to generate random chibi position at game start

    /**
     * Return random int in range
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNumberInRange(int min, int max) {
        //Error handling
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min value");
        }

        //Generate random within range
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    //Function to determine whether an object in the game is touching another

    /**
     *
     * @param gameObject
     * @param x
     * @param y
     * @return isTouching
     */
    public boolean isTouching(GameObject gameObject, int x, int y) {
        return gameObject.getX() < x && x < gameObject.getX() + gameObject.getWidth()
                && gameObject.getY() < y && y < gameObject.getY() + gameObject.getHeight();
    }

    /**
     * Shared preferences used for local storage of score
     * @param context
     * @return sharedPreferences instance
     */
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("Scores", Context.MODE_PRIVATE);
    }

    /**
     * Used to draw to canvas, can be overriden
     * @param canvas
     */
    public abstract void doDraw(Canvas canvas);



    public Bitmap getScaledBitmap(Bitmap bitmap) {
        //Take a bitmap image and make it the size of the screen
        Bitmap scaledBackground = Bitmap.createScaledBitmap(bitmap, this.getWidth(),
                this.getHeight(), true);

        return scaledBackground;
    }

    public static int retrieveHighScore(Context context, String level) {
        //Fetch score for level from shared preferences and return it as an integer
        String key = level;
        String score = getPrefs(context).getString(key,"0");
        return Integer.parseInt(score);
    }

    public abstract int getHighScoreFromPreferences();

    public abstract void saveHighScore();

    public abstract void endGame(boolean nextLevel);
}