package com.example.onpus.weddingpanda;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.adapter.SearchListAdapter;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity{

    private com.google.firebase.database.Query mQueryGuest;
    private com.google.firebase.database.Query mQueryGuestName;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    ListView guestListview;
    SearchListAdapter guestListAdapter;
    private ArrayList<String> guestList= new ArrayList<>();
    private ArrayList<User> guestItem = new ArrayList<>();
    String fromwh = "none" ;
    String type = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        Bundle b = getIntent().getExtras();
        if (b != null) {
            fromwh = b.getString("Waitrm");
            type = b.getString("type");
        } else Log.i("BUNDLE","Null");


        //init firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        if (!fromwh.equals("search")){
            //find the guest alread invited
            mQueryGuest = mDatabase.orderByChild("couple/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).equalTo(false);
        }
        else{// from simple search
            mQueryGuest = mDatabase.orderByChild("userType").equalTo("guest");

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        guestListview = (ListView) findViewById(R.id.guestListSearch);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });


        ButterKnife.bind(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                //on submit
                Log.i("well", " this worked too");
//                mQueryGuestName =  mQueryGuest.orderByChild("name").equalTo(s);
                mQueryGuest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = null;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.child("name").getValue().equals(s)||child.child("email").getValue().equals(s))
//                                User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                 user = child.getValue(User.class);
                            if (!guestItem.contains(user))
                                guestItem.add(user);

                        }
                        guestListAdapter = new SearchListAdapter(getApplicationContext(), guestItem,fromwh,type);
                        guestListview.setAdapter(guestListAdapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                //get all text changes
                Log.i("well", " this worked");
                mQueryGuest.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //ADDED ON 6/4/2017 ALICE
                        guestItem.clear();
//                        guestList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
//                                guestList.add(child.getKey());
                                User temp = child.getValue(User.class);
                                Log.d("quert",s);
                                Log.d("String",temp.getName());
                                temp.setId(child.getKey());
                                if(temp.getName().contains(s)&&!guestItem.contains(temp))
                                    guestItem.add(temp);
                        }

                        //for album search
                        if(fromwh.equals("album")){
                            mDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User temp = dataSnapshot.getValue(User.class);
                                    if(temp.getName().contains(s)&&!guestItem.contains(temp))
                                        guestItem.add(temp);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                            if(guestItem!=null) {
                                guestListAdapter = new SearchListAdapter(getApplicationContext(), guestItem,fromwh,type);
//                                guestListAdapter = new SearchListAdapter(getApplicationContext(), guestItem,guestList,fromwh,type);
                            }
                        if(guestListAdapter!=null)
                            guestListview.setAdapter(guestListAdapter);
//                            guestListAdapter.notifyDataSetChanged();
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


                return false;
            }
        });        return super.onCreateOptionsMenu(menu);
    }

    public static class User {

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String id;
        private String name;
        private String email;
        //    private String password;
        private String userType;

        public String getUserPic() {
            return userPic;
        }

        public void setUserPic(String userPic) {
            this.userPic = userPic;
        }

        private String userPic;

        public User(){

        }

        public User(String name, String email, String userType) {
            this.name = name;
            this.email = email;
            this.userType = userType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

        public void setUserType(String userType) { this.userType = userType;}
        public String getUserType(){ return userType;}


    }

}
