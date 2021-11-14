package com.princelegend.beahead;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpcomingOnlineEventsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference reff;
    OnlineEventsAdapter onlineEventsAdapter;
    ArrayList<OnlineEvents> list;
    FirebaseAuth fAuth;
    String uid;
    EditText searchBar;
    CharSequence search = "";

    public UpcomingOnlineEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_online_events, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewOnlineEvents);
        searchBar = (EditText) view.findViewById(R.id.searchBarOnlineEvents);
        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();
        reff = FirebaseDatabase.getInstance().getReference("users").child(uid).child("oEvents").child("upcoming");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        onlineEventsAdapter = new OnlineEventsAdapter(getContext(),list);
        recyclerView.setAdapter(onlineEventsAdapter);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OnlineEvents onlineEvents = dataSnapshot.getValue(OnlineEvents.class);
                    list.add(onlineEvents);
                }
                onlineEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onlineEventsAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}