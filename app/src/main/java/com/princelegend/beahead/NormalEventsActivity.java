package com.princelegend.beahead;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class NormalEventsActivity extends AppCompatActivity {

    //initialize bottom navigation
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_events);

        bottomNavigation = (MeowBottomNavigation) findViewById(R.id.bottomNavigNormalEvents);

        //add menu item
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.upcoming_tasks));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.bottomnavig_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.completed_tasks));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                ///initialize fragment
                Fragment fragment = null;
                //check condition
                switch (item.getId()){
                    case 1:
                        // when id is 1
                        //initialize upcoming tasks fragment
                        fragment = new UpcomingNormalEventsFragment();
                        break;
                    case 2:
                        // when id is 2
                        //initialize home fragment
                        fragment = new HomeFragment();
                        break;
                    case 3:
                        // when id is 3
                        //initialize completed tasks fragment
                        fragment = new CompletedNormalEventsFragment();
                        break;
                }
                //fn for loading fragment
                loadfragment(fragment);
            }
        });

        //set home fragment initially selected
        bottomNavigation.show(1,true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case 1:
                        Toast.makeText(NormalEventsActivity.this,"Upcoming Events!",Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        Toast.makeText(NormalEventsActivity.this,"Home!",Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        Toast.makeText(NormalEventsActivity.this,"Completed Events!",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                //display toast
                Toast.makeText(getApplicationContext(),"You reselected "+item.getId(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadfragment(Fragment fragment) {
        //replace fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutNormalEvents,fragment).commit();
    }

}