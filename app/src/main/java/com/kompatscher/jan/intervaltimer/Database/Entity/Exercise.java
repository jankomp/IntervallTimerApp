package com.kompatscher.jan.intervaltimer.Database.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String desc;

    private Long totalWorkSeconds;
    private Long totalBreakSeconds;
    private long sets;
    private boolean includeLastBreak;

    public Exercise() {
    }

    public Exercise(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getTotalWorkSeconds() {
        return totalWorkSeconds;
    }


    public void setTotalWorkSeconds(Long totalWorkSeconds) {
        this.totalWorkSeconds = totalWorkSeconds;
    }

    public boolean getIncludeLastBreak() {return includeLastBreak;}

    public void setIncludeLastBreak(boolean includeLastBreak) {
        this.includeLastBreak = includeLastBreak;
    }

    public Long getTotalBreakSeconds() {
        return totalBreakSeconds;
    }

    public void setTotalBreakSeconds(Long totalBreakSeconds) {
        this.totalBreakSeconds = totalBreakSeconds;
    }

    public long getSets() {
        return sets;
    }

    public void setSets(long sets) {
        this.sets = sets;
    }
}
