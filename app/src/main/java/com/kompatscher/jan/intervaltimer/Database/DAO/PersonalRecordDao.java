package com.kompatscher.jan.intervaltimer.Database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.kompatscher.jan.intervaltimer.Database.Entity.PersonalRecordType;

@Dao
public interface PersonalRecordDao {
    @Insert
    void insert(PersonalRecordType personalRecordType);
}
