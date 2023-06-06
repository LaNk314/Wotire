package com.example.wotire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.text.UnicodeSetSpanner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
    private static DatabaseHelper databaseHelper;

    private static final String DATABASE_NAME = "mydb.db";
    private static final String TABLE_NAME = "entries3";
    private  static final int DATABASE_VERSION = 10;
    private static final String COUNTER = "Counter";
    private static final String ID = "id";
    private static final String TIME_IN = "[time in]";
    private static final String TIME_OUT = "[time out]";
    private static final String ROLE = "role";
    private static final String DELETED = "deleted";
    private static final String CREATE_TABLE =
            "create table "+ TABLE_NAME+ " ("+
            COUNTER+" integer primary key autoincrement, "+
            ID+ " int, "+
            TIME_IN+ " text, "+
            TIME_OUT+ " text, "+
            ROLE+ " text, "+
            DELETED+ " text)";
    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;
    private static final String ORDER_DESC = "order by " + COUNTER + " desc";
    //private static final String[] ARGS = {ORDER_DESC};
    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        //Toast.makeText(context, "constructor called", Toast.LENGTH_SHORT).show();
    }

    public static DatabaseHelper instanceOfDatabase(Context context){
        if(databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);

        return databaseHelper;
    }

    public void addNewEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, entry.getId());
        contentValues.put(TIME_IN, getStringFroDate(entry.getStartTime()));
        contentValues.put(TIME_OUT, getStringFroDate(entry.getEndTime()));
        contentValues.put(ROLE, entry.getRole());
        contentValues.put(DELETED, getStringFroDate(entry.getDeleted()));

        db.insert(TABLE_NAME, null, contentValues);

        db.close();

    }

    public double sumHoursFromMonth(int month, int year){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        double sumHours = 0;

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE "+ DELETED + " IS NULL", null)){
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    String startTimeString = result.getString(2);
                    String endTimeString = result.getString(3);

                    Date startTime = getDateFromString(startTimeString);
                    Date endTime = getDateFromString(endTimeString);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startTime);
                    int entryMonth = cal.get(Calendar.MONTH) +1;
                    int entryYear = cal.get(Calendar.YEAR);

                    if(entryMonth == month && entryYear == year) {
                        double difference = endTime.getTime() - startTime.getTime();
                        sumHours += difference / 3600000;
                    }
                }
            }
            return sumHours;

        }

    }

    public void populateEntryListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME +" "+ORDER_DESC, null )){
            if(result.getCount() != 0){
//                result.moveToLast();
                while (result.moveToNext()){
                    int id = result.getInt(1);
                    String stringStartTime = result.getString(2);
                    String stringEndTime = result.getString(3);
                    String role = result.getString(4);
                    String stringDeleted = result.getString(5);

                    Date startTime  = getDateFromString(stringStartTime);
                    Date endTime = getDateFromString(stringEndTime);
                    Date deleted = getDateFromString(stringDeleted);

                    Entry entry = new Entry(id, startTime, endTime, role, deleted);
                    Entry.entryArrayList.add(entry);
                }
            }

        }

    }

    public void updateEntryInDB(Entry entry){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, entry.getId());
        contentValues.put(TIME_IN, getStringFroDate(entry.getStartTime()));
        contentValues.put(TIME_OUT, getStringFroDate(entry.getEndTime()));
        contentValues.put(ROLE, entry.getRole());
        contentValues.put(DELETED, getStringFroDate(entry.getDeleted()));

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID + " =? ", new String[]{String.valueOf(entry.getId())});
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_TABLE);
            //Toast.makeText(context, "onCreate called", Toast.LENGTH_SHORT).show();
        } catch (SQLException e){
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            //Toast.makeText(context, "onUpgrade called", Toast.LENGTH_SHORT).show();
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e){
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStringFroDate(Date date){
        if(date == null)
            return  null;
        return sdf.format(date);
    }

    private  Date getDateFromString(String string){
        try {
            return sdf.parse(string);
        } catch (ParseException | NullPointerException e){
            return null;
        }
    }
}
