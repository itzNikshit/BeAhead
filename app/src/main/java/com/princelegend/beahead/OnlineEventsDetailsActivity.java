package com.princelegend.beahead;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

public class OnlineEventsDetailsActivity extends AppCompatActivity {

    String name,date,time,link;
    Date userDate;
    SimpleDateFormat sdf1,sdf2;
    TextView titleTv,dateTv,dayTv,timeTv,linkTv;
    CountdownView countdownView;
    String dayToShow,timeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_events_details);

        titleTv = (TextView) findViewById(R.id.onlineEventDetailsName);
        dateTv = (TextView) findViewById(R.id.onlineEventDetailsDate);
        dayTv = (TextView) findViewById(R.id.onlineEventDetailsDay);
        timeTv = (TextView) findViewById(R.id.onlineEventDetailsTime);
        linkTv = (TextView) findViewById(R.id.onlineEventDetailsLink);
        countdownView = (CountdownView) findViewById(R.id.countdownViewOnlineEventsDetails);

        sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf2 = new SimpleDateFormat("dd/MM/yyyy");

        name = getIntent().getStringExtra("oEventName");
        date = getIntent().getStringExtra("oEventDate");
        time = getIntent().getStringExtra("oEventTime");
        link = getIntent().getStringExtra("oEventLink");

        timeTv.setText(time);
        titleTv.setText(name);
        try {
            userDate = sdf2.parse(date);
            dayToShow = DateFormat.getDateInstance(DateFormat.FULL).format(userDate);
            String[] splitDate = dayToShow.split(",");
            dayTv.setText(splitDate[0].trim());
            dateTv.setText(splitDate[1].trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        linkTv.setText(link);

        if((time.charAt(6)=='P' || time.charAt(6)=='p') && !time.substring(0,2).equals("12")){
            int a=time.charAt(0)-48;
            int b=time.charAt(1)-48;
            int total=(a*10+b)+12;
            timeStr = total + time.substring(2,5) + ":00";
        }
        if((time.charAt(6)=='P' || time.charAt(6)=='p') && time.substring(0,2).equals("12")){
            timeStr = time.substring(0,5)+":00";
        }
        if((time.charAt(6)=='A' || time.charAt(6)=='a') && time.substring(0,2).equals("12")){
            String total = "00";
            timeStr = total + time.substring(2,5)+ ":00";
        }
        if((time.charAt(6)=='A' || time.charAt(6)=='a') && !time.substring(0,2).equals("12")){
            timeStr = time.substring(0,5)+":00";
        }

        String countDate = date + " " + timeStr;
        Date now = new Date();
        try {
            Date date1 = sdf1.parse(countDate);
            Long currentTime = now.getTime();
            Long newDate = date1.getTime();
            Long countDownTime = newDate - currentTime;
            countdownView.start(countDownTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        linkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });

    }
}