package com.example.fxgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.fxgame.activities.GameActivityThree;
import com.example.fxgame.activities.GameActivityTwo;
import com.example.fxgame.framework.GameButton;
import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.Explosion;
import com.example.fxgame.gameobjects.GameTarget;
import com.example.fxgame.gameobjects.MainCharacter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameSurfaceTwo extends GameSurface implements SurfaceHolder.Callback, View.OnTouchListener {
    private SurfaceHolder mHolder;
    private final Context mContext;
    private GameThread gameThread;
    private static final String MYPREFERENCES = "MyPrefs";
    private static final String Level = "LevelTwo";
    SharedPreferences sharedPreferences;

    //Declare variables to store characters and explosions in the game
    private final List<ChibiCharacter> chibiList = new ArrayList<ChibiCharacter>();
    private final List<MainCharacter> mainCharacterList = new ArrayList<MainCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();
    private final List<GameButton> gameButtonList = new ArrayList<GameButton>();

    private GameButton gameOverButton;
    private GameTarget chocoTarget;


    //Variables to deal with sounds within the game
    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private static final String TAG = "GameSurfaceTwo";
    private int points;
    private boolean isGameOver = false;

    //Used to set the scaled font size taking into account pixel density and user preference
    private int scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

    public GameSurfaceTwo(Context context) {
        super(context);
        this.mContext = context;
        isGameOver = false;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);
        this.getHolder().addCallback(this);


        //Create game characters
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi1);
        MainCharacter mainCharacter = new MainCharacter(this, chibiBitmap1, 100, 50);

        Bitmap chocobar = BitmapFactory.decodeResource(this.getResources(), R.drawable.chocolate);
        chocoTarget = new GameTarget(chocobar, 800, 1200);

        //Retrieve the high score from shared preferences.
        sharedPreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        points = getHighScoreFromPreferences();

        //Create NPC's
        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);

        //Recursively create characters to add into the arena and position them randomly
        //More Chibi's in level 2
        for (int counter = 0; counter < 10; counter++){
            int chibiX = getRandomNumberInRange(150, 1000);
            int chibiY = getRandomNumberInRange(700, 1450);

            ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap2, chibiX, chibiY);

            //Increase the speed of NPC's in the second level
            chibi1.setChibiSpeed((float)0.1);
            this.chibiList.add(chibi1);
        }

        //Add characters to relevant list so they can be drawn into the game
        this.mainCharacterList.add(mainCharacter);

        this.initSoundPool();
    }

    public void doDraw(Canvas canvas) {
        //Draws the canvas
        super.draw(canvas);

        Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.sand);
        Bitmap scaledBackground = Bitmap.createScaledBitmap(background, this.getWidth(),
                this.getHeight(), true);

        canvas.drawBitmap(scaledBackground, 0 , 0,null);

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

        chocoTarget.draw(canvas);

        //Draws user score in top left of screen
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(scaledSize);
        canvas.drawText("Current score: " + points, 20,50,textPaint);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mHolder = holder;

        // Create and start the game thread
        this.gameThread = new GameThread(this, mHolder);
        this.gameThread.setCanDraw(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
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
                    points +=1;
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
            if (isGameOver) {
                for (GameButton  gameButton : gameButtonList) {
                    if (gameButton.btn_rect.contains(event.getX(), event.getY())) {
                        ((Activity) mContext).finish();

                        //Need to fix that buttons don't work once returned to home screen
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void update() {
        //loop through the character arraylist and update them
        for (ChibiCharacter chibi : chibiList) {

            chibi.update();
            Iterator<MainCharacter> iterator = this.mainCharacterList.iterator();


            while (iterator.hasNext()) {
                MainCharacter mainCharacter = iterator.next();
                int chibiX = chibi.getX();
                int chibiY = chibi.getY();

                //If the main character bumps into a chibi character
                if (isTouching(mainCharacter, chibiX, chibiY)) {
                    //Remove the main character
                    iterator.remove();

                    //set game over bool to true
                    this.isGameOver = true;

                    //Create explosion object
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, chibi.getX(), chibi.getY());
                    // Add created explosion to explosion list
                    this.explosionList.add(explosion);

                    //Create the game over button
                    addGameOverButton(isGameOver);

                    //If score is a high score, insert it into shared preferences
                   if (getHighScoreFromPreferences() < points) {
                       saveHighScore();
                   }
                }
            }
        }

        //Loop through maincharacter (s)
        for (MainCharacter mainCharacter : mainCharacterList) {
            //update main character
            mainCharacter.update();

            //check if main character is touching end goal
            int chocoX = chocoTarget.getX();
            int chocoY = chocoTarget.getY();

            //If main character is touching the chocolate bar
            if (isTouching(mainCharacter, chocoX, chocoY)) {
                //Stop the thread from drawing and interrupt it
                gameThread.setCanDraw(false);
                gameThread.interrupt();

                //If the current score is higher than saved high score, save high score
                if (getHighScoreFromPreferences() < points) {
                    saveHighScore();
                }

                //Load level three
                Intent intent = new Intent(mContext, GameActivityThree.class);
                mContext.startActivity(intent);
            };
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

    void addGameOverButton(boolean isGameOver) {
        if (isGameOver) {
            //If the game is said to be over, create a new GameButton object and add it to the
            // Game button list
            Bitmap gameOverBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_over);
            this.gameOverButton = new GameButton(this.getPivotX() + (gameOverBitmap.getWidth() /2), this.getPivotY() + (gameOverBitmap.getHeight() / 2),
                    gameOverBitmap, "gameover");
            gameButtonList.add(gameOverButton);
        }
    }

    @Override
    public int getHighScoreFromPreferences() {
        //Gets the user high score from shared preferences for LevelTwo
        sharedPreferences = mContext.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String highScoreString = sharedPreferences.getString("LevelOne", "0");
        return Integer.parseInt(highScoreString);

    }

    @Override
    public void saveHighScore() {
        //Save the user points to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Level, Integer.toString(points));
        editor.commit();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.v(TAG, "yes");

        return false;
    }
}
