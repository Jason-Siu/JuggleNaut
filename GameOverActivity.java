package com.example.something.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final MediaPlayer mp3 = MediaPlayer.create(this, R.raw.gameoversong);

        final boolean music;

        Bundle bundle = getIntent().getExtras();
        TextView textActualScore = (TextView) findViewById(R.id.textScore2);
        String error = "ERROR!";
        int currentScore = bundle.getInt("SCORE", 0);
        music = bundle.getBoolean("MUSIC", false);

        if(!music)
        {
            mp3.start();
            mp3.setLooping(true);
        }

        if(bundle != null)
        {
            textActualScore.setText(String.valueOf(currentScore));
        }
        else
        {
            textActualScore.setText(error);
        }

        TextView textHighScore = (TextView) findViewById (R.id.textHighScore2);

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);
        if(currentScore > highScore)
        {
            textHighScore.setText(String.valueOf(currentScore));
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", currentScore);
            editor.commit();
        }
        else
        {
            textHighScore.setText(String.valueOf(highScore));
        }

        Button activateToMain = (Button) findViewById(R.id.buttonToPlay);
        activateToMain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(GameOverActivity.this, Game.class);
                startActivity(toMain);
                if(!music)
                {
                    mp3.release();
                }
            }
        });

        Button activateToHome = (Button) findViewById(R.id.buttonMenu);
        activateToHome.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent toMain = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(toMain);
                if(!music)
                {
                    mp3.release();
                }
            }
        });
    }


}
