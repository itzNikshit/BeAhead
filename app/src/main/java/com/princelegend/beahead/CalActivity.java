package com.princelegend.beahead;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class CalActivity extends AppCompatActivity {

    TextView currentDay, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        currentDay = (TextView) findViewById(R.id.currentDay);
        currentDate = (TextView) findViewById(R.id.currentDate);

        Calendar calendar = Calendar.getInstance();
        String currentDateStr = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String[] splitDate = currentDateStr.split(",");

        currentDay.setText(splitDate[0].trim());
        currentDate.setText(splitDate[1].trim());
    }
}