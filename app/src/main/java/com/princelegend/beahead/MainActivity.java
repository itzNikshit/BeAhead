package com.princelegend.beahead;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // white bg var
    ImageView whiteBg;
    // Floating buttons vars
    public FloatingActionButton addFab, addTasksFab, addNormalFab, addOnlineFab;
    // animation vars
    Animation rotateOpen,rotateClose,fromBottom,toBottom;
    // TextViews vars
    public TextView addTaskText, addNormalText, addOnlineText;
    // Button clicked var
    public Boolean isAllFabsVisible;
    // PopUp vars and buttons
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    public Button saveBtn, cancelBtn;
    // Text Field vars for add Task
    public EditText taskName, taskDate, taskTime;
    // Text Field vars for normal event
    public EditText normalEventName, normalEventDate, normalEventTime, normalEventDes;
    // Text Field vars for online events
    public EditText onlineEventName, onlineEventDate, onlineEventTime, onlineEventLink;
    // Vars for contact us popup
    public EditText reporterName,reporterMail, reporterPhone, reporterMsg;
    public Button submitReportBtn;
    // Time Pickup vars
    int hour,minute;
    // Database reference var for all events
    DatabaseReference reff;
    // Class vars for database
    Tasks tasks;
    NormalEvents normalEvents;
    OnlineEvents onlineEvents;
    // cardviews vars
    CardView dailyTasksCard,normalEventsCard,onlineEventsCard,calendarCard,contactUsCard,infoCard;
    // firestore var
    FirebaseFirestore fStore;
    // timestr string
    String timeStr;

    //Logout function
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    // fun to make timeStr accordingly
    public void setTimeStr(String time) {
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
    }

    // setAlarm fun for setting notification for tasks
    public void setAlarm(String title,String date,String time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("taskName", title);
        intent.putExtra("taskDate", date);
        intent.putExtra("taskTime", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String fullDate = date + " " + timeStr;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date userdate = sdf.parse(fullDate);
            alarmManager.set(AlarmManager.RTC_WAKEUP, userdate.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // setAlarm fun for setting notification for normal events
    public void setAlarmNormalEvents(String name, String date, String time, String des) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastNormalEvents.class);
        intent.putExtra("nEventName", name);
        intent.putExtra("nEventDate", date);
        intent.putExtra("nEventTime", time);
        intent.putExtra("nEventDes", des);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String fullDate = date + " " + timeStr;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date userdate = sdf.parse(fullDate);
            alarmManager.set(AlarmManager.RTC_WAKEUP, userdate.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // setAlarm fun for setting notification for normal events
    public void setAlarmOnlineEvents(String name, String date, String time, String link) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastOnlineEvents.class);
        intent.putExtra("oEventName", name);
        intent.putExtra("oEventDate", date);
        intent.putExtra("oEventTime", time);
        intent.putExtra("oEventLink", link);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String fullDate = date + " " + timeStr;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date userdate = sdf.parse(fullDate);
            alarmManager.set(AlarmManager.RTC_WAKEUP, userdate.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Create Dialog popup Function for add task
    public void addTaskDialog() {
        // Make new Dialog builder
        dialogBuilder = new AlertDialog.Builder(this);
        // Inflate view on the root for dialog box
        final View addTaskPopupView = getLayoutInflater().inflate(R.layout.activity_popup, null);
        // Using Text fields to extract details
        taskName = (EditText) addTaskPopupView.findViewById(R.id.enter_name);
        taskDate = (EditText) addTaskPopupView.findViewById(R.id.enter_date);
        taskTime = (EditText) addTaskPopupView.findViewById(R.id.enter_time);
        // Using buttons to see action
        saveBtn = (Button) addTaskPopupView.findViewById(R.id.save_button);
        cancelBtn = (Button) addTaskPopupView.findViewById(R.id.cancel_button);

        // Adding text watcher for the inputs
        taskName.addTextChangedListener(addTaskTextWatcher);
        taskDate.addTextChangedListener(addTaskTextWatcher);
        taskTime.addTextChangedListener(addTaskTextWatcher);

        // Setting new view to the dialog builder
        dialogBuilder.setView(addTaskPopupView);
        // Creating dialog from the created builder
        dialog = dialogBuilder.create();
        // Showing the dialog on screen
        dialog.show();

        // Calendar methods
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // SetOnClickListerner methods for editTexts
        taskDate.setFocusable(false);
        taskDate.setKeyListener(null);
        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String date = i2 + "/" + i1 + "/" + i;
                        taskDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        // setOnclickListener for EditText time
        taskTime.setFocusable(false);
        taskTime.setKeyListener(null);
        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        // initialize calendar
                        Calendar calendar1 = Calendar.getInstance();
                        // set day of month
                        calendar1.set(0,0,0,hour,minute);
                        // set text on view
                        taskTime.setText(DateFormat.format("hh:mm aa",calendar1));
                    }
                },12, 0, false);
                // Update previous selected time
                timePickerDialog.updateTime(hour,minute);
                // show the time picker dialog
                timePickerDialog.show();
            }
        });

        // Tasks initialization
        tasks = new Tasks();

        // SetOnClickListener methods for the buttons
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.setName(taskName.getText().toString().trim());
                tasks.setDate(taskDate.getText().toString().trim());
                tasks.setTime(taskTime.getText().toString().trim());

                // Strings for tasks details
                //for notification
                String date = tasks.getDate();
                String time = tasks.getTime();
                String title = tasks.getName();
                setTimeStr(time);
                setAlarm(title,date,time);

                String uid = FirebaseAuth.getInstance().getUid();
                reff.child(uid).child("Tasks").child("upcoming").push().setValue(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "An error occurred! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    // Create function for normal event popup
    public void normalEventDialog() {
        // Make new Dialog builder
        dialogBuilder = new AlertDialog.Builder(this);
        // Inflate view on the root for dialog box
        final View addTaskPopupView = getLayoutInflater().inflate(R.layout.popup_normal_event, null);
        // Using Text fields to extract details
        normalEventName = (EditText) addTaskPopupView.findViewById(R.id.normal_event_enter_name);
        normalEventDate = (EditText) addTaskPopupView.findViewById(R.id.normal_event_enter_date);
        normalEventTime = (EditText) addTaskPopupView.findViewById(R.id.normal_event_enter_time);
        normalEventDes = (EditText) addTaskPopupView.findViewById(R.id.normal_event_enter_des);
        // Using buttons to see action
        saveBtn = (Button) addTaskPopupView.findViewById(R.id.normal_event_save_button);
        cancelBtn = (Button) addTaskPopupView.findViewById(R.id.normal_event_cancel_button);

        // Adding text watcher for the inputs
        normalEventName.addTextChangedListener(normalEventTextWatcher);
        normalEventDate.addTextChangedListener(normalEventTextWatcher);
        normalEventTime.addTextChangedListener(normalEventTextWatcher);

        // Setting new view to the dialog builder
        dialogBuilder.setView(addTaskPopupView);
        // Creating dialog from the created builder
        dialog = dialogBuilder.create();
        // Showing the dialog on screen
        dialog.show();

        // Calendar methods
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // SetOnClickListerner methods for editTexts
        normalEventDate.setFocusable(false);
        normalEventDate.setKeyListener(null);
        normalEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String date = i2 + "/" + i1 + "/" + i;
                        normalEventDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        // setOnclickListener for EditText time
        normalEventTime.setFocusable(false);
        normalEventTime.setKeyListener(null);
        normalEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        // initialize calendar
                        Calendar calendar1 = Calendar.getInstance();
                        // set hour and minute
                        calendar1.set(0,0,0,hour,minute);
                        // setText
                        normalEventTime.setText(DateFormat.format("hh:mm aa",calendar1));
                    }
                },12, 0, false);
                // Update previous selected time
                timePickerDialog.updateTime(hour,minute);
                // show the time picker dialog
                timePickerDialog.show();
            }
        });

        // normalEvents initialization
        normalEvents = new NormalEvents();

        // SetOnClickListener methods for the buttons
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalEvents.setEventName(normalEventName.getText().toString().trim());
                normalEvents.setEventDate(normalEventDate.getText().toString().trim());
                normalEvents.setEventTime(normalEventTime.getText().toString().trim());
                normalEvents.setEventDes(normalEventDes.getText().toString().trim());

                String name = normalEvents.getEventName();
                String date = normalEvents.getEventDate();
                String time = normalEvents.getEventTime();
                String des = normalEvents.getEventDes();

                setTimeStr(time);
                setAlarmNormalEvents(name,date,time,des);

                String uid = FirebaseAuth.getInstance().getUid();
                reff.child(uid).child("nEvents").child("upcoming").push().setValue(normalEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Event details saved successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "An error occurred! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    // Create popup function for online Event
    public void onlineEventDialog() {
        // Make new Dialog builder
        dialogBuilder = new AlertDialog.Builder(this);
        // Inflate view on the root for dialog box
        final View addTaskPopupView = getLayoutInflater().inflate(R.layout.popup_online_event, null);
        // Using Text fields to extract details
        onlineEventName = (EditText) addTaskPopupView.findViewById(R.id.online_event_enter_name);
        onlineEventDate = (EditText) addTaskPopupView.findViewById(R.id.online_event_enter_date);
        onlineEventTime = (EditText) addTaskPopupView.findViewById(R.id.online_event_enter_time);
        onlineEventLink = (EditText) addTaskPopupView.findViewById(R.id.online_event_enter_link);
        // Using buttons to see action
        saveBtn = (Button) addTaskPopupView.findViewById(R.id.online_event_save_button);
        cancelBtn = (Button) addTaskPopupView.findViewById(R.id.online_event_cancel_button);

        // Adding text watcher for the inputs
        onlineEventName.addTextChangedListener(onlineEventTextWatcher);
        onlineEventDate.addTextChangedListener(onlineEventTextWatcher);
        onlineEventTime.addTextChangedListener(onlineEventTextWatcher);
        onlineEventLink.addTextChangedListener(onlineEventTextWatcher);

        // Setting new view to the dialog builder
        dialogBuilder.setView(addTaskPopupView);
        // Creating dialog from the created builder
        dialog = dialogBuilder.create();
        // Showing the dialog on screen
        dialog.show();

        // Calendar methods
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // SetOnClickListerner methods for editTexts
        onlineEventDate.setFocusable(false);
        onlineEventDate.setKeyListener(null);
        onlineEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String date = i2 + "/" + i1 + "/" + i;
                        onlineEventDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });

        // setOnclickListener for EditText time
        onlineEventTime.setFocusable(false);
        onlineEventTime.setKeyListener(null);
        onlineEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        // initialize calendar
                        Calendar calendar1 = Calendar.getInstance();
                        // set hour and minute
                        calendar1.set(0,0,0,hour,minute);
                        // setText
                        onlineEventTime.setText(DateFormat.format("hh:mm aa",calendar1));
                    }
                },12, 0, false);
                // Update previous selected time
                timePickerDialog.updateTime(hour,minute);
                // show the time picker dialog
                timePickerDialog.show();
            }
        });

        // onlineEvents initialization
        onlineEvents = new OnlineEvents();

        // SetOnClickListener methods for the buttons
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineEvents.setOnlineName(onlineEventName.getText().toString().trim());
                onlineEvents.setOnlineDate(onlineEventDate.getText().toString().trim());
                onlineEvents.setOnlineTime(onlineEventTime.getText().toString().trim());
                onlineEvents.setOnlineLink(onlineEventLink.getText().toString().trim());

                String name = onlineEvents.getOnlineName();
                String date = onlineEvents.getOnlineDate();
                String time = onlineEvents.getOnlineTime();
                String link = onlineEvents.getOnlineLink();

                setTimeStr(time);
                setAlarmOnlineEvents(name,date,time,link);

                String uid = FirebaseAuth.getInstance().getUid();
                reff.child(uid).child("oEvents").child("upcoming").push().setValue(onlineEvents).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Event saved successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "An error occurred! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    // TextWatcher Functions
    public TextWatcher addTaskTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // converting text by user to string
            String nameInput = taskName.getText().toString().trim();
            String dateInput = taskDate.getText().toString().trim();
            String timeInput = taskTime.getText().toString().trim();

            // Checking if button should be enabled or not
            saveBtn.setEnabled(!nameInput.isEmpty() && !dateInput.isEmpty() && !timeInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public TextWatcher normalEventTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // converting text by user to string
            String nameInput = normalEventName.getText().toString().trim();
            String dateInput = normalEventDate.getText().toString().trim();
            String timeInput = normalEventTime.getText().toString().trim();

            // Checking if button should be enabled or not
            saveBtn.setEnabled(!nameInput.isEmpty() && !dateInput.isEmpty() && !timeInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public TextWatcher onlineEventTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // converting text by user to string
            String nameInput = onlineEventName.getText().toString().trim();
            String dateInput = onlineEventDate.getText().toString().trim();
            String timeInput = onlineEventTime.getText().toString().trim();
            String linkInput = onlineEventLink.getText().toString().trim();

            // Checking if button should be enabled or not
            saveBtn.setEnabled(!nameInput.isEmpty() && !dateInput.isEmpty() && !timeInput.isEmpty() && !linkInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialzing firestore var
        fStore = FirebaseFirestore.getInstance();

        //Initialization of database
        reff = FirebaseDatabase.getInstance().getReference().child("users");

        // white bg var
        whiteBg = (ImageView) findViewById(R.id.whiteBg);
        // Register all the FABs with their IDs
        // This FAB button is the Parent
        addFab = findViewById(R.id.add_fab);
        // FAB buttons
        addTasksFab = findViewById(R.id.add_tasks_fab);
        addNormalFab = findViewById(R.id.add_normal_event_fab);
        addOnlineFab = findViewById(R.id.add_online_event_fab);
        // loading animations
        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        // Also register the action name text, of all the FABs.
        addTaskText = findViewById(R.id.add_tasks_action_text);
        addNormalText = findViewById(R.id.add_normal_action_text);
        addOnlineText = findViewById(R.id.add_online_action_text);
        //Finding each card with id
        dailyTasksCard = (CardView) findViewById(R.id.dailyTaskCard);
        normalEventsCard = (CardView) findViewById(R.id.normalEventCard);
        onlineEventsCard = (CardView) findViewById(R.id.onlineEventCard);
        calendarCard = (CardView) findViewById(R.id.calendarCard);
        contactUsCard = (CardView) findViewById(R.id.contactUsCard);
        infoCard = (CardView) findViewById(R.id.infoCard);

        // Now set all the FABs and all the action name
        // texts as GONE
        addTasksFab.setVisibility(View.GONE);
        addNormalFab.setVisibility(View.GONE);
        addOnlineFab.setVisibility(View.GONE);
        addTaskText.setVisibility(View.GONE);
        addNormalText.setVisibility(View.GONE);
        addOnlineText.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabsVisible) {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    whiteBg.setVisibility(View.VISIBLE);
                    addFab.startAnimation(rotateOpen);
                    addTasksFab.setVisibility(View.VISIBLE);
                    addNormalFab.setVisibility(View.VISIBLE);
                    addOnlineFab.setVisibility(View.VISIBLE);
                    addTasksFab.startAnimation(fromBottom);
                    addNormalFab.startAnimation(fromBottom);
                    addOnlineFab.startAnimation(fromBottom);
                    addTasksFab.setClickable(true);
                    addNormalFab.setClickable(true);
                    addOnlineFab.setClickable(true);
                    addTaskText.setVisibility(View.VISIBLE);
                    addNormalText.setVisibility(View.VISIBLE);
                    addOnlineText.setVisibility(View.VISIBLE);

                    // gridLayout clickability to false
                    dailyTasksCard.setClickable(false);
                    normalEventsCard.setClickable(false);
                    onlineEventsCard.setClickable(false);
                    calendarCard.setClickable(false);
                    contactUsCard.setClickable(false);
                    infoCard.setClickable(false);

                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = true;
                } else {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs GONE.
                    whiteBg.setVisibility(View.INVISIBLE);
                    addFab.startAnimation(rotateClose);
                    addTasksFab.setVisibility(View.INVISIBLE);
                    addNormalFab.setVisibility(View.INVISIBLE);
                    addOnlineFab.setVisibility(View.INVISIBLE);
                    addTasksFab.startAnimation(toBottom);
                    addNormalFab.startAnimation(toBottom);
                    addOnlineFab.startAnimation(toBottom);
                    addTasksFab.setClickable(false);
                    addNormalFab.setClickable(false);
                    addOnlineFab.setClickable(false);
                    addTaskText.setVisibility(View.GONE);
                    addNormalText.setVisibility(View.GONE);
                    addOnlineText.setVisibility(View.GONE);

                    // gridLayout clickability to true
                    dailyTasksCard.setClickable(true);
                    normalEventsCard.setClickable(true);
                    onlineEventsCard.setClickable(true);
                    calendarCard.setClickable(true);
                    contactUsCard.setClickable(true);
                    infoCard.setClickable(true);

                    // make the boolean variable false
                    // as we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = false;
                }
            }
        });

        // below is the action to handle add tasks
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        addTasksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add Task", Toast.LENGTH_SHORT).show();
                addTaskDialog();
            }
        });

        // below is the action to handle add tasks
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        addNormalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add Normal Event", Toast.LENGTH_SHORT).show();
                normalEventDialog();
            }
        });

        // below is the action to handle add tasks
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        addOnlineFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add Online Event", Toast.LENGTH_SHORT).show();
                onlineEventDialog();
            }
        });

        // cardviews set on click listeners
        dailyTasksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TasksActivity.class));
            }
        });

        normalEventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NormalEventsActivity.class));
            }
        });

        onlineEventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OnlineEventsActivity.class));
            }
        });

        calendarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CalActivity.class));
            }
        });

        contactUsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final View contactUsPopupView = getLayoutInflater().inflate(R.layout.contact_us_popup,null);
                reporterName = (EditText) contactUsPopupView.findViewById(R.id.contactName);
                reporterMail = (EditText) contactUsPopupView.findViewById(R.id.contactMail);
                reporterPhone = (EditText) contactUsPopupView.findViewById(R.id.contactPhone);
                reporterMsg = (EditText) contactUsPopupView.findViewById(R.id.contactMsg);
                submitReportBtn = (Button) contactUsPopupView.findViewById(R.id.contactBtn);

                // Setting new view to the dialog builder
                dialogBuilder.setView(contactUsPopupView);
                // Creating dialog from the created builder
                dialog = dialogBuilder.create();
                // Showing the dialog on screen
                dialog.show();

                submitReportBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = reporterName.getText().toString().trim();
                        String mail = reporterMail.getText().toString().trim();
                        String phone = reporterPhone.getText().toString().trim();
                        String msg = reporterMsg.getText().toString().trim();
                        String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        if(TextUtils.isEmpty(name)) {
                            reporterName.setError("Name is required!");
                            return;
                        }
                        if(TextUtils.isEmpty(phone)) {
                            reporterName.setError("Phone number is required!");
                            return;
                        }
                        if(TextUtils.isEmpty(mail)) {
                            reporterName.setError("Mail address is required!");
                            return;
                        }
                        if(TextUtils.isEmpty(msg)) {
                            reporterName.setError("Message is required!");
                            return;
                        }
                        if(!currentMail.equals(mail)) {
                            Toast.makeText(MainActivity.this,currentMail,Toast.LENGTH_LONG).show();
                            reporterMail.setError("User mail is not same as entered mail!");
                            return;
                        }

                        DocumentReference documentReference = fStore.collection("feedbacks").document("reports");
                        Map<String,Object> report = new HashMap<>();
                        report.put("fullName",name);
                        report.put("mail",mail);
                        report.put("phoneNo",phone);
                        report.put("message",msg);
                        documentReference.set(report);

                        Toast.makeText(MainActivity.this,"Report submitted successfully. We will contact you shortly.",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

            }
        });

        infoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Info card clicked!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item1:
                startActivity(new Intent(getApplicationContext(),Profile.class));
                return true;

            case R.id.item2:
                Toast.makeText(MainActivity.this,"Signed out!",Toast.LENGTH_SHORT).show();
                logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}