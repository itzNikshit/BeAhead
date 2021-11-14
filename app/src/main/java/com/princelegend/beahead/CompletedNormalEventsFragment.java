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

public class CompletedNormalEventsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference reff;
    CompletedNormalEventsAdapter normalEventsAdapter;
    ArrayList<NormalEvents> list;
    FirebaseAuth fAuth;
    String uid;
    EditText searchBar;
    CharSequence search = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed_normal_events, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewCompletedNormalEvents);
        searchBar = (EditText) view.findViewById(R.id.searchBarCompletedNormalEvents);
        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();
        reff = FirebaseDatabase.getInstance().getReference("users").child(uid).child("nEvents").child("completed");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        normalEventsAdapter = new CompletedNormalEventsAdapter(getContext(),list);
        recyclerView.setAdapter(normalEventsAdapter);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NormalEvents normalEvents = dataSnapshot.getValue(NormalEvents.class);
                    list.add(normalEvents);
                }
                normalEventsAdapter.notifyDataSetChanged();
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
                normalEventsAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}