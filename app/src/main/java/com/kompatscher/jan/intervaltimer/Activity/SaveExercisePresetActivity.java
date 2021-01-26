package com.kompatscher.jan.intervaltimer.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;

public class SaveExercisePresetActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Time workTime;
    private Time breakTime;
    private long setsToSave;
    private boolean includeLastBreakToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        workTime = new Time();
        breakTime = new Time();
        Long sets = 1l;
        Boolean includeLastBreak = true;
        if(b != null) {
            workTime = Time.fromString(b.getString("workTime"));
            breakTime = Time.fromString(b.getString("breakTime"));
            sets = b.getLong("sets");
            includeLastBreak = b.getBoolean("includeLastBreak");
        }

        setContentView(R.layout.activity_save_preset);

        nameEditText = findViewById(R.id.editText);
        setsToSave = sets;
    }

    public void savePreset() {
        /*
         * enter username
         * */
        String presetName = nameEditText.getText().toString();

        final Exercise exerciseRoom = new Exercise();        //new preset-entity

        //no save for presets without a username
        if(presetName.isEmpty()){
            return;
        }
        exerciseRoom.setName(presetName);

        if(workTime != null)
            exerciseRoom.setTotalWorkSeconds(workTime.getTotalSeconds());
        if(breakTime != null)
            exerciseRoom.setTotalBreakSeconds(breakTime.getTotalSeconds());

        exerciseRoom.setSets(setsToSave);
        exerciseRoom.setIncludeLastBreak(includeLastBreakToSave);

        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, Long>() {     //new thread
            @Override
            protected Long doInBackground(Void... voids) { //what happens in the new thread
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        exerciseDao().insert(exerciseRoom); //get database from class which needs ApplicationContext and uses the insert-dunction on the exerciseDao
            }

            @Override
            protected void onPostExecute(Long resultId) { //resultId = parameter from doInBackround
                // you can update the UI-Thread with your results here
                Toast.makeText(getApplicationContext(), "Created exercise", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public void presetsButtonClick(View view) {
        savePreset();
        finish();
    }




}
