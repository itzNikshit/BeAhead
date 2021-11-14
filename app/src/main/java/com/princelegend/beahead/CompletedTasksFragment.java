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


public class CompletedTasksFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference reff;
    CompletedTasksAdapter tasksAdapter;
    ArrayList<Tasks> list;
    FirebaseAuth fAuth;
    String uid;
    EditText searchBar;
    CharSequence search = "";

    public CompletedTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewCompletedTasks);
        searchBar = (EditText) view.findViewById(R.id.searchBarCompletedTasks);

        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();
        reff = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Tasks").child("completed");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        tasksAdapter = new CompletedTasksAdapter(getContext(),list);
        recyclerView.setAdapter(tasksAdapter);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tasks tasks = dataSnapshot.getValue(Tasks.class);
                    list.add(tasks);
                }
                tasksAdapter.notifyDataSetChanged();
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
                tasksAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}