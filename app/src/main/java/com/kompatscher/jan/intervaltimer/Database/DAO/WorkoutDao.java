package com.kompatscher.jan.intervaltimer.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.kompatscher.jan.intervaltimer.Database.Entity.Workout;

import java.util.List;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workout ORDER BY id ASC")
    List<Workout> findAllWorkouts();

    @Delete
    void deleteWorkout(Workout workout);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Workout workout);

    @Query("SELECT name FROM workout WHERE id = :workoutId")
    String getName(long workoutId);
}
