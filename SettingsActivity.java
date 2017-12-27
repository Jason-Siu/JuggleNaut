package com.example.something.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity{

    SoundPool soundPool;
    SoundPool.Builder soundPoolBuilder;
    AudioAttributes attributes;
    AudioAttributes.Builder attributesBuilder;

    int soundBallClick;
    int soundMisClick1;
    int soundMisClick2;
    int soundWallBounce;

    boolean currentMusic;
    boolean currentSFX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        createSoundPool();
        loadSounds();

        final CheckBox checkBoxMusic = (CheckBox) findViewById(R.id.checkBoxMusic);
        final CheckBox checkBoxSFX = (CheckBox) findViewById(R.id.checkBoxSFX);
        boolean checkbox1 = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("CHECKBOX1", false);
        boolean checkbox2 = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("CHECKBOX2", false);
        checkBoxMusic.setChecked(checkbox1);
        checkBoxSFX.setChecked(checkbox2);

        final MediaPlayer[] media = new MediaPlayer[3];
        media[0] = MediaPlayer.create(this, R.raw.mainmenusong);
        media[1] = MediaPlayer.create(this, R.raw.ingamemusic);
        media[2] = MediaPlayer.create(this, R.raw.gameoversong);

        Button advanceToMain = (Button) findViewById(R.id.buttonToMain);
        advanceToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);

                for (int x = 0; x < media.length; x++)
                {
                    media[x].stop();
                    media[x].prepareAsync();
                }
                if(checkBoxMusic.isChecked())
                {
                    savePrefs("CHECKBOX1", true);
                    currentMusic = true;
                }
                else
                {
                    savePrefs("CHECKBOX1", false);
                    currentMusic = false;
                }
                if(checkBoxSFX.isChecked())
                {
                    savePrefs("CHECKBOX2", true);
                    currentSFX = true;
                }
                else
                {
                    savePrefs("CHECKBOX2", false);
                    currentSFX = false;
                }

                Bundle extras = new Bundle();
                extras.putBoolean("MUSIC", currentMusic);
                extras.putBoolean("SFX", currentSFX);
                Log.i("MainActivity", "Bundle from settings " + String.valueOf(currentMusic) + " " + String.valueOf(currentSFX));
                mainIntent.putExtras(extras);
                startActivity(mainIntent);
            }
        });

        Button playMenuMusic = (Button) findViewById(R.id.buttonMenuMusic);

        playMenuMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int x = 1; x < media.length; x++) {
                    if (media[x].isPlaying()) {
                        media[x].stop();
                        media[x].prepareAsync();
                    }
                }
                media[0].seekTo(0);
                media[0].start();
            }
        });

        Button playGameMusic = (Button) findViewById(R.id.buttonGameMusic);

        playGameMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int x = 0; x < media.length; x++) {
                    if (x == 1) {
                        continue;
                    } else if (media[x].isPlaying()) {
                        media[x].stop();
                        media[x].prepareAsync();
                    }
                }
                media[1].seekTo(0);
                media[1].start();
            }
        });

        Button playOverMusic = (Button) findViewById(R.id.buttonOverMusic);

        playOverMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int x = 0; x < media.length; x++) {
                    if (x == 2) {
                        continue;
                    } else if (media[x].isPlaying()) {
                        media[x].stop();
                        media[x].prepareAsync();
                    }
                }
                media[2].seekTo(0);
                media[2].start();
            }
        });
        Button playWallBounce = (Button) findViewById(R.id.buttonWallSFX);
        playWallBounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                soundPool.play(soundWallBounce, 1, 1, 0, 0, 1);
            }
        });
        Button playClickBall = (Button) findViewById(R.id.buttonBallSFX);
        playClickBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundBallClick, 1, 1, 0, 0, 1);
            }
        });
        Button playMisClick1 = (Button) findViewById(R.id.buttonMisclick1);
        playMisClick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundMisClick1, 1, 1, 0, 0, 1);
            }
        });
        Button playMisClick2 = (Button) findViewById(R.id.buttonMisclick2);
        playMisClick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundMisClick2, 1, 1, 0, 0, 1);
            }
        });
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

    private void savePrefs(String key, boolean value)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            createSoundPool();
            loadSounds();

            final CheckBox checkBoxMusic = (CheckBox) findViewById(R.id.checkBoxMusic);
            final CheckBox checkBoxSFX = (CheckBox) findViewById(R.id.checkBoxSFX);

            final MediaPlayer[] media = new MediaPlayer[3];
            media[0] = MediaPlayer.create(this, R.raw.mainmenusong);
            media[1] = MediaPlayer.create(this, R.raw.ingamemusic);
            media[2] = MediaPlayer.create(this, R.raw.gameoversong);

            Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);

            for (int x = 0; x < media.length; x++)
            {
                media[x].stop();
                media[x].prepareAsync();
            }
            if(checkBoxMusic.isChecked())
            {
                savePrefs("CHECKBOX1", true);
                currentMusic = true;
            }
            else
            {
                savePrefs("CHECKBOX1", false);
                currentMusic = false;
            }
            if(checkBoxSFX.isChecked())
            {
                savePrefs("CHECKBOX2", true);
                currentSFX = true;
            }
            else
            {
                savePrefs("CHECKBOX2", false);
                currentSFX = false;
            }

            Bundle extras = new Bundle();
            extras.putBoolean("MUSIC", currentMusic);
            extras.putBoolean("SFX", currentSFX);
            Log.i("MainActivity", "Bundle from settings " + String.valueOf(currentMusic) + " " + String.valueOf(currentSFX));
            mainIntent.putExtras(extras);
            startActivity(mainIntent);

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
