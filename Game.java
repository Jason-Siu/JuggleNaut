package com.example.something.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class Game extends AppCompatActivity
{
    private int score = 0;
    private Circle[] objects = new Circle[5];
    private int xPosClick;
    private int yPosClick;
    private double distanceFromRad;

    public static int numBalls = 1;
    public static int count = 0;
    private MediaPlayer mediaplayer;

    SoundPool soundPool;
    SoundPool.Builder soundPoolBuilder;
    AudioAttributes attributes;
    AudioAttributes.Builder attributesBuilder;

    int soundBallClick;
    int soundMisClick1;
    int soundMisClick2;
    int soundWallBounce;

    private boolean music;
    private boolean sfx;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        createSoundPool();
        loadSounds();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            music = bundle.getBoolean("MUSIC", false);
            sfx = bundle.getBoolean("SFX", false);
            edit.putBoolean("MUSIC", music);
            edit.putBoolean("SFX", sfx);
        }
        else
        {
            music = sp.getBoolean("MUSIC", false);
            sfx = sp.getBoolean("SFX", false);
        }

        if(!music)
        {
            mediaplayer = MediaPlayer.create(this, R.raw.ingamemusic);
            mediaplayer.start();
            mediaplayer.setLooping(true);
        }
    }

    public class MyView extends View
    {
        public MyView(Context context)
        {
            super(context);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            for(int x = 0; x < objects.length; x++)
            {
                objects[x] = new Circle((int)(Math.random()* (metrics.widthPixels - 440)) + 220 , 0);
            }
            objects[0].alive = true;
            numBalls = 1;
        }

        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(70);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.valueOf(score), metrics.widthPixels - 20, 100, paint);

            for(int count = 0; count < objects.length; count++)
            {
                int xRange = Math.abs(objects[count].getXPos() - xPosClick);
                int yRange = Math.abs(objects[count].getYPos() - yPosClick);
                distanceFromRad = Math.hypot(xRange, yRange);

                if(distanceFromRad > objects[count].radius)
                {
                    if(xPosClick > -100 && yPosClick < metrics.heightPixels + 150 && count % 10 == 1)
                    {
                        int rand = (int) (Math.random() * 2);
                        if(rand == 0)
                        {
                            if(!sfx)
                            {
                                soundPool.play(soundMisClick1,1,1,0,0,1);
                            }
                        }
                        else
                        {
                            if(!sfx)
                            {
                                soundPool.play(soundMisClick2,1,1,0,0,1);
                            }
                        }
                    }
                    continue;
                }

                else
                {
                    objects[count].setXSpd((objects[count].getXPos() - xPosClick)/2);
                    objects[count].setYSpd(-Math.abs(yPosClick - objects[count].getYPos())/5 - 30);
                    if(!sfx)
                    {
                        soundPool.play(soundBallClick, 1, 1, 2, 0, 1);
                    }
                    score += 100;
                }
            }

            // checks if alive, then draws next frame
            for(int x = 0; x < 5; x++)
            {
                if(objects[x].alive)
                {
                    paint.setColor(Color.parseColor(objects[x].hexColor));
                    canvas.drawCircle(objects[x].getXPos(),objects[x].getYPos(),objects[x].radius, paint);
                    objects[x].advanceNextFrame();
                    if(objects[x].getXPos() <= 200 || objects[x].getXPos() >= metrics.widthPixels - 200)
                    {
                        if(objects[x].getXPos() <= 200)
                        {
                            objects[x].setXPos(200);
                        }
                        else
                        {
                            objects[x].setXPos(metrics.widthPixels - 200);
                        }
                        objects[x].setXSpd(-objects[x].getXSpd());
                        if(!sfx)
                        {
                            soundPool.play(soundWallBounce, 1, 1, 3, 0, 1);
                        }
                    }
                    // if touches bottom, then the bal will become inactive
                    if(metrics.heightPixels + 100 < objects[x].getYPos())
                    {
                        objects[x].reset(metrics.widthPixels); // this parameter should always be widthpixels
                        numBalls--;
                    }
                }
            }
            count++;
            if(count % 600 == 0) // this num is frequency of when the next ball is going to fall
            {
                for(int x = 0; x < objects.length; x++)
                {
                    if(!objects[x].alive)
                    {
                        objects[x].alive = true;
                        numBalls++;
                        break;
                    }
                }
                count = 0;
            }

            if(numBalls == 0)
            {
                Bundle extras = new Bundle();
                Intent toMain = new Intent(Game.this, GameOverActivity.class);
                extras.putInt("SCORE", score);
                extras.putBoolean("MUSIC", music);
                toMain.putExtras(extras);
                startActivity(toMain);
                if(!music)
                {
                    mediaplayer.release();
                }
            }
            else
            {
                invalidate();
                // clicks are put into position where a ball cannot possibly touch this position
                xPosClick = -100;
                yPosClick = metrics.heightPixels + 150;
                score++;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent me)
    {
        int x = (int) me.getX();
        int y = (int) me.getY();
        xPosClick = x;
        yPosClick = y;
        return true;
    }

    protected void onPause() {
        super.onPause();
        soundPool.release();
    }

    protected void onResume()
    {
        super.onResume();
        createSoundPool();
        loadSounds();
    }

    protected void loadSounds()
    {
        soundBallClick = soundPool.load(this, R.raw.userclick, 1);
        soundMisClick1 = soundPool.load(this, R.raw.misclick1, 1);
        soundMisClick2 = soundPool.load(this, R.raw.misclick2, 1);
        soundWallBounce = soundPool.load(this, R.raw.wallbounce, 1);
    }

    protected void createSoundPool()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setUsage(AudioAttributes.USAGE_GAME);
            attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attributes = attributesBuilder.build();

            soundPoolBuilder = new SoundPool.Builder();
            soundPoolBuilder.setAudioAttributes(attributes);
            soundPool = soundPoolBuilder.build();
        }
        else
        {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
    }
}

class Circle extends AppCompatActivity
{
    public boolean alive = false;
    public int radius;
    public String hexColor;

    private int xPos;
    private int yPos;
    private int xSpd;
    private int ySpd;
    private final int GRAVITY = 2;



    public Circle(int x, int y)
    {
        // parameters of constructor are the x,y locations of the initial position of the ball
        alive = false;
        xPos = x;
        yPos = y;
        radius = 200;
        ySpd = 0;
        xSpd = 0;
        hexColor = generateRandomColor();

    }

    public void setXPos(int a)
    {
        xPos = a;
    }


    public int getXSpd()
    {
        return xSpd;
    }

    public void setXSpd(int a)
    {
        xSpd = a;
    }

    public void setYSpd(int a)
    {
        ySpd = a;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }
    public void reset(int width)
    {
        xPos = (int)(Math.random() * (width - 440)) + 220;
        yPos = 0;
        alive = false;
        ySpd = 0;
        xSpd = 0;
        hexColor = generateRandomColor();
    }
    public void advanceNextFrame()
    {
        this.ySpd += GRAVITY;
        yPos += ySpd;
        xPos += xSpd;
    }

    private String generateRandomColor()
    {
        Integer r;
        Integer g;
        Integer b;
        String otherString = "";
        r = (int) (Math.random() * 255);
        g = (int) (Math.random() * 255);
        b = (int) (Math.random() * 255);

        if((r + g + b < 300) && r < 85 && g < 85 && b < 85)
        {
            Integer difference = 127;
            r += difference;
            g += difference;
            b += difference;
        }

        String[] list = {Integer.toString(r,16),Integer.toString(g,16),Integer.toString(b,16) };
        for(int a = 0; a < list.length; a++)
        {
            if(list[a].length() == 1)
            {
                list[a] = "0" + list[a];
            }
            otherString += list[a];
        }
        return "#" + otherString;
    }
}

