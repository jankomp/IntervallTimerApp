package com.kompatscher.jan.intervaltimer.Database.Entity;

public class ExerciseWithFeatureNumber {
    private long id;

    private String name;

    private String desc;

    private Long totalWorkSeconds;
    private Long totalBreakSeconds;
    private long sets;
    private long featureNumber;
    private long eiwId;
    private long workoutId;
    private long exerciseId;
    private boolean includeLastBreak;

    public ExerciseWithFeatureNumber() {
    }

    public ExerciseWithFeatureNumber(long sets) {
        this.sets = sets;
    }

    public ExerciseWithFeatureNumber(Long totalWorkSeconds, Long totalBreakSeconds, long sets) {
        this.totalWorkSeconds = totalWorkSeconds;
        this.totalBreakSeconds = totalBreakSeconds;
        this.sets = sets;
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

    public long getFeatureNumber() {
    return featureNumber;
    }

    public void setFeatureNumber(long featureNumber) {
    this.featureNumber = featureNumber;
    }

    public long getEiwId() {
        return eiwId;
    }

    public void setEiwId(long eiwId) {
        this.eiwId = eiwId;
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

    public ExerciseInWorkout getExerciseInWorkoutAndAssignNewFeatureNumber(long featureNumber){
        return new ExerciseInWorkout(eiwId, workoutId, exerciseId, featureNumber);
    }

    public boolean getIncludeLastBreak() {
        return includeLastBreak;
    }

    public void setIncludeLastBreak(boolean includeLastBreak) {
        this.includeLastBreak = includeLastBreak;
    }
}
