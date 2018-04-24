package com.example.onpus.weddingpanda.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.example.onpus.weddingpanda.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.onpus.weddingpanda.R.color.common_google_signin_btn_text_light_pressed;

/**
 * Created by onpus on 2018/2/2.
 */

public class SearchListAdapter extends BaseAdapter  {

    private ArrayList<SearchActivity.User> filteredList;
    private Context mContext;
    private String isWaiting;
    private String type;


    public SearchListAdapter(Context mContext, ArrayList<SearchActivity.User> friendList, String isWaiting, String type) {
        this.mContext = mContext;
        this.filteredList = friendList;
        this.isWaiting = isWaiting;
        this.type = type;

    }


    @Override
    public int getCount() {
        return filteredList.size();
    }


    @Override
    public SearchActivity.User getItem(int i) {
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
        final SearchActivity.User user = getItem(position);

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
        if(isWaiting.equals("waiting")||isWaiting.equals("search")){
            holder.invitebtn.setText("INVITE");
            checkInvited(user.getId(),holder.invitebtn);
            holder.invitebtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    //search from waiting and search tool

                    if (!holder.invitebtn.getText().equals("INVITED")) {
                        writeNewUser((user.getId()));
                        holder.invitebtn.setText("INVITED");
                        holder.invitebtn.setBackgroundColor(common_google_signin_btn_text_light_pressed);

                    }
                    else{
                        Toast.makeText(mContext, "You have already invited her/him!", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
        else{ //search from album
            checkAdded(user.getId(),holder.invitebtn);
            holder.invitebtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    //search from waiting and search tool
                    if(!holder.invitebtn.getText().equals("ADDED")){
                        writeNewUser((user.getId()));
                        holder.invitebtn.setText("ADDED");
                        holder.invitebtn.setBackgroundColor(common_google_signin_btn_text_light_pressed);
                    }
                    else{
                        Toast.makeText(mContext, "You have already invited her/him!", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }



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

        if (isWaiting.equals("search")){

            mDatabase.child("Users").child(userId).child("guest").child(guestid).setValue(false);
            mDatabase.child("Users").child(guestid).child("couple").child(userId).setValue(false);
        }

        //for waiting rm
        if (isWaiting.equals("waiting"))
        mDatabase.child("Games").child(userId).child("DrawCircle").child("waitingrm").child(guestid).setValue(false);

        if (isWaiting.equals("album"))
            mDatabase.child("Users").child(userId).child("Genqr").child(guestid).setValue(false);

//        if (isWaiting.equals("album")&&type.equals("guest"))
//            mDatabase.child("Games").child(userId).child("DrawCircle").child("waitingrm").child(guestid).setValue(false);

    }

    // for album search
    private void checkAdded(final String guestid, final Button btntemp) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mSearchQr = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Genqr");

        mSearchQr.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("ResourceAsColor")
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild(guestid)!==null){
                if (guestid!=null){
                    if(!dataSnapshot.hasChild(guestid)){
                        btntemp.setText("ADD");
                        btntemp.setBackgroundColor((Color.parseColor("#b1eacd")));

                    }
                    else{
                        btntemp.setText("ADDED");
                        btntemp.setBackgroundColor(common_google_signin_btn_text_light_pressed);

                    }
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

   private void checkInvited(final String guestid, final Button btntemp){
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        if (isWaiting.equals("search")) {

            mDatabase.child(userId).child("guest").addValueEventListener(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("SearchB","isNotWaiting");
//                    Boolean isfind = false;
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                        if (guestid.equals(child.getKey())) {
//                            isfind = true;
//                        } else {
//                            isfind = false;
//
//                        }
//                    }
//                        if(isfind){
//                            btntemp.setText("INVITED");
//                            btntemp.setBackgroundColor(common_google_signin_btn_text_light_pressed);
//
//                        }else{
//                            btntemp.setText("INVITED");
//                            btntemp.setBackgroundColor(R.color.colorBg);
//                        }
//

                    if (guestid!=null){
                        if(!dataSnapshot.hasChild(guestid)){
                            btntemp.setText("INVITE");
                            btntemp.setBackgroundColor((Color.parseColor("#b1eacd")));

                        }
                        else{
                            btntemp.setText("INVITED");
                            btntemp.setBackgroundColor(common_google_signin_btn_text_light_pressed);

                        }
                    }
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
//                    if (dataSnapshot.exists()) {
//                        // dataSnapshot is the "issue" node with all children with id 0
//                        btntemp.setText("INVITED");
//                        btntemp.setBackgroundColor(common_google_signin_btn_text_light_pressed);
//
//                        Log.d("SearchA","isWaiting");
//
//                    }else{
//
//                    }


                    if (guestid!=null){
                        if(!dataSnapshot.hasChild(guestid)){
                            btntemp.setText("INVITE");
                            btntemp.setBackgroundColor((Color.parseColor("#b1eacd")));

                        }
                        else{
                            btntemp.setText("INVITED");
                            btntemp.setBackgroundColor(common_google_signin_btn_text_light_pressed);

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}


