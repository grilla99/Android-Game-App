package com.example.fxgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Simulates entire surface of game. Extends surface view (which contains a canvas object)
//Objects in game are drawn onto the canvas
public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    protected GameThread gameThread;
    private Context mContext;
    private final List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();

    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;


    public GameSurface(Context context) {
        super(context);
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.initSoundPool();
    }

    void initSoundPool() {
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
        super.draw(canvas);
        canvas.drawColor(-16711681);

        for (ChibiCharacter chibi : chibiList) {
            chibi.draw(canvas);
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
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi1);
        ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap1, 100, 50);

        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);
        ChibiCharacter chibi2 = new ChibiCharacter(this, chibiBitmap2, 300, 150);

        this.chibiList.add(chibi1);
        this.chibiList.add(chibi2);

        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();

    }

    public void update() {
        //loop through the character arraylist and update them
        for (ChibiCharacter chibi : chibiList) {
            chibi.update();
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

            //
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

                    this.explosionList.add(explosion);
                }
            }

            for (ChibiCharacter chibi : chibiList) {
                int movingVectorX = x - chibi.getX();
                int movingVectorY = y - chibi.getY();
                chibi.setMovingVector(movingVectorX, movingVectorY);
            }
            return true;
        }
        return false;
    }
}