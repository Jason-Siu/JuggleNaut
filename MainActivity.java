package com.example.something.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final boolean music; // false state will mean that music/sound will play
        final boolean sfx; // because isChecked means that the music will be disabled from SettingsActivity

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            music = extras.getBoolean("MUSIC", false);
            sfx = extras.getBoolean("SFX", false);
            Log.i("MainActivity", "Bundle was passed to variables " + String.valueOf(music) + " " + String.valueOf(sfx));
            edit.putBoolean("MUSIC", music);
            edit.putBoolean("SFX", sfx);
            edit.commit();
        }
        else
        {
            music = sp.getBoolean("MUSIC", false);
            sfx = sp.getBoolean("SFX", false);
            Log.i("MainActivity", "Bundle was not passed to variables "  + String.valueOf(music) + " " + String.valueOf(sfx));
        }

        final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.mainmenusong);

        Button advanceToPlay = (Button) findViewById(R.id.buttonPlay);
        advanceToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras1 = new Bundle();
                extras1.putBoolean("MUSIC", music);
                extras1.putBoolean("SFX", sfx);
                Intent mainIntent = new Intent(MainActivity.this, Game.class);
                mainIntent.putExtras(extras1);
                startActivity(mainIntent);
                if(mp1.isPlaying())
                    mp1.release();

            }
        });

        Button advanceToAbout = (Button) findViewById(R.id.buttonAbout);
        advanceToAbout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(mainIntent);
                if(mp1.isPlaying())
                    mp1.release();

            }
        });

        Button advanceToInstruct = (Button) findViewById(R.id.buttonInstruct);
        advanceToInstruct.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, InstructActivity.class);
                startActivity(mainIntent);
                if(mp1.isPlaying())
                mp1.release();

            }
        });

        Button advanceToSettings = (Button) findViewById(R.id.buttonSettings);
        advanceToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent mainIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(mainIntent);
                if(mp1.isPlaying())
                    mp1.release();
            }
        });

        if(!music)
        {
            mp1.seekTo(0);
            mp1.start();
            mp1.setLooping(true);
        }
    }
}
