package com.kompatscher.jan.intervaltimer.Timer;

public class Time {
    private long hours;
    private long minutes;
    private long seconds;

    private long totalSeconds;

    public Time() {
    }

    public Time(Time time){
        this.hours = time.hours;
        this.minutes = time.minutes;
        this.seconds = time.seconds;
        this.totalSeconds = time.totalSeconds;
    }

    public Time(int hours, int minutes, int seconds){
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        totalSeconds = (hours*3600) + (minutes*60) + seconds;
    }

    public Time(long totalSeconds) {
        this.hours = (int)totalSeconds / 3600;
        this.minutes = (int)(totalSeconds - (hours*3600)) / 60;
        this.seconds = (int)totalSeconds % 60;
        this.totalSeconds = (int)totalSeconds;
    }

    public String getTimeAsString(){
        return "" + String.format("%02d", this.hours) + ":" + String.format("%02d", this.minutes) + ":" + String.format("%02d", this.seconds);
    }

    public void tick(){
        if(totalSeconds > 0)
            totalSeconds -= 1;

        hours = totalSeconds / 3600;
        minutes = (totalSeconds - (hours*3600)) / 60;
        seconds = totalSeconds % 60;
    }

    public String toString(){
        return hours + ":" + minutes + ":" + seconds;
    }

    public static Time fromString(String timeString){
        if(timeString != null){
            Time aTime = new Time();
            String timeStrings[] = timeString.split(":");
            aTime.hours = Integer.decode(timeStrings[0]);
            aTime.minutes = Integer.decode(timeStrings[1]);
            aTime.seconds = Integer.decode(timeStrings[2]);

            aTime.totalSeconds = (aTime.hours*3600) + (aTime.minutes*60) + aTime.seconds;
            return aTime;
        }
        return null;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    boolean beep(){
        if(totalSeconds < 4 && totalSeconds != 0)
            return true;
        else
            return false;
    }

    public static String totalSecondsToTimeString(long totalSeconds){
        Time aTime = new Time(totalSeconds);
        return aTime.getTimeAsString();
    }
}
