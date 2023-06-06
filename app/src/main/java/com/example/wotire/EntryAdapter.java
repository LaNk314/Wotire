package com.example.wotire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Formatter;
import java.util.List;

public class EntryAdapter extends ArrayAdapter<Entry>   {
    public EntryAdapter(Context context, List<Entry> entries){
        super(context,0, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Entry entry = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_cell, parent, false);

        TextView date = convertView.findViewById(R.id.tvDate);
        TextView hourIn = convertView.findViewById(R.id.tvStartingHour);
        TextView hourOut = convertView.findViewById(R.id.tvEndingHour);
        TextView hoursTotal = convertView.findViewById(R.id.tvHoursTotal);
        ImageView role = convertView.findViewById(R.id.ivRole);

        Formatter hours  = new Formatter();
        hours.format("%.2f", entry.getHoursOfWork());

        date.setText(entry.acquireDate(entry.getStartTime()));
        hourIn.setText(entry.acquireHour(entry.getStartTime()));
        hourOut.setText(entry.acquireHour(entry.getEndTime()));
        hoursTotal.setText(hours.toString());

        switch (entry.getRole()) {
            case "P":
                role.setImageResource(R.drawable.cookinghat1);
                break;
            case "D":
                role.setImageResource(R.drawable.scooter);
        }
        return convertView;

    }






//    @Override
//    public void onClick(View v) {
//        Toast.makeText(getContext(), ""+v.toString(), Toast.LENGTH_SHORT).show();
//    }
}
