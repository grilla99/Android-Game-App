package com.example.fxgame.surfaces;

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
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.example.fxgame.R;
import com.example.fxgame.activities.GameActivityThree;
import com.example.fxgame.activities.GameActivityTwo;
import com.example.fxgame.activities.HighScoreActivity;
import com.example.fxgame.activities.MainActivity;
import com.example.fxgame.framework.GameButton;
import com.example.fxgame.gameobjects.ChibiCharacter;
import com.example.fxgame.gameobjects.Explosion;
import com.example.fxgame.gameobjects.GameTarget;
import com.example.fxgame.gameobjects.MainCharacter;
import com.example.fxgame.surfaces.GameSurface;
import com.example.fxgame.surfaces.GameThread;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameSurfaceThree extends GameSurface implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
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
    private GameTarget candyApple;


    //Variables to deal with sounds within the game
    private static final int MAX_STREAMS = 100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    private static final String TAG = "GameSurfaceThree";
    private int points;
    private boolean isGameOver = false;


    //Used to set the scaled font size taking into account pixel density and user preference
    private int scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

    private GameButton gameOverButton;

    public GameSurfaceThree(Context context) {
        super(context);
        this.mContext = context;

        //Make surface focusable so that it can handle events
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        this.initSoundPool();

        //Create game characters
        Bitmap chibiBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi1);
        MainCharacter mainCharacter = new MainCharacter(this, chibiBitmap1, 100, 50);

        Bitmap candyapple = BitmapFactory.decodeResource(this.getResources(), R.drawable.candyapple);
        candyApple = new GameTarget(candyapple, 800, 1200);

        //Retrieve the high score from shared preferences.
        sharedPreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        points = getPreviousLevelScore();

        //Create NPC's
        Bitmap chibiBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.chibi2);

        //Recursively create characters to add into the arena and position them randomly
        //More Chibi's in level 2
        for (int counter = 0; counter < 6; counter++) {
            int chibiX = getRandomNumberInRange(150, 1000);
            int chibiY = getRandomNumberInRange(700, 1450);

            ChibiCharacter chibi1 = new ChibiCharacter(this, chibiBitmap2, chibiX, chibiY);

            //Increase the speed of NPC's in the second level
            chibi1.setChibiSpeed((float) 0.14);
            this.chibiList.add(chibi1);
        }

        //Add characters to relevant list so they can be drawn into the game
        this.mainCharacterList.add(mainCharacter);

        initSoundPool();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mHolder = holder;

        this.gameThread = new GameThread(this, holder);
        this.gameThread.setCanDraw(true);
        this.gameThread.start();
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
            if (isGameOver) {
                for (GameButton gameButton : gameButtonList) {
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
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

    public void doDraw(Canvas canvas) {
        //Draws the canvas
        super.draw(canvas);

        Bitmap background = BitmapFactory.decodeResource(this.getResources(), R.drawable.snow);
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

        candyApple.draw(canvas);

        //Draws user score in top left of screen
        Paint textPaint = new Paint();
        //Black colour for this level due to white background
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(scaledSize);
        canvas.drawText("Current score: " + points, 20, 50, textPaint);
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
                    endGame(false);
                }
            }
        }

        //Loop through maincharacter (s)
        for (MainCharacter mainCharacter : mainCharacterList) {
            //update main character
            mainCharacter.update();

            //check if main character is touching end goal
            int teddyX = candyApple.getX();
            int teddyY = candyApple.getY();

            //If main character is touching the chocolate bar
            if (isTouching(mainCharacter, teddyX, teddyY)) {
                endGame(true);
            }
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

    public int getHighScoreFromPreferences() {
        //Gets the user high score from shared preferences for LevelOne
        sharedPreferences = mContext.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        //Return 0 as default value if no value stored so that current player score will be used
        String highScoreString = sharedPreferences.getString("LevelThree", "0");
        return Integer.parseInt(highScoreString);
    }

    public int getPreviousLevelScore() {
        //Gets the user high score from shared preferences for LevelOne
        sharedPreferences = mContext.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        //Return 0 as default value if no value stored so that current player score will be used
        String highScoreString = sharedPreferences.getString("LevelTwo", "0");
        return Integer.parseInt(highScoreString);
    }

    @Override
    public void saveHighScore() {
        //Saves the user points to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Level, Integer.toString(points));
        editor.commit();
    }

    void addGameOverButton(boolean isGameOver) {
        if (isGameOver) {
            //If the game is said to be over, create a new GameButton object and add it to the
            // Game button list
            Bitmap gameOverBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.game_over);
            this.gameOverButton = new GameButton(this.getPivotX() + (gameOverBitmap.getWidth() / 2), this.getPivotY() + (gameOverBitmap.getHeight() / 2),
                    gameOverBitmap, "gameover");
            gameButtonList.add(gameOverButton);
        }
    }

    @Override
    public void endGame(boolean nextLevel) {
        if (!nextLevel) {
            //set game over bool to true
            this.isGameOver = true;

            //Create the game over button
            addGameOverButton(isGameOver);

            //If score is a high score, insert it into shared preferences
            if (getHighScoreFromPreferences() < points) {
                saveHighScore();
            }

            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        } else {
            //Stop the thread from drawing and interrupt it
            gameThread.setCanDraw(false);
            gameThread.interrupt();

            //If the current score is higher than saved high score, save high score
            if (getHighScoreFromPreferences() < points) {
                saveHighScore();
            }

            Intent intent = new Intent(mContext, HighScoreActivity.class);

            //Add the score to be used in the high score via a bundle
            Bundle bundle = new Bundle();
            bundle.putInt("Score", points);
            intent.putExtras(bundle);

            mContext.startActivity(intent);

        }
    }
}
