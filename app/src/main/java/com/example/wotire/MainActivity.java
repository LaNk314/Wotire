package com.example.wotire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    TextView tvBriefing;
    TextView tvSummary;
    Button bClockInOut;

    ImageButton ibRole;

    Calendar calendar;
    ImageView ivRole;

    static boolean working;
    static String startTimeString;
    static String endTimeString;
    static String role;

    Date startTime;
    Date endTime;

    StringBuilder briefing;
    StringBuilder summary;
    static SharedPreferences mPreferences;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    DatabaseHelper databaseHelper;

    String payRateString;
    Double payRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

    }

    private void initWidgets(){
        tvBriefing = findViewById(R.id.tvBriefing);
        tvSummary = findViewById(R.id.tvSummary);
        bClockInOut = findViewById(R.id.bClockInOut);
        ibRole = findViewById(R.id.ibRole);

        bClockInOut.setOnClickListener(this);
        ibRole.setOnClickListener(this);
        bClockInOut.setOnLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("pay", "24.5");

        working = mPreferences.getBoolean("working", false);
        role = mPreferences.getString("role", "P");
        startTimeString = mPreferences.getString("startTime", null);
        endTimeString = mPreferences.getString("endTime", null);


        updatePreferencesData(working, startTimeString, endTimeString, role);

        updateRoleImage();
        updateBriefing(working, startTimeString, endTimeString);
        updateClockInOutButton();

        updateSummary();

    }

    @Override
    protected void onResume(){
        super.onResume();

        working = mPreferences.getBoolean("working", false);
        role = mPreferences.getString("role", null);
        startTimeString = mPreferences.getString("startTime", null);
        endTimeString = mPreferences.getString("endTime", null);

        updatePreferencesData(working, startTimeString, endTimeString, role);

        updateRoleImage();
        updateBriefing(working, startTimeString, endTimeString);
        updateClockInOutButton();

        updateSummary();
    }

    @Override
    public boolean onLongClick(View v) {
            Button b = (Button) v;
            int id = b.getId();
            String text = b.getText().toString();
            if (id == R.id.bClockInOut)
                    handleClockInOutButtonLong(text);
        return false;
    }

    private void handleClockInOutButtonLong(String text){
        if (text.equals("Clock in")){ //What to do if clocked in

            calendar = Calendar.getInstance();
            startTime = calendar.getTime();

            startTimeString = parseTimeToString(startTime);

            working = true;
            endTimeString = null;

            updatePreferencesData(true, startTimeString, endTimeString, role);
            updateClockInOutButton();
            updateBriefing(working, startTimeString, endTimeString);

        } else if (text.equals("Clock out")) { //What to do if clocked out

            working = false;
            startTimeString = mPreferences.getString("startTime", null);
            role = mPreferences.getString("role", null);
            updateClockInOutButton();

            calendar = Calendar.getInstance();



            startTime = parseTimeFromString(startTimeString);

            endTime = calendar.getTime();
            endTimeString = parseTimeToString(endTime);

            updateBriefing(working, startTimeString, endTimeString);
            updatePreferencesData(working, startTimeString, endTimeString, role);




            addEntry(role, startTime, endTime);
            updateSummary();


            //Toast.makeText(MainActivity.this, sdf.format(startTime) +" "+sdf.format(endTime)+"\n"+role, Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch(id){
            case R.id.ibRole:
                handleRoleButton();
                break;
            case R.id.bClockInOut:
                handleClockInOutButtonShort();
                break;
            case R.id.tvSummary:

        }
    }

    private void handleClockInOutButtonShort() {
//        if (working) Toast.makeText(MainActivity.this, "Press longer to clock out", Toast.LENGTH_SHORT).show();
//        else Toast.makeText(MainActivity.this, "Press longer to clock in", Toast.LENGTH_SHORT).show();
    }

    private void handleRoleButton() {
        if (!working) {
            switch (role) {
                case "P":
                    role = "D";
                    setRole(role);
                    updateRoleImage();
                    break;
                case "D":
                    role = "P";
                    setRole(role);
                    updateRoleImage();
                    break;
            }
            role = mPreferences.getString("role", null);
        } else Toast.makeText(MainActivity.this, "You can't change role during shift", Toast.LENGTH_SHORT).show();

    }


    public void updateSummary(){

        Formatter formatterHours = new Formatter();
        Formatter formatterPay = new Formatter();
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.setTime(now);

        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        summary = new StringBuilder();

        String monthName = new SimpleDateFormat("MMMM").format(calendar.getTime());
        DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);
        double hoursFromMonth = databaseHelper.sumHoursFromMonth(month, year);
        formatterHours.format("%.2f", hoursFromMonth);
        payRateString = mPreferences.getString("pay", "0");

        payRate = Double.parseDouble(payRateString);
        double salary = payRate * hoursFromMonth;
        formatterPay.format("%.2f", salary);



        summary.append("In ");
        summary.append(monthName);
        summary.append(" you've worked ");
        summary.append(formatterHours.toString());
        summary.append(" hours\n");
        summary.append("Your expected salary is ");
        summary.append(formatterPay.toString());
        summary.append("zł");
        summary.toString();
        tvSummary.setText(summary);

    }


    public void updateBriefing(boolean isWorking, String startTime, String endTime){
        briefing = new StringBuilder();

        if(isWorking) {
            briefing.append("You are currently at work\n\n")
                    .append("Started working at:\n\n ")
                    .append(startTime)
                    .append("\n\n\n")
                    .append("Arbeit macht frei");
            tvBriefing.setText(briefing.toString());
        } else {
            briefing.append("You are currently not working\n")
                    .append("Last time you worked: \n\nfrom: ")
                    .append(startTime)
                    .append("\nto: ")
                    .append(endTime)
                    .append("\n\nEnjoy your free time!");


            tvBriefing.setText(briefing.toString());
        }
    }

    private void setRole(String role){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("role", role);
        editor.apply();
    }

    public void updateClockInOutButton(){
        if(working){
            bClockInOut.setText("Clock out");
        } else {
            bClockInOut.setText("Clock in");
        }
    }

    public String parseTimeToString(Date timeSDF){
        return sdf.format(timeSDF);
    }

    public Date parseTimeFromString(String timeString ) {
        Date timeSDF;

        if (!(timeString == null)) {
            try {
                timeSDF = sdf.parse(timeString);
                return timeSDF;
            } catch (ParseException e) {
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    public void addEntry(String role, Date startTime, Date endTime){

        DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);

        int id = Entry.entryArrayList.size();
        Entry newEntry = new Entry(id, startTime, endTime, role);
        Entry.entryArrayList.add(newEntry);
        databaseHelper.addNewEntry(newEntry);


    }

    private void updatePreferencesData(boolean working, String starTime, String endTime, String role){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("working", working);
        editor.putString("startTime", starTime);
        editor.putString("endTime", endTime);
        editor.putString("role", role);
        editor.apply();
    }

    private void updateRoleImage(){
        switch (role){
            case "P":
                ibRole.setImageResource(R.drawable.cookinghat1);
                break;
            case "D":
                ibRole.setImageResource(R.drawable.scooter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent;
        switch (item.getItemId()){
            case R.id.scHome:
                break;
            case R.id.scEntries:
                intent = new Intent(getApplicationContext(), EntriesActivity.class);
                startActivity(intent);
                break;
            case R.id.scHistory:
                intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.scSettings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}