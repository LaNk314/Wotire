package com.example.wotire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    StringBuilder summary;
    SharedPreferences mPreferences;

    int month;
    int year;

    NumberPicker npMonth;
    NumberPicker npYear;

    ImageButton ibPrevious;
    ImageButton ibNext;

    TextView tvSummary;
    String payRateString;
    double payRate;

    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
                                     "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Calendar calendar = Calendar.getInstance();
        calendar.getTime();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH )+1;

        initWidgets();
        updateSummary();


        npMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                month = newVal+1;

                updateSummary();
            }
        });

        npYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year = newVal;

                updateSummary();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibPrevious:
                handlePreviousButton();
                break;
            case R.id.ibNext:
                handleNextButton();
                break;
        }
        updateSummary();

    }

    private void handleNextButton() {
        if(month +1 == 13) {
            month = 1;
            year += 1;
        } else month += 1;

        npMonth.setValue(month-1);
        npYear.setValue(year);

    }

    private void handlePreviousButton() {
        if(month -1 == 0){
            month = 12;
            year -= 1;
        } else month -= 1;

        npMonth.setValue(month-1);
        npYear.setValue(year);
    }

    private void handleYearButton() {
    }

    private void handleMonthButton() {
    }

    private void initWidgets() {
        ibNext = findViewById(R.id.ibNext);
        ibNext.setOnClickListener(this);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibPrevious.setOnClickListener(this);
        tvSummary = findViewById(R.id.tvSummaryHistory);
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        npMonth = findViewById(R.id.npMonth);
        npYear = findViewById(R.id.npYear);

        npMonth.setMaxValue(MONTHS.length -1);
        npMonth.setMinValue(0);
        npMonth.setDisplayedValues(MONTHS);
        npMonth.setValue(month-1);

        npYear.setMaxValue(2099);
        npYear.setMinValue(2000);
        npYear.setValue(year);
    }

    public void updateSummary(){
        Formatter formatterHours = new Formatter();
        Formatter formatterPay = new Formatter();
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.setTime(now);


        summary = new StringBuilder();


        String monthName = new DateFormatSymbols().getMonths()[month-1];
        DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);
        double hoursFromMonth = databaseHelper.sumHoursFromMonth(month, year);
        formatterHours.format("%.2f", hoursFromMonth);
        payRateString = mPreferences.getString("pay", "0");

        payRate = Double.parseDouble(payRateString);
        double salary = payRate * hoursFromMonth;
        formatterPay.format("%.2f", salary);



        summary.append("In ");
        summary.append(monthName);
        summary.append(" ");
        summary.append(Integer.toString(year));
        summary.append(", you've worked ");
        summary.append(formatterHours.toString());
        summary.append(" hours\n");
        summary.append("Your expected salary is ");
        summary.append(formatterPay.toString());
        summary.append("zł");
        summary.toString();
        tvSummary.setText(summary);
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
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.scEntries:
                intent = new Intent(getApplicationContext(), EntriesActivity.class);
                startActivity(intent);
                break;
            case R.id.scHistory:
                break;
            case R.id.scSettings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}