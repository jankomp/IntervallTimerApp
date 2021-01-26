package com.kompatscher.jan.intervaltimer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CarrierConfigManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPopActivity extends Activity {

    private ArrayList<ExerciseWithFeatureNumber> exerciseList;
    private Long workoutId;
    private Long currentFeatureNumber;
    private ExerciseAdapter exerciseAdapter;
    private ListView exerciseListView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_pop);

        exerciseListView = findViewById(R.id.exerciseListView);
        progressView = findViewById(R.id.progressView);

        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(this, exerciseList);
        exerciseListView.setAdapter(exerciseAdapter);

        //make activity a popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.85), (int)(height*.85));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            workoutId = b.getLong("workoutId");
            currentFeatureNumber = b.getLong("currentFeatureNumber");
        }

        fillData(workoutId);
        registerForContextMenu(exerciseListView);
    }

    private void showProgress(boolean progress) {
        exerciseListView.setVisibility(progress ? View.GONE : View.VISIBLE);
        progressView.setVisibility(progress ? View.VISIBLE : View.GONE);
    }


    private void fillData(final long workoutId) {
        showProgress(true);

        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, List<ExerciseWithFeatureNumber>>() {
            @Override
            protected List<ExerciseWithFeatureNumber> doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        exerciseDao().findAllExercisesInAWorkout(workoutId);
            }

            @Override
            protected void onPostExecute(List<ExerciseWithFeatureNumber> exercises) {
                // you can update the UI-Thread with your results here
                exerciseList.clear();
                exerciseList.addAll(exercises);
                showProgress(false);
            }
        }.execute();
    }

    class ExerciseAdapter extends ArrayAdapter<ExerciseWithFeatureNumber> {
        public  ExerciseAdapter(Context context, List<ExerciseWithFeatureNumber> exercises) {
            super(context, 0, exercises);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final ExerciseWithFeatureNumber exercise = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_exercise_in_workout, parent, false);
            }
            // Lookup view for data population
            TextView nameTxtField = convertView.findViewById(R.id.nameTextView);
            TextView workTimeTxtField = convertView.findViewById(R.id.workTimeTextView);
            TextView workTxtView = convertView.findViewById(R.id.workTextView);
            TextView breakTimeTxtField = convertView.findViewById(R.id.breakTimeTextView);
            TextView breakTxtView = convertView.findViewById(R.id.breakTextView);
            TextView setNumberTxtField = convertView.findViewById(R.id.setNumberTextView);
            TextView setsTxtField = convertView.findViewById(R.id.setsTextView);

            nameTxtField.setTextSize(14);
            workTimeTxtField.setTextSize(14);
            workTxtView.setTextSize(14);
            breakTimeTxtField.setTextSize(14);
            breakTxtView.setTextSize(14);
            setsTxtField.setTextSize(14);
            setNumberTxtField.setTextSize(14);
            // Lookup Buttons to set onClickListener
            convertView.findViewById(R.id.deleteButton).setVisibility(View.GONE);
            convertView.findViewById(R.id.moveButton).setVisibility(View.GONE);

            //make the text size bigger for recognition of the current exercise
            if(currentFeatureNumber == exercise.getEiwId()){
                nameTxtField.setTextSize(16); nameTxtField.setTextColor(Color.WHITE);
                workTimeTxtField.setTextSize(16); workTimeTxtField.setTextColor(Color.WHITE);
                workTxtView.setTextSize(16); workTxtView.setTextColor(Color.WHITE);
                breakTimeTxtField.setTextSize(16); breakTimeTxtField.setTextColor(Color.WHITE);
                breakTxtView.setTextSize(16); breakTxtView.setTextColor(Color.WHITE);
                setsTxtField.setTextSize(16); setsTxtField.setTextColor(Color.WHITE);
                setNumberTxtField.setTextSize(16); setNumberTxtField.setTextColor(Color.WHITE);
            }

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

            // Return the completed view to render on screen

            return convertView;
        }
    }

}
