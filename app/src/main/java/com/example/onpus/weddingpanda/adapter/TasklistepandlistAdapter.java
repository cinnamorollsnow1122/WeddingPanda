package com.example.onpus.weddingpanda.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.TaskListActivity;
import com.example.onpus.weddingpanda.constant.Budget;
import com.example.onpus.weddingpanda.fragment.TasklistFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by alice on 2/5/2018.
 */

public class TasklistepandlistAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<TaskListActivity.newTaskItem>> _listDataChild;

    public TasklistepandlistAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<TaskListActivity.newTaskItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TaskListActivity.newTaskItem childText = (TaskListActivity.newTaskItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }



        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView actam = (TextView) convertView.findViewById(R.id.actam);
        TextView budam = (TextView) convertView.findViewById(R.id.budgetamount);
        String titlekey = "";
        actam.setText(childText.getDate());
        budam.setVisibility(View.GONE);
        title.setText(childText.getItemTitle());


//        actam.setText("Actual amount:$ "+childText.getActualAmount());
//        budam.setText("Budget amount:$ "+childText.getBudgetAm());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        TextView add = (TextView) convertView
                .findViewById(R.id.addItem);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference  ref = FirebaseDatabase.getInstance().getReference().child("Tasks").child(currentUser.getUid());

        final com.google.firebase.database.Query mQueryTask = ref.orderByChild("TaskTitle").equalTo(headerTitle);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mQueryTask.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            Intent mIntent = new Intent(view.getContext(), TaskListActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("TitleKey", key);
                            mIntent.putExtras(mBundle);
                            view.getContext().startActivity(mIntent);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }



        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
