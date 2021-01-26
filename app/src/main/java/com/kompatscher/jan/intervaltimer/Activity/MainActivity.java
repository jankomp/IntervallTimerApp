package com.kompatscher.jan.intervaltimer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kompatscher.jan.intervaltimer.R;

public class MainActivity extends AppCompatActivity {

    public void onWorkoutsButtonClick(View view){
        Intent timerIntent = new Intent (this, WorkoutsListActivity.class);
        startActivityForResult(timerIntent, 1);
    }

    public void onQuickExerciseButtonClick(View view){
        Intent timerIntent = new Intent (this, CreateExerciseActivity.class);
        startActivityForResult(timerIntent, 1);
    }

    public void onSettingsButtonClick(View view){
        //TODO: create Settings Activity and route to that
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
