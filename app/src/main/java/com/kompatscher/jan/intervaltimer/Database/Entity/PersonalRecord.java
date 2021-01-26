package com.kompatscher.jan.intervaltimer.Database.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDateTime;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity
public class PersonalRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ForeignKey(entity = PersonalRecordType.class,
            parentColumns = "id",
            childColumns = "typeId",
            onDelete = CASCADE)
    private long typeId;

    private LocalDateTime date;

    private double value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
