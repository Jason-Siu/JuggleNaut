package com.example.something.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class InstructActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruct);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button activateToMain = (Button) findViewById(R.id.buttonToMain);
        activateToMain.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Intent toMain = new Intent(InstructActivity.this, MainActivity.class);
                startActivity(toMain);
            }
        });

    }
}
