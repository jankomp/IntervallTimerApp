package com.kompatscher.jan.intervaltimer.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.kompatscher.jan.intervaltimer.Activity.Filter.InputFilterMinMax;
import com.kompatscher.jan.intervaltimer.R;
import com.kompatscher.jan.intervaltimer.Timer.Time;
import com.kompatscher.jan.intervaltimer.Timer.TimerActivity;

public class CreateExerciseActivity extends AppCompatActivity {

    private long workHours;
    private long workMinutes;
    private long workSeconds;
    private long breakHours;
    private long breakMinutes;
    private long breakSeconds;
    private long sets;

    private Time workTime;
    private Time breakTime;

    EditText workHourInput,  workMinuteInput,  workSecondInput,
            breakHourInput, breakMinuteInput, breakSecondInput,
            setsNumberInput;

    CheckBox includeLastBreakCheckBox;
    Switch timedSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);

        workHourInput = findViewById(R.id.workHourInput);
        workMinuteInput = findViewById(R.id.workMinuteInput);
        workSecondInput = findViewById(R.id.workSecondInput);
        breakHourInput = findViewById(R.id.breakHourInput);
        breakMinuteInput = findViewById(R.id.breakMinuteInput);
        breakSecondInput = findViewById(R.id.breakSecondInput);
        setsNumberInput = findViewById(R.id.setNumberInput);
        includeLastBreakCheckBox = findViewById(R.id.includeLastBreakCheckBox);
        timedSwitch = findViewById(R.id.timedSwitch);

        workHourInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "99")});
        workMinuteInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});
        workSecondInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});
        breakHourInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "99")});
        breakMinuteInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});
        breakSecondInput.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});
        setsNumberInput.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});

        workHours = 0;
        workMinutes = 0;
        workSeconds = 0;
        breakHours = 0;
        breakMinutes = 0;
        breakSeconds = 0;
        sets = 1;
        workTime = new Time();
        breakTime = new Time();
    }

    public void startButtonClick(View view){
        setInput();
        Intent timerIntent = new Intent (this, TimerActivity.class);
        Bundle b = new Bundle();

        if(workTime != null)
            b.putString("workTime", workTime.toString());
        if(breakTime != null)
            b.putString("breakTime", breakTime.toString());

        if(timedSwitch.isChecked())
            b.putInt    ("newExerciseIndex", -1);
        else
            b.putInt    ("newExerciseIndex", 0);

        b.putLong("sets", sets);
        b.putBoolean("includeLastBreak", includeLastBreakCheckBox.isChecked());
        b.putInt    ("newSetNumber", 1);
        b.putBoolean("workNotBreak", true);
        timerIntent.putExtras(b);
        startActivityForResult(timerIntent, 1);
    }

    public void onSaveButtonClick(View view) {
        setInput();
        Intent timerIntent = new Intent (this, SaveExercisePresetActivity.class);
        Bundle b = new Bundle();

        if(workTime != null)
            b.putString("workTime", workTime.toString());
        if(breakTime != null)
            b.putString("breakTime", breakTime.toString());

        b.putLong("sets", sets);
        b.putBoolean("includeLastBreak", includeLastBreakCheckBox.isChecked());
        timerIntent.putExtras(b);
        startActivityForResult(timerIntent, 1);
        finish();
    }

    public void onPresetListButtonClick(View view){
        Intent presetsIntent = new Intent (this, ExercisePresetsListActivity.class);
        startActivity(presetsIntent);
        finish();
    }

    public void onMoreSetsButtonClick(View view){
        int sets = Integer.parseInt(setsNumberInput.getText().toString());
        if(sets + 1 <= 99)
            setsNumberInput.setText("" + ++sets );
    }

    public void onLessSetsButtonClick(View view){
        int sets = Integer.parseInt(setsNumberInput.getText().toString());
        if(sets - 1 > 0)
        setsNumberInput.setText("" + --sets );
    }

    public void onTimedSwitchClick(View view){
        if(timedSwitch.isChecked()){
            TextView workTimeTextView = findViewById(R.id.worTimeTextView);
            TextView breakTimeTextView = findViewById(R.id.breakTextView);
            workTimeTextView.setVisibility(view.VISIBLE);
            breakTimeTextView.setVisibility(view.VISIBLE);

            workHourInput.setVisibility(view.VISIBLE);
            workMinuteInput.setVisibility(view.VISIBLE);
            workSecondInput.setVisibility(view.VISIBLE);
            breakHourInput.setVisibility(view.VISIBLE);
            breakMinuteInput.setVisibility(view.VISIBLE);
            breakSecondInput.setVisibility(view.VISIBLE);
            includeLastBreakCheckBox.setVisibility(view.VISIBLE);
        }else{
            TextView workTimeTextView = findViewById(R.id.worTimeTextView);
            TextView breakTimeTextView = findViewById(R.id.breakTextView);
            workTimeTextView.setVisibility(view.INVISIBLE);
            breakTimeTextView.setVisibility(view.INVISIBLE);

            workHourInput.setVisibility(view.INVISIBLE);
            workMinuteInput.setVisibility(view.INVISIBLE);
            workSecondInput.setVisibility(view.INVISIBLE);
            breakHourInput.setVisibility(view.INVISIBLE);
            breakMinuteInput.setVisibility(view.INVISIBLE);
            breakSecondInput.setVisibility(view.INVISIBLE);
            includeLastBreakCheckBox.setVisibility(view.INVISIBLE);
        }
    }

    private void setInput () {
        if(timedSwitch.isChecked()) {
            workHours = Long.parseLong(workHourInput != null ? workHourInput.getText().toString() : "00");
            workMinutes = Long.parseLong(workMinuteInput != null ? workMinuteInput.getText().toString() : "00");
            workSeconds = Long.parseLong(workSecondInput != null ? workSecondInput.getText().toString() : "00");
            breakHours = Long.parseLong(breakHourInput != null ? breakHourInput.getText().toString() : "00");
            breakMinutes = Long.parseLong(breakMinuteInput != null ? breakMinuteInput.getText().toString() : "00");
            breakSeconds = Long.parseLong(breakSecondInput != null ? breakSecondInput.getText().toString() : "00");
            sets = Long.parseLong(setsNumberInput != null ? setsNumberInput.getText().toString() : "00");

            workTime.setHours(workHours);
            workTime.setMinutes(workMinutes);
            workTime.setSeconds(workSeconds);

            breakTime.setHours(breakHours);
            breakTime.setMinutes(breakMinutes);
            breakTime.setSeconds(breakSeconds);
        }else{
            workTime = null;
            breakTime = null;
            sets = Long.parseLong(setsNumberInput != null ? setsNumberInput.getText().toString() : "00");
        }
    }
}
