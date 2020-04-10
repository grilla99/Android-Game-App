package com.example.fxgame;

import android.app.Activity;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fxgame.framework.GameButton;
import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.Explosion;
import com.example.fxgame.gameobjects.GameObject;
import com.example.fxgame.gameobjects.MainCharacter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;

    private final Context mContext;

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

    private static int points;

    //Used to set the scaled font size taking into account pixel density and user preference
    private int scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

    private boolean stop = false;
    private SurfaceHolder mHolder;

    public GameSurface(Context context) {
        super(context);
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.initSoundPool();
    }

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

    public void playSoundExplosion() {
        if (this.soundPoolLoaded) {
            float leftVol = 0.8f;
            float rightVol = 0.8f;

            //play explosion sound
            int streamId = this.soundPool.play(this.soundIdExplosion, leftVol, rightVol, 1, 0, 1f);
        }
    }

    public void playSoundBackground() {
        if (this.soundPoolLoaded) {
            float leftVol = 0.8f;
            float rightVol = 0.8f;

            //play background sound
            int streamId = this.soundPool.play(this.soundIdExplosion, leftVol, rightVol, 1, 0, 1f);
        }
    }

    //Draw sprite to canvas
    @Override
    public void draw(Canvas canvas) {
        //Draws the canvas
        super.draw(canvas);

        //Set background colour for the canvas

        Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
        Bitmap scaledBackground = Bitmap.createScaledBitmap(background, this.getWidth(),
                this.getHeight(), true);

        canvas.drawBitmap(scaledBackground, 0, 0, null);


        //Draw characters and explosions into the arena
        for (ChibiCharacter chibi : chibiList) {
            chibi.draw(canvas);
        }

        for (MainCharacter mainCharacter : mainCharacterList) {
            mainCharacter.draw(canvas);
        }

        for (Explosion explosion : this.explosionList) {
            explosion.draw(canvas);
        }

        for (GameButton gameButton : this.gameButtonList) {
            gameButton.setPosition(canvas.getWidth() / 2 - gameButton.getWidth() / 2,
                    canvas.getHeight() / 2 - gameButton.getHeight() / 2);
            gameButton.draw(canvas);
        }

        //Draws user score in top left of screen
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(scaledSize);
        canvas.drawText("Current score: " + points, 20, 50, textPaint);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        levelOne = new GameSurfaceOne(mContext);
        levelTwo = new GameSurfaceTwo(mContext);
        levelThree = new GameSurfaceThree(mContext);


        //Create game characters
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi1);
        MainCharacter mainCharacter = new MainCharacter(this, chibiBitmap1, 100, 50);

        //Initialize the score as zero
        points = 0;

        //Create NPC's
        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);

        //Recursively create characters to add into the arena and position them randomly
        for (int counter = 0; counter < 5; counter++) {
            int chibiX = getRandomNumberInRange(150, 1000);
            int chibiY = getRandomNumberInRange(700, 1450);

            ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap2, chibiX, chibiY);
            this.chibiList.add(chibi1);
        }

        //Add characters to relevant list so they can be drawn into the game
        this.mainCharacterList.add(mainCharacter);

        // Create and start the game thread
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setCanDraw(true);
        this.gameThread.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setCanDraw(false);
            } catch (IllegalStateException e ){
                e.printStackTrace();
            }
            retry = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //code that handles user interacting with the screen
        //character will run toward where touched

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //get location of touch
            int x = (int) event.getX();
            int y = (int) event.getY();

            //Create new iterator to loop through Chibi characters
            Iterator<ChibiCharacter> iterator = this.chibiList.iterator();

            while (iterator.hasNext()) {
                ChibiCharacter chibi = iterator.next();

                if (isTouching(chibi, x, y)) {
                    //Remove the current element from iterator and list
                    iterator.remove();

                    //Create explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());

                    // Add created explosion to explosion list
                    this.explosionList.add(explosion);

                    // Increase the points of player
                    points += 1;

                }
            }

            for (MainCharacter mainCharacter : mainCharacterList) {
                int movingVectorX = x - mainCharacter.getX();
                int movingVectorY = y - mainCharacter.getY();
                mainCharacter.setMovingVector(movingVectorX, movingVectorY);

                // If the chibi is further down the screen than the main character,
                // Continue heading towards the top of the screen, else follow the chibi
                for (ChibiCharacter chibi : chibiList) {
                    if (chibi.getY() < mainCharacter.getY()) {
                        int chibiMovingVectorX = x - chibi.getX();
                        int chibiMovingVectorY = y - chibi.getY();
                        chibi.setMovingVector(chibiMovingVectorX, chibiMovingVectorY);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void update() {
        //loop through the character arraylist and update them
        for (ChibiCharacter chibi : chibiList) {

            chibi.update();
            Iterator<MainCharacter> iterator = this.mainCharacterList.iterator();

            while (iterator.hasNext()) {
                MainCharacter mainCharacter = iterator.next();
                int chibiX = chibi.getX();
                int chibiY = chibi.getY();
                int mainCharX = mainCharacter.getX();
                int mainCharY = mainCharacter.getY();

                //If the main character bumps into a chibi character
                if (isTouching(mainCharacter, chibiX, chibiY)) {
                    //Remove the main character
                    iterator.remove();

                    //Create explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());

                    // Add created explosion to explosion list
                    this.explosionList.add(explosion);

                    if (isHighScore(mContext, points)) {
//                        insertLevelOneScore(mContext, Integer.toString(points));
                    }

                    //Create the game over button and end game
                    Bitmap gameOverBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_over);
                    GameButton gameOverButton = new GameButton(50, 50, gameOverBitmap, "gameover");

                    this.gameButtonList.add(gameOverButton);
                    ((Activity)mContext).finish();


                }
//                    } else if (isTouching(endGoal, mainCharX, mainCharY)) {
//                        if (isHighScore(mContext, points)) {
//                            insertLevelOneScore(mContext, Integer.toString(points));
//                        }
//
//                        // progress to the next level
//                    }
            }
        }

        for (MainCharacter mainCharacter : mainCharacterList) {
            mainCharacter.update();
        }

        for (Explosion explosion : this.explosionList) {
            explosion.update();
        }

        Iterator<Explosion> iterator = this.explosionList.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();

            if (explosion.isFinish()) {
                //If explosion is finished, remove current element from iterator and the list
                iterator.remove();

                continue;
            }
        }
    }

    //A function to generate a random number within a range
    //Used to generate random chibi position at game start
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
    public boolean isTouching(GameObject gameObject, int x, int y) {
        if (gameObject.getX() < x && x < gameObject.getX() + gameObject.getWidth()
                && gameObject.getY() < y && y < gameObject.getY() + gameObject.getHeight()) {
            return true;
        }
        return false;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("Scores", Context.MODE_PRIVATE);
    }

    public boolean isHighScore(Context context, int points) {
        String score = getPrefs(context).getString("LevelOne", "no_data_found");
        int storedScore = Integer.parseInt(score);

        if (points > storedScore) {
            return true;
        }
        return false;
    }

    public static String retrieveHighScore(Context context) {
        String key = "LevelOne";
        return getPrefs(context).getString(key, "no_data_found");

    }

}