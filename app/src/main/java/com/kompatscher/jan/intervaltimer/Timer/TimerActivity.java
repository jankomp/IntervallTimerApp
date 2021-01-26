package com.kompatscher.jan.intervaltimer.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Activity.WorkoutPopActivity;
import com.kompatscher.jan.intervaltimer.Controls.OnSwipeTouchListener;
import com.kompatscher.jan.intervaltimer.Database.AppRoomDatabase;
import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;
import com.kompatscher.jan.intervaltimer.R;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends Activity {


    private TextView hourText;
    private TextView minuteText;
    private TextView secondText;
    private TextView dots1;
    private TextView dots2;
    private TextView setsText;

    private TextView workoutNameTextView;
    private TextView exerciseNameTextView;

    private String exerciseName;

    private Button stopButton;
    private Button progressButton;

    private Timer timer;

    private Long workoutId;
    private Long currentFeatureNumber = 1l;

    private int     exerciseIndex;
    private int     numberOfSet;
    private boolean workNotBreak;

    List<ExerciseWithFeatureNumber> exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);

        //inform about swipe controls and set swipe controls
        getWindow().getDecorView().setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                startNextTimerActivity(exerciseIndex, numberOfSet, workNotBreak, false);
                endThisTimerActivity();
            }

            @Override
            public void onSwipeRight() {
                startPreviousTimerActivity(exerciseIndex, numberOfSet, workNotBreak, false);
                endThisTimerActivity();
            }
        });

        exerciseList = new ArrayList<>();

        hourText = findViewById(R.id.hourText);
        minuteText = findViewById(R.id.minuteText);
        secondText = findViewById(R.id.secondText);
        setsText = findViewById(R.id.setNumberText);
        dots1 = findViewById(R.id.dots);
        dots2 = findViewById(R.id.dots2);
        workoutNameTextView = findViewById(R.id.workoutNameTextView);
        exerciseNameTextView = findViewById(R.id.exerciseNameTextView);

        stopButton = findViewById(R.id.stopButton);
        progressButton = findViewById(R.id.progressButton);

        Bundle b = getIntent().getExtras();
        Time workTime = new Time();
        Time breakTime = new Time();
        Long sets = 1l;
        if(b != null) {
            workTime = Time.fromString(b.getString("workTime"));
            breakTime = Time.fromString(b.getString("breakTime"));
            sets = b.getLong("sets");

            exerciseName = b.getString("exerciseName");
            workoutId = b.getLong("workoutId");

            exerciseIndex = b.getInt    ("newExerciseIndex");
            numberOfSet = b.getInt    ("newSetNumber");
            workNotBreak = b.getBoolean("workNotBreak");
        }

        if(workoutId != 0) {

            getExerciseListAndStartExercises(workoutId);
        }else {
            if(exerciseName != null)
                exerciseNameTextView.setText(exerciseName);

            progressButton.setVisibility(View.GONE);
            Long workTimeTotalSeconds = workTime != null ? workTime.getTotalSeconds() : null;
            Long breakTimeTotalSeconds = workTime != null ? breakTime.getTotalSeconds() : null;
            ExerciseWithFeatureNumber aExercise = new ExerciseWithFeatureNumber(workTimeTotalSeconds, breakTimeTotalSeconds, sets);
            exerciseList.add(aExercise);
            if(workTimeTotalSeconds != null && breakTimeTotalSeconds != null) {
                timer = new Timer(this, exerciseList, exerciseIndex, numberOfSet, workNotBreak);
                timer.start();
            }else{
                if(!workNotBreak)
                    setBackgroundColor(Color.YELLOW);
                else
                    setBackgroundColor(Color.CYAN);
                setSetText("" + numberOfSet);
            }
            //set the visibility according to if the exercise is timed or not
            setTimeVisibility(isExerciseTimed());
        }
    }

    private void getExerciseListAndStartExercises(final long workoutId) {
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
                setWorkoutName();
                //set the visibility according to if the exercise is timed or not
                setTimeVisibility(isExerciseTimed());
                if(isExerciseTimed()) {
                    timer = new Timer(getThis(), exerciseList, exerciseIndex, numberOfSet, workNotBreak);
                    timer.start();
                }
            }
        }.execute();
    }

    private void setWorkoutName(){
        // run database action in background. don't block UI thread
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return AppRoomDatabase.getDatabase(getApplicationContext()).
                        workoutDao().getName(workoutId);
            }

            @Override
            protected void onPostExecute(String s) {
                workoutNameTextView.setText(s);
            }
        }.execute();
    }

    private void endThisTimerActivity(){
        if(timer != null)
            timer.stopTimer();
        finish();
    }

    public void onStopButtonClicked(View view){
        if(!timer.isTakingTime()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopButton.setText("PAUSE");
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopButton.setText("RESUME");
                }
            });
        }

        timer.toggleTimer();
    }

    private boolean isExerciseTimed(){
        boolean result = false;
        if(exerciseIndex >= 0 && exerciseIndex < exerciseList.size()){
            if(exerciseList.get(exerciseIndex).getTotalWorkSeconds() != null || exerciseList.get(exerciseIndex).getTotalBreakSeconds() != null)
                result = true;
        }else if(exerciseIndex == -1){
            result = true;
        }
        return result;
    }

    protected void setBackgroundColor(final int color){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().setBackgroundColor(color);
            }
        });
    }

    //toggles the visibility o
    protected void setTimeVisibility(final boolean visible){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(visible){
                    hourText.setVisibility(View.VISIBLE);
                    minuteText.setVisibility(View.VISIBLE);
                    secondText.setVisibility(View.VISIBLE);
                    dots1.setVisibility(View.VISIBLE);
                    dots2.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                }else{
                    hourText.setVisibility(View.INVISIBLE);
                    minuteText.setVisibility(View.INVISIBLE);
                    secondText.setVisibility(View.INVISIBLE);
                    dots1.setVisibility(View.INVISIBLE);
                    dots2.setVisibility(View.INVISIBLE);
                    stopButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    protected void setSetText(final String set){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setsText.setText(set);
            }
        });
    }

    protected void setExerciseNameText(final String exerciseName){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                exerciseNameTextView.setText(exerciseName);
            }
        });
    }

    protected void setTime(final String hours, final  String minutes, final  String seconds) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hourText.setText(hours);
                minuteText.setText(minutes);
                secondText.setText(seconds);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Quit?");
            builder.setMessage("Are you sure you wan't to end the workout?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            endThisTimerActivity();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void wellDoneSplashScreen(){
        Intent i = new Intent(this, WellDoneActivity.class);
        startActivity(i);
        finish(); //finish TimerActivity
    }

    private TimerActivity getThis(){
        return this;
    }

    protected void makeToast(final String toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCurrentFeatureNumber(Long currentFeatureNumber) {
        this.currentFeatureNumber = currentFeatureNumber;
    }

    public void onProgressButtonClick(View view){
        Intent exerciseInWorkoutIntent = new Intent (this, WorkoutPopActivity.class);
        Bundle b = new Bundle();
        b.putLong("workoutId", workoutId);
        b.putLong("currentFeatureNumber", currentFeatureNumber);
        exerciseInWorkoutIntent.putExtras(b);
        startActivityForResult(exerciseInWorkoutIntent, 1);
    }

    public void startNextTimerActivity(int exerciseIndex, int setNumber, boolean workNotBreak, boolean ended) {
        int newExerciseIndex = exerciseIndex;
        int newSetNumber = setNumber;
        boolean newWorkNotBreak = workNotBreak;


        Intent exerciseInWorkoutIntent = new Intent (this, TimerActivity.class);
        Bundle b = new Bundle();


        if(exerciseIndex < 0){
            newExerciseIndex = 0;
            newSetNumber = 1;
            newWorkNotBreak = true;

            ExerciseWithFeatureNumber newExercise = exerciseList.get(0);
            if(workoutId == 0) {
                b.putString("workTime", newExercise != null && newExercise.getTotalWorkSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalWorkSeconds()) : null);
                b.putString("breakTime", newExercise != null && newExercise.getTotalBreakSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalBreakSeconds()) : null);
                b.putLong("sets", newExercise != null ? newExercise.getSets() : 0);

                b.putString("exerciseName", newExercise != null && newExercise.getName() != null ? newExercise.getName() : null);
            }
        } else {
            ExerciseWithFeatureNumber currentExercise = exerciseList.get(exerciseIndex);
            ExerciseWithFeatureNumber newExercise = null;

            if (setNumber < currentExercise.getSets()) {
                newExercise = currentExercise;
                if (!workNotBreak) { //We're in a break set right now
                    newWorkNotBreak = true;
                    newSetNumber++; //change to next set
                    if (newExercise.getTotalWorkSeconds() != null && currentExercise.getTotalWorkSeconds() == 0) {
                        newWorkNotBreak = false;
                    }
                }else{
                    newWorkNotBreak = false;
                    if (newExercise.getTotalBreakSeconds() != null && currentExercise.getTotalBreakSeconds() == 0) {
                        newWorkNotBreak = true;
                        newSetNumber++;
                    }
                }
            } else {
                newExerciseIndex++;
                if (exerciseList.size() > newExerciseIndex) { //change to next exercise
                    newExercise = exerciseList.get(exerciseIndex + 1);
                    newSetNumber = 1;
                    if (newExercise.getTotalWorkSeconds() != null && newExercise.getTotalWorkSeconds() == 0)
                        newWorkNotBreak = true;
                    else if (newExercise.getTotalBreakSeconds() != null && newExercise.getTotalBreakSeconds() == 0)
                        newWorkNotBreak = false;
                } else{
                    wellDoneSplashScreen();//end workout
                    ended = true;
                }
            }


            if(workoutId == 0) {
                b.putString("workTime", newExercise != null && newExercise.getTotalWorkSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalWorkSeconds()) : null);
                b.putString("breakTime", newExercise != null && newExercise.getTotalBreakSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalBreakSeconds()) : null);
                b.putLong("sets", newExercise != null ? newExercise.getSets() : 0);

                b.putString("exerciseName", newExercise != null && newExercise.getName() != null ? newExercise.getName() : null);
            }
        }
        if(timer != null)
            timer.stopTimer();

        finish();
        if(!ended) {
            b.putLong("workoutId", workoutId);
            b.putInt("newExerciseIndex", newExerciseIndex);
            b.putInt("newSetNumber", newSetNumber);
            b.putBoolean("workNotBreak", newWorkNotBreak);
            exerciseInWorkoutIntent.putExtras(b);
            startActivityForResult(exerciseInWorkoutIntent, 1);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void startPreviousTimerActivity(int exerciseIndex, int setNumber, boolean workNotBreak, boolean ended) {
        int newExerciseIndex = exerciseIndex;
        int newSetNumber = setNumber;
        boolean newWorkNotBreak = workNotBreak;


        Intent exerciseInWorkoutIntent = new Intent (this, TimerActivity.class);
        Bundle b = new Bundle();

        if(exerciseIndex < 0){
            newExerciseIndex = 0;
            newSetNumber = 1;
            newWorkNotBreak = true;

            ExerciseWithFeatureNumber newExercise = exerciseList.get(0);
            if(workoutId == 0) {
                b.putString("workTime", newExercise != null && newExercise.getTotalWorkSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalWorkSeconds()) : null);
                b.putString("breakTime", newExercise != null && newExercise.getTotalBreakSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalBreakSeconds()) : null);
                b.putLong("sets", newExercise != null ? newExercise.getSets() : 0);

                b.putString("exerciseName", newExercise != null && newExercise.getName() != null ? newExercise.getName() : null);
            }
        } else {
            ExerciseWithFeatureNumber currentExercise = exerciseList.get(exerciseIndex);
            ExerciseWithFeatureNumber newExercise = null;

            if (setNumber > 1) {
                newExercise = currentExercise;
                if (!workNotBreak) {
                    newWorkNotBreak = true;
                    if (currentExercise.getTotalWorkSeconds() == 0) {
                        newWorkNotBreak = false;
                        newSetNumber--;
                    }
                }else{
                    newWorkNotBreak = false;
                    newSetNumber--; //change to previous set
                    if (currentExercise.getTotalBreakSeconds() == 0) {
                        newWorkNotBreak = true;
                    }
                }
            } else {
                newExerciseIndex--;
                if (newExerciseIndex >= 0) { //change to previous exercise
                    newExercise = exerciseList.get(newExerciseIndex);
                    newSetNumber = (int)newExercise.getSets();
                    if (newExercise.getTotalBreakSeconds() == 0)
                        newWorkNotBreak = false;
                    else if (newExercise.getTotalWorkSeconds() == 0)
                        newWorkNotBreak = true;
                }
            }


            if(workoutId == 0) {
                b.putString("workTime", newExercise != null && newExercise.getTotalWorkSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalWorkSeconds()) : null);
                b.putString("breakTime", newExercise != null && newExercise.getTotalBreakSeconds() != null ? Time.totalSecondsToTimeString(newExercise.getTotalBreakSeconds()) : null);
                b.putLong("sets", newExercise != null ? newExercise.getSets() : 0);

                b.putString("exerciseName", newExercise != null && newExercise.getName() != null ? newExercise.getName() : null);
            }
        }
        if(timer != null)
            timer.stopTimer();
        finish();
        if(!ended) {
            b.putLong("workoutId", workoutId);
            b.putInt("newExerciseIndex", newExerciseIndex);
            b.putInt("newSetNumber", newSetNumber);
            b.putBoolean("workNotBreak", newWorkNotBreak);
            exerciseInWorkoutIntent.putExtras(b);
            startActivityForResult(exerciseInWorkoutIntent, 1);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
