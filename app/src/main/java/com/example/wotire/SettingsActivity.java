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

public class SettingsActivity extends AppCompatActivity {

    EditText etnRate;
    Button bSave;

    String payRateString;
    Double payRate;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        initWidgets();
    }

    private void initWidgets() {
        etnRate = findViewById(R.id.etnRate);
        payRateString = mPreferences.getString("pay", "0");
        payRate = Double.parseDouble(payRateString);

        etnRate.setText(payRate.toString());

        bSave = findViewById(R.id.bSaveSettings);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payRateString = etnRate.getText().toString();

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("pay", payRateString);
                editor.apply();
                editor.commit();

                finish();
            }
        });
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
                intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.scSettings:
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}