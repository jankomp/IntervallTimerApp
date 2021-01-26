package com.kompatscher.jan.intervaltimer.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;
import com.kompatscher.jan.intervaltimer.Timer.TimerActivity;

import java.util.ArrayList;
import java.util.List;

public class ExercisePresetsListActivity extends AppCompatActivity {

    private ListView presetListView;
    private View progressView;
    private ArrayList<Exercise> presetsList;
    private PresetAdapter presetAdapter;

    private boolean fromWorkoutEditNotHomeScreen = false;
    private Long workoutId;

    private static final int DELETE_ID = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        presetListView = findViewById(R.id.presetListView);
        progressView = findViewById(R.id.progressView);

        presetsList = new ArrayList<>();
        presetAdapter = new PresetAdapter(this, presetsList);
        presetListView.setAdapter(presetAdapter);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            workoutId = b.getLong("workoutId");
            fromWorkoutEditNotHomeScreen = workoutId != null;
        }

        fillData();
        registerForContextMenu(presetListView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fillData();
    }

    private void showProgress(boolean progress) {
        presetListView.setVisibility(progress ? View.GONE : View.VISIBLE);
        progressView.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void fillData() {
        showProgress(true);

        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, List<Exercise>>() {
            @Override
            protected List<Exercise> doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        exerciseDao().findAllPresetsAsc();
            }

            @Override
            protected void onPostExecute(List<Exercise> exercises) {
                // you can update the UI-Thread with your results here
                presetsList.clear();
                presetsList.addAll(exercises);
                showProgress(false);
            }
        }.execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    // Source: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
    class PresetAdapter extends ArrayAdapter<Exercise> {
        public PresetAdapter(Context context, List<Exercise> exercises) {
            super(context, 0, exercises);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Exercise exercise = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_preset, parent, false);
            }
            // Lookup view for data population
            TextView nameTxtField = convertView.findViewById(R.id.nameTextView);
            TextView workTimeTxtField = convertView.findViewById(R.id.workTimeTextView);
            TextView workTxtView = convertView.findViewById(R.id.workTextView);
            TextView breakTimeTxtField = convertView.findViewById(R.id.breakTimeTextView);
            TextView breakTxtView = convertView.findViewById(R.id.breakTextView);
            TextView setNumberTxtField = convertView.findViewById(R.id.setNumberTextView);
            // Lookup Buttons to set onClickListener
            final Button deleteButton = convertView.findViewById(R.id.deleteButton);
            Button startButton = convertView.findViewById(R.id.startButton);

            // Populate the data into the template view using the data object
            nameTxtField.setText(exercise.getName());
            //differ between timed and untimed exercises
            if(exercise.getTotalWorkSeconds() != null && exercise.getTotalBreakSeconds() != null) {
                workTimeTxtField.setText(String.valueOf(Time.totalSecondsToTimeString(exercise.getTotalWorkSeconds())));
                breakTimeTxtField.setText(String.valueOf(Time.totalSecondsToTimeString(exercise.getTotalBreakSeconds())));
            }else{
                workTxtView.setVisibility(View.INVISIBLE);
                breakTxtView.setVisibility(View.INVISIBLE);
                workTimeTxtField.setVisibility(View.INVISIBLE);
                breakTimeTxtField.setVisibility(View.INVISIBLE);
            }
            setNumberTxtField.setText(String.valueOf(exercise.getSets()));

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    return AppRoomDatabase.getDatabase(getApplicationContext()).
                            exerciseDao().usedInWorkout(exercise.getId());
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if(fromWorkoutEditNotHomeScreen || aBoolean) {
                        deleteButton.setVisibility(View.GONE);
                    } else {
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AsyncTask<Void, Void, Exercise>() {
                                    @Override
                                    protected Exercise doInBackground(Void... voids) {
                                        AppRoomDatabase.getDatabase(getApplicationContext()).
                                                exerciseDao().deletePreset(exercise);
                                        return exercise;
                                    }

                                    @Override
                                    protected void onPostExecute(Exercise exerciseRoom) {
                                        presetAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Deleted entry", Toast.LENGTH_LONG).show(); //shows notification with the eiwId of the entry
                                        fillData();
                                    }
                                }.execute();
                            }
                        });
                    }
                }
            }.execute();

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent timerIntent = new Intent (getContext(), TimerActivity.class);
                    Bundle b = new Bundle();
                    if(exercise.getTotalWorkSeconds() != null)
                        b.putString("workTime", Time.totalSecondsToTimeString(exercise.getTotalWorkSeconds()));
                    if(exercise.getTotalBreakSeconds() != null)
                        b.putString("breakTime", Time.totalSecondsToTimeString(exercise.getTotalBreakSeconds()));
                    b.putLong("sets", exercise.getSets());
                    b.putString("exerciseName", exercise.getName());
                    b.putInt    ("newExerciseIndex", -1);
                    b.putInt    ("newSetNumber", 1);
                    b.putBoolean("workNotBreak", true);
                    timerIntent.putExtras(b);
                    startActivityForResult(timerIntent, 1);
                }
            });

            //if we come from the workoutEdit screen, not from the homescreen we want not to start the exercise but to save it to the workout
            if(fromWorkoutEditNotHomeScreen)
            {
                startButton.setText("ADD");
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ExerciseInWorkout exerciseInWorkout = new ExerciseInWorkout();
                        exerciseInWorkout.setExerciseId(exercise.getId());
                        exerciseInWorkout.setWorkoutId(workoutId);
                        exerciseInWorkout.setFeatureNumber(1);

                        //look at which number of exercises in the workout we are and put this exercise as the next one in the workout
                        new AsyncTask<Void, Void, Long>() {
                            @Override
                            protected Long doInBackground(Void... voids) {
                                return AppRoomDatabase.getDatabase(getApplicationContext()).
                                        exerciseDao().getMaxFeatureNumberInWorkout(workoutId);
                            }

                            @Override
                            protected void onPostExecute(Long aLong) {
                                exerciseInWorkout.setFeatureNumber(aLong != null ? aLong + 1 : 1);
                                // run database action in background. don't block UI thread
                                new AsyncTask<Void, Void, Long>() {     //new thread
                                    @Override
                                    protected Long doInBackground(Void... voids) { //what happens in the new thread
                                        return AppRoomDatabase.getDatabase(getApplicationContext()).
                                                exerciseDao().insert(exerciseInWorkout); //get database from class which needs ApplicationContext and uses the insert-dunction on the exerciseDao
                                    }

                                    @Override
                                    protected void onPostExecute(Long resultId) { //resultId = parameter from doInBackround
                                        // you can update the UI-Thread with your results here
                                        Toast.makeText(getApplicationContext(), "Exercise added to workout", Toast.LENGTH_LONG).show(); //shows notification with the eiwId of the entry

                                        //close this activity and open the workout view again
                                        finish();
                                        Intent workoutViewIntent = new Intent ( getContext(), ExerciseInWorkoutActivity.class);
                                        workoutViewIntent.putExtra("workoutId", workoutId);
                                        startActivity(workoutViewIntent);
                                    }
                                }.execute();
                            }
                        }.execute();
                    }
                });
            }

            // Return the completed view to render on screen

            return convertView;
        }
    }

    public void onNewExerciseButtonClick(View view) {
        Intent createIntent = new Intent(this, CreateExerciseActivity.class);
        startActivity(createIntent);
    }

    public void backButtonClick(View view) {
        finish();
        Intent mainIntent = new Intent ( this, CreateExerciseActivity.class);
        startActivity(mainIntent);
    }
}

