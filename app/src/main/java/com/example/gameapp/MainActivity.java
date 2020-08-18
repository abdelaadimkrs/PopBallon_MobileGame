package com.example.gameapp;
import android.app.Dialog;
import android.app.AlertDialog;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gameapp.tools.Balloon;
import com.example.gameapp.tools.Colore;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {
    private SoundHelper soundHelper;
    private static final int BALLOON_PER_LEVEL = 3 ;
    public ViewGroup mContentView;
    public ViewGroup  barContxt ;
    public TextView mScoreDisplay,mLevelDisplay;
    public Button mGoButton;
    private int mScore;
    public boolean mPlaying;
    public boolean mStopped=true;
    List<ImageView> nPinsImage = new ArrayList<>();
    List<Balloon> mBalloons = new ArrayList<>();
    private int mBalloonsPopped;


    public int RandColor()
    {
        Random rand = new Random();
        int a = rand.nextInt();
        int r = rand.nextInt();
        int g = rand.nextInt();
        int b = rand.nextInt();

        int color = Color.argb(a,r,g,b);
        return color;
    }
    int mScreenW,mScreenH,mPinsUsed;

    static final int MIN_ANIMATION_DELAY = 500;
    static final int MAX_ANIMATION_DELAY = 1500;
    static final int MIN_ANIMATION_DURATION = 1000;
    static final int MAX_ANIMATION_DURATION = 8000;
    static final int NUMBER_OF_PINS = 5;

    private int mLevel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);
        mContentView= (ViewGroup) findViewById(R.id.activity);
        mGoButton =  findViewById(R.id.go_button);
        OnFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if(viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenH = mContentView.getHeight();
                    mScreenW=mContentView.getWidth();


                }
            });
        }

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFullScreen();
            }
        });
        mLevelDisplay = findViewById(R.id.level_display);
        mScoreDisplay=findViewById(R.id.score_display);
        nPinsImage.add((ImageView) findViewById(R.id.pushpin1));
        nPinsImage.add((ImageView) findViewById(R.id.pushpin2));
        nPinsImage.add((ImageView) findViewById(R.id.pushpin3));
        nPinsImage.add((ImageView) findViewById(R.id.pushpin4));
        nPinsImage.add((ImageView) findViewById(R.id.pushpin5));
        soundHelper = new SoundHelper(this);
        soundHelper.prepareMusicPlayer(this);

    }

    public void OnFullScreen()
    {
        barContxt = findViewById(R.id.activity);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OnFullScreen();
    }

    private void startLevel()
    {
        mLevel++;
        BalloonLauncher lancher = new BalloonLauncher();
        lancher.execute(mLevel);
        mPlaying=true;
        mBalloonsPopped=0;
        mGoButton.setText("stop Game");

    }

    // TODO: 2/26/2020
    private  void finishLevel()
    {
        Toast.makeText(this,String.format("you finishied level %d",mLevel),Toast.LENGTH_SHORT).show();
        mPlaying=false;
        mGoButton.setText(String.format("Start level %d",mLevel+1));
        startLevel();
    }

    public void goButtononClickHandel(View view)
    {
        if(mPlaying)
        {
            gameOver(false);
        }else if (mStopped)
        {
            startGame();
        }
        // TODO: 2/26/2020
       // else{startLevel();}


    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {

        mBalloonsPopped++;
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        soundHelper.playSound();
        if(userTouch)
        {
            mScore++;
        }else
            {
                mPinsUsed++;
                if(mPinsUsed <= nPinsImage.size())
                {
                    nPinsImage.get(mPinsUsed-1).setImageResource(R.drawable.pin_off);
                }
                if(mPinsUsed==NUMBER_OF_PINS)
                {
                    gameOver(true);
                    return;
                }
                else
                {
                    Toast.makeText(this,"Missed that One !",Toast.LENGTH_SHORT).show();
                }
            }
        updateDisplay();
        if(mBalloonsPopped==BALLOON_PER_LEVEL)
        {
            finishLevel();
        }

    }

    /*
    public void newLevel(){
        if(mBalloonsPopped==3){startLevel();}
    }*/

    private  void startGame()
    {
        OnFullScreen();
        mLevel=0;
        mScore=0;
        mPinsUsed=0;
        for (ImageView pin : nPinsImage
             ) {
            pin.setImageResource(R.drawable.pin);
        }
        mStopped=false;
        startLevel();
        soundHelper.PlayMusic();
        // TODO: 2/26/2020

        /*
        if(mScore==mScore+3)
        {
            startLevel();
        }*/
       // newLevel();
    }

    private void gameOver(boolean allPinsUsed) {
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
        for (Balloon balloon : mBalloons
        ) {
            mContentView.removeView(balloon);
            balloon.setPopped(true);
        }
        mBalloons.clear();
        mPlaying = false;
        mGoButton.setText("Start Game");
        mStopped = true;
        if (allPinsUsed)
        {
            HighScoreHelper.isTopScore(this,mScore);
            HighScoreHelper.setTopScore(this,mScore);
            //SimpleAlertDialog dialog = new SimpleAlertDialog.inS()
           // Toast.makeText(this,"Great, new Hieght score",Toast.LENGTH_SHORT);

        }
        soundHelper.PouseMusic();
    }


    private void updateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while ( mPlaying &&balloonsLaunched < BALLOON_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenW - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {
        int clr = RandColor();
        Balloon balloon = new Balloon(this, clr, 150);
        mBalloons.add(balloon);
//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenH + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenH, duration);
       //newLevel();
    }
}
