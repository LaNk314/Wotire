package com.example.wotire;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.InsetDialogOnTouchListener;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

public class Entry {
    public static ArrayList<Entry> entryArrayList = new ArrayList<>();
    public static String ENTRY_EDIT_EXTRA = "entryEdit";

    private int id;
    Date startTime;
    Date endTime;
    Date deleted;
    String role;
    double hoursOfWork;
    String hoursofWorkString;

    SimpleDateFormat sdfD = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Entry(int id, Date startTime, Date endTime, String role, Date deleted) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = deleted;
        this.role = role;
        calculateHoursOfWork(startTime, endTime);
    }

    public Entry(int id, Date startTime, Date endTime, String role) {
        this.id = id;
        this.role = role;
        this.startTime = startTime;
        this.endTime = endTime;
        calculateHoursOfWork(startTime, endTime);
    }

    public static Entry getEntryForID(int passEntryID) {
        for (Entry entry : entryArrayList){
            if(entry.getId() == passEntryID)
                return entry;
        }
        return null;
    }

    public static ArrayList<Entry> nonDeletedEntries(){
        ArrayList<Entry> nonDeleted = new ArrayList<>();
        for(Entry entry : entryArrayList){
            if(entry.getDeleted() == null)
                nonDeleted.add(entry);
        }
        return nonDeleted;
    }

    private double calculateHoursOfWork(Date startTime, Date endTime){
        Formatter formatter = new Formatter();

        double difference = endTime.getTime() - startTime.getTime();
        this.hoursOfWork = difference / 3600000;
        formatter.format("%.2f", this.hoursOfWork);
        this.hoursofWorkString = formatter.toString();

        return difference;
    }

    @NonNull
    @Override
    public String toString() {
        Formatter formatter  = new Formatter();
        formatter.format("%.2f", hoursOfWork);

        return (formatter.toString());
    }


    public String acquireDate(Date date){
        return sdfD.format(date);
    }

    public String acquireHour(Date date){
        return sdfH.format((date));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getHoursOfWork() {
        return hoursOfWork;
    }

    public void setHoursOfWork(double hoursOfWork) {
        this.hoursOfWork = hoursOfWork;
    }
}
