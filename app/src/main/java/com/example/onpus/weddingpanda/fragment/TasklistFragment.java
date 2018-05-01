package com.example.onpus.weddingpanda.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.TaskListActivity;
import com.example.onpus.weddingpanda.adapter.BudgetExpandableListAdapter;
import com.example.onpus.weddingpanda.adapter.TasklistepandlistAdapter;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.Budget;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TasklistFragment extends Fragment {
    @BindView(R.id.material_design_android_floating_action_menu2)
    FloatingActionMenu buttonmenu;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;


    public TasklistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        final List<String> listDataHeader = new ArrayList<>();
        final HashMap<String, List<TaskListActivity.newTaskItem>> listDataChild = new HashMap<>();

        View view = inflater.inflate(R.layout.fragment_tasklist, container, false);
        ButterKnife.bind(this,view);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExpTask);
        prepareListData(listDataHeader,listDataChild);
        return view;
    }

    @OnClick({R.id.ite1_addlistTitle,R.id.item2_btn_addlistitem})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ite1_addlistTitle:
                addTitle();
                break;
            case R.id.item2_btn_addlistitem:
                startActivity(new Intent(getActivity(), TaskListActivity.class));
                break;

        }
    }
    private void prepareListData(final List<String> listDataHeader, final HashMap<String, List<TaskListActivity.newTaskItem>> listDataChild) {

        final HashMap<String, String> headermap = new HashMap<>();
//        final List< Budget.ItemCategory> temp = new ArrayList<>();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks").child(currentUser.getUid());


        //add header
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //ADDED ON 6/4/2017 ALICE
                if (listDataHeader != null)
                    listDataHeader.clear();
                if (listDataChild != null)
                    listDataChild.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    final String tempValue = child.child("TaskTitle").getValue(String.class);
                    listDataHeader.add(tempValue);
                    headermap.put(tempValue, child.getKey());
//                        Map<String, List<Budget.ItemCategory>> td = (Map<String, List<Budget.ItemCategory>>) child.child("Items").getValue();
//                        Budget.ItemCategory item = td.values();

//                        Log.d("key", String.valueOf(tddd.));

                    FirebaseDatabase.getInstance().getReference().child("Tasks").child(currentUser.getUid()).child(child.getKey()).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            final List<TaskListActivity.newTaskItem> temp = new ArrayList<>();
                            //ADDED ON 6/4/2017 ALICE
                            for (DataSnapshot child : snapshot.getChildren()) {
                                try {

//                                    Log.d("panda2222222", child.child("title").getValue(String.class));
                                    TaskListActivity.newTaskItem tempValueItem = new TaskListActivity.newTaskItem();
                                    tempValueItem.setItemTitle(child.child("itemTitle").getValue(String.class));
                                    tempValueItem.setDate(child.child("date").getValue(String.class));
                                    tempValueItem.setItemid(child.child("itemid").getValue(String.class));

                                    temp.add(tempValueItem);
                                } catch (DatabaseException e) {

                                }

                            }
                            listDataChild.put(tempValue, temp); // 標題, 內容

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                // setting list adapter

                listAdapter = new TasklistepandlistAdapter(getActivity(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addTitle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add task title");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialoglayout, (ViewGroup) getView(), false);
        // Set up the input

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String m_Text = input.getText().toString();
                newTask(m_Text);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog d = builder.show();
        int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        int textViewId = d.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) d.findViewById(textViewId);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void newTask(String m_text) {
            //add task
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference newItem=ref.child("Tasks").child(currentUser.getUid()).push();
            final String[] pushkey = {""};

//        pushkey[0] = newAlbum.getKey();
            //add item to database

        pushkey[0] = newItem.getKey();

        //add album to database
//        newItem.setValue(new TaskListActivity.newTask(pushkey[0],m_text));
            newItem.child("TaskTitle").setValue(m_text);
            newItem.child("Taskid").setValue(m_text);

        }

}
