package com.example.wotire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EntriesActivity extends AppCompatActivity {

    private ListView entryListView;
    FloatingActionButton fabNewEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entries);
        initWidgets();
        loadFromDBToMemory();
        setEntryAdapter();
        setOnClickListener();
        


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFromDBToMemory();
        setEntryAdapter();

    }

    private void setOnClickListener() {
        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry selectedEntry = (Entry) entryListView.getItemAtPosition(position);
                Intent editEntryIntent = new Intent(getApplicationContext(), AddEntryActivity.class);
                editEntryIntent.putExtra(Entry.ENTRY_EDIT_EXTRA, selectedEntry.getId());
                startActivity(editEntryIntent);

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
            case R.id.scEntries:
                break;
            case R.id.scHistory:
                intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.scSettings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    private void setEntryAdapter() {
        EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(), Entry.nonDeletedEntries());
        entryListView.setAdapter(entryAdapter);
    }

    private void initWidgets(){
        entryListView = findViewById(R.id.lvEntries);
        fabNewEntry = findViewById(R.id.fabNewEntry);
        fabNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
                startActivity(intent);

            }
        });
    }
    
    public void newEntry(View view){
        Intent newEntryIntent = new Intent(this, AddEntryActivity.class);
        startActivity(newEntryIntent);
    }

    private void loadFromDBToMemory(){
        clearMemory();
        DatabaseHelper databaseHelper = DatabaseHelper.instanceOfDatabase(this);
        databaseHelper.populateEntryListArray();
    }

    private void clearMemory(){
        Entry.entryArrayList.clear();
    }
}