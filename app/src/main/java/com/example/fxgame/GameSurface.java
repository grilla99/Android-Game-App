package com.example.fxgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.MainCharacter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, GameSurfaceInterface {
    protected GameThread gameThread;
    private final Context mContext;

    //Declare variables to store characters and explosions in the game
    private final List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    private final List<MainCharacter> mainCharacterList = new ArrayList<MainCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();

    //Variables to deal with sounds within the game
    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private static final String TAG = "GameSurface";
    public int points;


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


        //Load the sound background.mp3 into soundPool
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
        canvas.drawColor(-16711681);

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
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Create game characters
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi1);
        MainCharacter mainCharacter = new MainCharacter(this, chibiBitmap1, 100, 50);

        //Initialize the score as zero
        points = 0;

        //Create NPC's
        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);

        //Recursively create characters to add into the arena and position them randomly
        for (int counter = 0; counter < 5; counter++){
            int chibiX = getRandomNumberInRange(150, 1000);
            int chibiY = getRandomNumberInRange(700, 1450);

            ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap2, chibiX, chibiY);
            this.chibiList.add(chibi1);
        }

        //Add characters to relevant list so they can be drawn into the game
        this.mainCharacterList.add(mainCharacter);

        // Create and start the game thread
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();

    }

    public void update() {
        //loop through the character arraylist and update them
        for (ChibiCharacter chibi : chibiList) {
            chibi.update();

            Iterator<MainCharacter> iterator = this.mainCharacterList.iterator();

            while (iterator.hasNext()) {
                MainCharacter mainCharacter = iterator.next();

                //If the main character bumps into a chibi character
                if (mainCharacter.getX() < chibi.getX() && chibi.getX() < mainCharacter.getX() + mainCharacter.getWidth()
                        && mainCharacter.getY() < chibi.getY() && chibi.getY() < mainCharacter.getY() + mainCharacter.getHeight()) {
                    //Remove the main character
                    iterator.remove();

                    //Create explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());

                    // Add created explosion to explosion list
                    this.explosionList.add(explosion);

                }
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setRunning(false);
                //Parent thread must wait until end of game thread
                this.gameThread.join();
            } catch (InterruptedException e) {
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

                if (chibi.getX() < x && x < chibi.getX() + chibi.getWidth()
                        && chibi.getY() < y && y < chibi.getY() + chibi.getHeight()) {
                    //Remove the current element from iterator and list
                    iterator.remove();

                    //Create explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());

                    // Add created explosion to explosion list
                    this.explosionList.add(explosion);

                    // Increase the points of player
                    points +=1;
                    Log.v(TAG, "index=" + points);
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

    //A function to generate a random number within a range
    //Used to generate random chibi position at game start
    private static int getRandomNumberInRange(int min, int max) {
        //Error handling
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min value");
        }

        //Generate random within range
        Random r = new Random();
        return r.nextInt((max-min) + 1) + min;
    }
}