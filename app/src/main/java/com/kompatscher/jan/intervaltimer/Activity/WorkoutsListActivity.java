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
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;
import com.kompatscher.jan.intervaltimer.Database.Entity.Workout;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;
import com.kompatscher.jan.intervaltimer.Timer.TimerActivity;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsListActivity extends AppCompatActivity {

    private ListView workoutListView;
    private View progressView;
    private ArrayList<Workout> workoutsList;
    private WorkoutAdapter workoutAdapter;

    private static final int DELETE_ID = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts_list);

        workoutListView = findViewById(R.id.workoutListView);
        progressView = findViewById(R.id.progressView);

        workoutsList = new ArrayList<>();
        workoutAdapter = new WorkoutAdapter(this, workoutsList);
        workoutListView.setAdapter(workoutAdapter);

        fillData();
        registerForContextMenu(workoutListView);
    }

    private void showProgress(boolean progress) {
        workoutListView.setVisibility(progress ? View.GONE : View.VISIBLE);
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
        new AsyncTask<Void, Void, List<Workout>>() {
            @Override
            protected List<Workout> doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        workoutDao().findAllWorkouts();
            }

            @Override
            protected void onPostExecute(List<Workout> exercises) {
                // you can update the UI-Thread with your results here
                workoutsList.clear();
                workoutsList.addAll(exercises);
                showProgress(false);
            }
        }.execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    // Source: https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
    class WorkoutAdapter extends ArrayAdapter<Workout> {
        public WorkoutAdapter(Context context, List<Workout> exercises) {
            super(context, 0, exercises);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Workout workout = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_workout, parent, false);
            }

            // Lookup view for data population
            TextView nameTxtField = convertView.findViewById(R.id.nameTextView);
            TextView timeField = convertView.findViewById(R.id.timeView);
            TextView exercisesField = convertView.findViewById(R.id.numberOfExercisesView);
            // Lookup Buttons to set onClickListener
            Button deleteButton = convertView.findViewById(R.id.deleteButton);
            Button editButton = convertView.findViewById(R.id.editButton);
            Button startButton = convertView.findViewById(R.id.startButton);

            // Populate the data into the template view using the data object
            nameTxtField.setText(workout.getName());
            //setTimeAndNoOfExInWorkout(workout.getId(), timeField, exercisesField);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Workout>() {
                        @Override
                        protected Workout doInBackground(Void... voids) {
                            AppRoomDatabase.getDatabase(getApplicationContext()).
                                    workoutDao().deleteWorkout(workout);
                            return workout;
                        }

                        @Override
                        protected void onPostExecute(Workout exerciseRoom) {
                            workoutAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Deleted entry", Toast.LENGTH_LONG).show(); //shows notification with the eiwId of the entry
                            fillData();
                        }
                    }.execute();
                }
            });

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent timerIntent = new Intent (getContext(), TimerActivity.class);
                    Bundle b = new Bundle();
                    b.putLong("workoutId", workout.getId());
                    b.putInt    ("newExerciseIndex", -1);
                    b.putInt    ("newSetNumber", 1);
                    b.putBoolean("workNotBreak", true);
                    timerIntent.putExtras(b);
                    startActivityForResult(timerIntent, 1);
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent exerciseInWorkoutIntent = new Intent (getContext(), ExerciseInWorkoutActivity.class);
                    Bundle b = new Bundle();
                    b.putLong("workoutId", workout.getId());
                    exerciseInWorkoutIntent.putExtras(b);
                    startActivityForResult(exerciseInWorkoutIntent, 1);
                }
            });

            // Return the completed view to render on screen

            return convertView;
        }
    }

    public void onCreateWorkoutButtonClick(View view) {
        finish();
        Intent mainIntent = new Intent( this, SaveWorkoutActivity.class);
        startActivity(mainIntent);
    }

    private void setTimeAndNoOfExInWorkout(final Long workoutId, final TextView timeField, final TextView exercisesField){
        new AsyncTask<Void, Void, List<ExerciseWithFeatureNumber>>() {
            @Override
            protected List<ExerciseWithFeatureNumber> doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        exerciseDao().findAllExercisesInAWorkout(workoutId);
            }

            @Override
            protected void onPostExecute(List<ExerciseWithFeatureNumber> exercises) {
                // you can update the UI-Thread with your results here
                exercisesField.setText(exercises.size());
                long totalWorkoutSeconds = 0;
                for (ExerciseWithFeatureNumber e: exercises) {
                    totalWorkoutSeconds += (e.getTotalWorkSeconds() * e.getSets()) + (e.getTotalBreakSeconds() * (e.getSets() - 1));
                }
                timeField.setText(new Time(totalWorkoutSeconds).getTimeAsString());
                showProgress(false);
            }
        }.execute();
    }
}
