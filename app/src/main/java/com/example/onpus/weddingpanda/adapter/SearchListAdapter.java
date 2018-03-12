package com.example.onpus.weddingpanda.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by onpus on 2018/2/2.
 */

public class SearchListAdapter extends BaseAdapter  {

    private ArrayList<String> guestList;
    private ArrayList<User> filteredList;
    private Context mContext;
    private String isWaiting;


    public SearchListAdapter(Context mContext, ArrayList<User> friendList,ArrayList<String> guestList,String isWaiting) {
        this.mContext = mContext;
        this.guestList = guestList;
        this.filteredList = friendList;
        this.isWaiting = isWaiting;

    }


    @Override
    public int getCount() {
        return filteredList.size();
    }


    @Override
    public Object getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        final ViewHolder holder;
        final User user = (User) getItem(position);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = LayoutInflater.from(mContext).inflate(R.layout.list_row_guestlist_search, parent, false);
            holder = new ViewHolder();
            holder.invitebtn = (Button) view.findViewById(R.id.invitebtn);
            holder.emailGuest = (TextView) view.findViewById(R.id.GuestEmail);
            holder.name = (TextView) view.findViewById(R.id.nameGuest);


            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind text with view holder content view for efficient use
        holder.emailGuest.setText(user.getEmail());
        holder.name.setText(user.getName());


        //check btn if that guy invited
        holder.emailGuest.setText("INVITE");
        checkInvited(guestList.get(position),holder.invitebtn);


        holder.invitebtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (!holder.invitebtn.getText().equals("INVITED")) {
                    writeNewUser(guestList.get(position));
                    holder.invitebtn.setText("INVITED");
                    holder.invitebtn.setBackgroundColor(R.color.common_google_signin_btn_text_light_pressed);
                }
                else{
                    Toast.makeText(mContext, "You have already invited her/him!", Toast.LENGTH_SHORT).show();

                }
            }
        });
//        view.setBackgroundResource(R.drawable.friend_list_selector);

        return view;
    }

    private void checkInvited(String s) {
    }


    static class ViewHolder {
        TextView emailGuest;
        TextView name;
        Button invitebtn;
    }

    private void writeNewUser(String guestid){
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(userId).child("guest").child(guestid).setValue(false);
        mDatabase.child("Users").child(guestid).child("couple").child(userId).setValue(false);
        //for waiting rm
        if (isWaiting.equals("waiting"))
        mDatabase.child("Games").child(userId).child("DrawCircle").child("waitingrm").child(guestid).setValue(true);
    }

   private void checkInvited(final String guestid, final Button btntemp){
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (isWaiting.equals("none")) {

            mDatabase.child(userId).child("guest").addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("SearchB","isNotWaiting");

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (guestid.equals(child.getKey())) {
                            btntemp.setText("INVITED");
                            btntemp.setBackgroundColor(R.color.common_google_signin_btn_text_light_pressed);
                            break;
                        }

                    }
                    btntemp.setText("INVITE");
                    btntemp.setBackgroundColor(R.color.colorBg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            //if it is in waiting room
            Query query = ref.child("Games").child(userId).child("DrawCircle").child("waitingrm").orderByChild(guestid).equalTo(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        btntemp.setText("INVITED");
                        btntemp.setBackgroundColor(R.color.common_google_signin_btn_text_light_pressed);

                        Log.d("SearchA","isWaiting");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}


