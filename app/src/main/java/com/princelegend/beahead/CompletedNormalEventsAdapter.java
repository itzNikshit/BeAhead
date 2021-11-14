package com.princelegend.beahead;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CompletedNormalEventsAdapter extends RecyclerView.Adapter<CompletedNormalEventsAdapter.MyViewHolder> {

    Context context;
    ArrayList<NormalEvents> list;
    ArrayList<NormalEvents> filteredList;
    boolean isEnabled = false;
    boolean isSelectAll = false;
    ArrayList<NormalEvents> selectedList = new ArrayList<>();

    public CompletedNormalEventsAdapter(Context context, ArrayList<NormalEvents> list) {
        this.context = context;
        this.list = list;
        this.filteredList = list;
    }

    private void clickItem(MyViewHolder holder) {
        // get selected item
        NormalEvents normalEvents = filteredList.get(holder.getAdapterPosition());
        // check condition
        if(holder.checkImg.getVisibility() == View.INVISIBLE) {
            // when item not selected
            // visible check box image
            holder.checkImg.setVisibility(View.VISIBLE);
            // set bg color
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            // add value in select array list
            selectedList.add(normalEvents);
        } else {
            // when item selected
            // hide check box image
            holder.checkImg.setVisibility(View.INVISIBLE);
            // set background
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            // remove value from select array list
            selectedList.remove(normalEvents);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NormalEvents normalEvents = filteredList.get(position);
        holder.taskName.setText(normalEvents.getEventName());
        // item click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnabled) {
                    clickItem(holder);
                } else {
                    Intent intent = new Intent(context,NormalEventsDetailsActivity.class);
                    intent.putExtra("nEventName",filteredList.get(position).getEventName());
                    intent.putExtra("nEventDate",filteredList.get(position).getEventDate());
                    intent.putExtra("nEventTime",filteredList.get(position).getEventTime());
                    intent.putExtra("nEventDes",filteredList.get(position).getEventDes());
                    context.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // making some variables
                DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // check condition
                if(!isEnabled) {
                    // when action mode is not enabled
                    // initialize action mode
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            // initialize menu inflater
                            MenuInflater inflater = actionMode.getMenuInflater();
                            // inflate menu
                            inflater.inflate(R.menu.menu_list,menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            // when action mode is prepared
                            // set isEnable true
                            isEnabled = true;
                            // create method
                            clickItem(holder);
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            // when click on action mode item
                            // get item id
                            int id = menuItem.getItemId();
                            switch (id) {
                                case R.id.menu_delete:
                                    ArrayList<NormalEvents> deleteList = new ArrayList<>();
                                    deleteList.addAll(selectedList);
                                    // create dialog asking to delete
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Do you want to delete the selected items?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            for(NormalEvents n : deleteList) {
                                                // delete from database
                                                Query query = reff.child("users").child(uid).child("nEvents").child("completed").orderByChild("eventName").equalTo(n.getEventName());
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            dataSnapshot.getRef().removeValue();
                                                        }
                                                        filteredList.remove(n);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                });
                                            }
                                            notifyDataSetChanged();
                                            deleteList.clear();
                                            dialogInterface.dismiss();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    actionMode.finish();
                                    break;

                                case R.id.menu_select_all:
                                    if(selectedList.size() == filteredList.size()) {
                                        // when all item selected
                                        // set isSelectAll false
                                        isSelectAll = false;
                                        // clear select all list
                                        selectedList.clear();
                                    } else {
                                        // when all item unselected
                                        // set isSelectALl true
                                        isSelectAll = true;
                                        // clear select all list
                                        selectedList.clear();
                                        // add all values in selected list
                                        selectedList.addAll(filteredList);
                                    }
                                    // notify adapter
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            // when action mdoe is destroyed
                            // set isEnable false
                            isEnabled = false;
                            isSelectAll = false;
                            // clear select array list
                            selectedList.clear();
                            // notify adapter
                            notifyDataSetChanged();
                        }
                    };
                    // start action mode
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    // when action mode is already enabled
                    // call method
                    clickItem(holder);
                }
                return true;
            }
        });

        if(isSelectAll) {
            // when all value selected
            // visible all check box image
            holder.checkImg.setVisibility(View.VISIBLE);
            // set bg color
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            // when all value unselected
            // invisible all check box image
            holder.checkImg.setVisibility(View.INVISIBLE);
            // set bg color
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        ImageView checkImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = (TextView) itemView.findViewById(R.id.itemTask);
            checkImg  = (ImageView) itemView.findViewById(R.id.checkCircle);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if(key.isEmpty()) {
                    filteredList = list;
                } else {
                    ArrayList<NormalEvents> listFiltered = new ArrayList<>();
                    for(NormalEvents row : list) {
                        if(row.getEventName().toLowerCase().contains(key.toLowerCase())) {
                            listFiltered.add(row);
                        }
                    }
                    filteredList = listFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<NormalEvents>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
