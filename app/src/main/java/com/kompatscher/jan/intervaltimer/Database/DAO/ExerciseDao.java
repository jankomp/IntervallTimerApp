package com.kompatscher.jan.intervaltimer.Database.DAO;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseWithFeatureNumber;

import java.util.List;

@Dao
public interface ExerciseDao {

    /*
     * shows all score-entries ordered from high to low
     * */
    @Query("SELECT * FROM Exercise ORDER BY id ASC")
    List<Exercise> findAllPresetsAsc();

    //@Query("SELECT * FROM Exercise e INNER JOIN workout_exercise_join wej ON e.eiwId=wej.exerciseId WHERE wej.workoutId=:workoutId ORDER BY wej.featureNumber ASC")
    @Query("SELECT * FROM Exercise e LEFT JOIN workout_exercise_join wej ON e.id = wej.exerciseId WHERE wej.workoutId = :workoutId ORDER BY wej.featureNumber")
    List<ExerciseWithFeatureNumber> findAllExercisesInAWorkout(Long workoutId);

    @Query("SELECT MAX (featureNumber) FROM workout_exercise_join WHERE workoutId = :workoutId")
    Long getMaxFeatureNumberInWorkout(long workoutId);

    @Update
    void update(ExerciseInWorkout exerciseInWorkout);

    @Query("SELECT CASE WHEN EXISTS (SELECT * FROM workout_exercise_join WHERE exerciseId = :exerciseId)" +
            "THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END")
    boolean usedInWorkout(long exerciseId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ExerciseInWorkout exerciseInWorkout);

    @Query("DELETE FROM workout_exercise_join WHERE eiwId = :eiwId")
    void removeExerciseFromWorkout(long eiwId);

    @Delete
    void deletePreset(Exercise exercise);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Exercise exercise);

}
