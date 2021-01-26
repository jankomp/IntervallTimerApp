package com.kompatscher.jan.intervaltimer.Timer;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.Toast;

import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;

import java.util.List;

public class Timer extends Thread implements Runnable {

    private TimerActivity timerActivity;


    private boolean takingTime = true;

    private boolean stop = false;

    private long timerInterruptedAtMillis;
    private long timeForSeconds;
    private long endTime;
    boolean startTimer = false;
    private List<ExerciseWithFeatureNumber> exerciseList;
    private int exerciseIndex;
    private int setNumber;
    private boolean workNotBreak;

    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);


    public Timer(TimerActivity timerActivity, List<ExerciseWithFeatureNumber> eList, int exerciseIndex, int setNumber, boolean workNotBreak){
        this.timerActivity = timerActivity;
        this.exerciseList = eList;

        this.exerciseIndex = exerciseIndex;
        this.setNumber = setNumber;
        this.workNotBreak = workNotBreak;
    }

    @Override
    public void run(){
        if(exerciseIndex < 0) {
            timerActivity.setSetText("1/" + exerciseList.get(0).getSets());
            //Initial Countdown
            startTimer(5);
            timerActivity.makeToast("Swipe left/right to change sets"); //shows controls
        }else if(exerciseIndex < exerciseList.size()){

            ExerciseWithFeatureNumber e = exerciseList.get(exerciseIndex);

            //set the currentFeature number to graphically show the progress on the progress popup
            timerActivity.setCurrentFeatureNumber(e.getFeatureNumber());

            if (e.getName() != null)
                timerActivity.setExerciseNameText(e.getName());

            timerActivity.setSetText(setNumber + "/" + e.getSets());

            if (workNotBreak) {
                //Work
                timerActivity.setBackgroundColor(Color.CYAN);

                if (e.getTotalWorkSeconds() != 0) {
                    startTimer(e.getTotalWorkSeconds());
                }
            } else {
                //Break (No break in last set)
                timerActivity.setBackgroundColor(Color.YELLOW);
                boolean lastSet = setNumber == e.getSets();
                if (!lastSet || e.getIncludeLastBreak()) {
                    startTimer(e.getTotalBreakSeconds());
                }
            }

        }

        startNewTimerActivity(exerciseIndex, setNumber, workNotBreak, false);
    }

    private void startTimer(long seconds){
        try {
            Time timeInSet = new Time(seconds);

            long startTime = System.currentTimeMillis();
            timeForSeconds = startTime + 1000;
            endTime = startTime + (seconds * 1000);
            while (true) {
                //for pause-button
                if(takingTime) {
                    long runTime = System.currentTimeMillis();

                    if (runTime >= timeForSeconds) {
                        boolean beep = false;

                        timeForSeconds += 1000;
                        timeInSet.tick();
                        beep = timeInSet.beep();

                        if (beep)
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                    }
                    //Display the Time
                    timerActivity.setTime(String.format("%02d", timeInSet.getHours()),
                            String.format("%02d", timeInSet.getMinutes()),
                            String.format("%02d", timeInSet.getSeconds()));

                    //Play End-Beep & Exit Loop
                    if (runTime >= endTime) {
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300);
                        break;
                    }
                    //to end the thread
                    if(stop)
                        break;
                }
                sleep(50);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void startUntimedExercise(){
        try {
            sleep(50);
        }catch(Exception ignored){}
    }

    private void startNewTimerActivity(int newExerciseIndex, int newSetNumber, boolean workNotBreak, boolean ended) {
        timerActivity.startNextTimerActivity(newExerciseIndex, newSetNumber, workNotBreak, ended);
    }

    boolean isTakingTime() {
        return takingTime;
    }

    void toggleTimer(){
        if(takingTime){
            takingTime = false;
            timerInterruptedAtMillis = System.currentTimeMillis();
        }else{
            takingTime = true;
            timeForSeconds += (System.currentTimeMillis() - timerInterruptedAtMillis);
            endTime += (System.currentTimeMillis() - timerInterruptedAtMillis);
        }
    }

    void stopTimer(){
        stop = true;
    }

}
