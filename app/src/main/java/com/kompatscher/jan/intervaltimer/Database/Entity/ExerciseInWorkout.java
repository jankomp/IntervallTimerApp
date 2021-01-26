package com.kompatscher.jan.intervaltimer.Database.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "workout_exercise_join",
        foreignKeys = {
                @ForeignKey(entity = Workout.class,
                        parentColumns = "id",
                        childColumns = "workoutId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId")
        })
public class ExerciseInWorkout {
    @PrimaryKey(autoGenerate = true)
    public long eiwId;
    public long workoutId;
    public long exerciseId;
    public long featureNumber;

    public ExerciseInWorkout(){}

    public ExerciseInWorkout(long eiwId, long workoutId, long exerciseId, long featureNumber) {
        this.eiwId = eiwId;
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
        this.featureNumber = featureNumber;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public long getFeatureNumber() {
        return featureNumber;
    }

    public void setFeatureNumber(long featureNumber) {
        this.featureNumber = featureNumber;
    }
}
