package com.kompatscher.jan.intervaltimer.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.Workout;
import com.kompatscher.jan.intervaltimer.R;

public class SaveWorkoutActivity extends AppCompatActivity {


    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameEditText = findViewById(R.id.nameEditText);
    }

    public void saveWorkout() {
        /*
         * enter username
         * */
        String workoutName = nameEditText.getText().toString();

        final Workout workout = new Workout();        //new preset-entity

        //no save for presets without a username
        if (workoutName.isEmpty()) {
            return;
        }
        workout.setName(workoutName);

        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, Long>() {     //new thread
            @Override
            protected Long doInBackground(Void... voids) { //what happens in the new thread
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        workoutDao().insert(workout); //get database from class which needs ApplicationContext and uses the insert-dunction on the exerciseDao
            }

            @Override
            protected void onPostExecute(Long resultId) { //resultId = parameter from doInBackround
                // you can update the UI-Thread with your results here
                Toast.makeText(getApplicationContext(), "Created workout", Toast.LENGTH_LONG).show(); //shows notification with the eiwId of the entry
            }
        }.execute();
    }

    public void saveWorkoutButtonClick(View view) {
        saveWorkout();
        finish();
    }

}
