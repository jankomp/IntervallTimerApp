package com.kompatscher.jan.intervaltimer.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.kompatscher.jan.intervaltimer.Database.DAO.ExerciseDao;
import com.kompatscher.jan.intervaltimer.Database.DAO.PersonalRecordDao;
import com.kompatscher.jan.intervaltimer.Database.DAO.WorkoutDao;
import com.kompatscher.jan.intervaltimer.Database.Entity.Exercise;
import com.kompatscher.jan.intervaltimer.Database.Entity.ExerciseInWorkout;
import com.kompatscher.jan.intervaltimer.Database.Entity.PersonalRecordType;
import com.kompatscher.jan.intervaltimer.Database.Entity.Workout;

@Database(entities = {Exercise.class, Workout.class, ExerciseInWorkout.class, PersonalRecordType.class}, version = 5)
public abstract class AppRoomDatabase extends RoomDatabase{
    public abstract ExerciseDao exerciseDao();
    public abstract WorkoutDao workoutDao();
    public abstract PersonalRecordDao personalRecordDao();

    private static AppRoomDatabase INSTANCE;

    // Singleton Pattern, only one database is available in the app
    public static AppRoomDatabase getDatabase(Context context) {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppRoomDatabase.class, "exercise_preset_database")
                        .fallbackToDestructiveMigration().build(); //.fallbackToDestructiveMigration().build(); // <- use when making another db version
            }
            return INSTANCE;
    }

}
