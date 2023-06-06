package com.example.wotire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEntryActivity extends AppCompatActivity implements View.OnClickListener {
    Button bSubmit;
    Button bStartDate;
    Button bStartHour;
    Button bEndDate;
    Button bEndHour;
    Button bDelete;


    ImageButton ibRole;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");

    int hour, minute, year, month, day;


    private Date startTime, endTime;
    private String role;
    private Entry selectedEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        initWidgets();
        checkForEditEntry();
    }

    private void checkForEditEntry() {
        Intent previousIntent = getIntent();

        int passEntryID = previousIntent.getIntExtra(Entry.ENTRY_EDIT_EXTRA, -1);
        selectedEntry = Entry.getEntryForID(passEntryID);

        if (selectedEntry != null) {
            // set view to entry values
            bStartDate.setText(getDateStringFromDate(selectedEntry.getStartTime()));
            bStartHour.setText(getHourStringFromDate(selectedEntry.getStartTime()));

            bEndDate.setText(getDateStringFromDate(selectedEntry.getEndTime()));
            bEndHour.setText(getHourStringFromDate(selectedEntry.getEndTime()));
            role = selectedEntry.getRole();
            updateRoleImage();

        }
        else {
            bDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void initWidgets() {
        bSubmit = findViewById(R.id.bSave);
        bSubmit.setOnClickListener(this);

        bStartDate = findViewById(R.id.bStartDate);
        bStartDate.setOnClickListener(this);

        bStartHour = findViewById(R.id.bStartTime);
        bStartHour.setOnClickListener(this);

        bEndDate = findViewById(R.id.bEndDate);
        bEndDate.setOnClickListener(this);

        bEndHour = findViewById(R.id.bEndTime);
        bEndHour.setOnClickListener(this);

        bDelete = findViewById(R.id.bDelete);
        bDelete.setOnClickListener(this);

        ibRole = findViewById(R.id.ibAddEntryRole);
        ibRole.setOnClickListener(this);
        role = "P";
        changeRole();
        updateRoleImage();
    }


    public void addEntry() {
        if (bStartDate.getText().toString().equals("Date") || bStartHour.getText().toString().equals("Time") ||
                bEndDate.getText().toString().equals("Date") || bEndHour.getText().toString().equals("Time"))
            Toast.makeText(this, "No value can be empty", Toast.LENGTH_SHORT).show();
        else {

            DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);

            String startDateString = bStartDate.getText().toString();
            String startHourString = bStartHour.getText().toString();
            String endDateString = bEndDate.getText().toString();
            String endHourString = bEndHour.getText().toString();

            startTime = getDateFromDateHour(startDateString, startHourString);
            endTime = getDateFromDateHour(endDateString, endHourString);


            if (selectedEntry == null) {
                int id = Entry.entryArrayList.size();
                Entry newEntry = new Entry(id, startTime, endTime, role);
                Entry.entryArrayList.add(newEntry);
                databaseHelper.addNewEntry(newEntry);
                finish();
            } else {
                selectedEntry.setStartTime(startTime);
                selectedEntry.setEndTime(endTime);
                selectedEntry.setRole(role);
                databaseHelper.updateEntryInDB(selectedEntry);
            }
        }
        finish();
    }


    @Override
    public void onClick(View v) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        int id = v.getId();

        switch (id) {
            case R.id.bSave:
                addEntry();
                break;
            case R.id.bStartDate:
                handleStartDateButton();
                break;
            case R.id.bStartTime:
                handleStartTimeButton();
                break;
            case R.id.bEndDate:
                handleEndDateButton();
                break;
            case R.id.bEndTime:
                handleEndTimeButton();
                break;
            case R.id.bDelete:
                handleDeleteButton();
                break;
            case R.id.ibAddEntryRole:
                changeRole();
                updateRoleImage();
                break;
        }
    }

    private void changeRole() {
        if (role.equals("P"))
            role = "D";
        else
            role = "P";
    }

    private void updateRoleImage() {
        switch (role) {
            case "P":
                ibRole.setImageResource(R.drawable.cookinghat1);
                break;
            case "D":
                ibRole.setImageResource(R.drawable.scooter);
        }
    }

        private void handleStartDateButton() {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    bStartDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayOfMonth, month+1, year));
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);
            datePickerDialog.setTitle("Select date");
            datePickerDialog.show();
        }

        private void handleStartTimeButton () {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    bStartHour.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);

            timePickerDialog.setTitle("Select time");
            timePickerDialog.show();
        }

        private void handleEndDateButton () {
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    bEndDate.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayOfMonth, month+1, year));
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);
            datePickerDialog.setTitle("Select date");
            datePickerDialog.show();
        }

        private void handleEndTimeButton () {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    bEndHour.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                }
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
            timePickerDialog.setTitle("Select time");
            timePickerDialog.show();
        }

        private void handleDeleteButton () {
            selectedEntry.setDeleted(new Date());
            DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);
            databaseHelper.updateEntryInDB(selectedEntry);
            finish();
        }

        private Date getDateFromDateHour (String date, String hour){

            String timeString = date + " " + hour;
            try {
                return sdf.parse(timeString);
            } catch (ParseException e) {
                Toast.makeText(this, "" + timeString, Toast.LENGTH_SHORT).show();
            }

            return null;

        }

        private String getDateStringFromDate (Date date){
            return sdfDate.format(date);
        }

        private String getHourStringFromDate (Date date){
            return sdfHour.format(date);
        }


    }
