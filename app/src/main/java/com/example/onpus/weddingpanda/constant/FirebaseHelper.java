package com.example.onpus.weddingpanda.constant;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by onpus on 2018/1/14.
 */

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved=null;
    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    //SAVE
    public  Boolean save(Object obj,String title)
    {
        if(obj==null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child(title).push().setValue(obj);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    //READ
    public ArrayList<String> retrieve()
    {
        final ArrayList<String> spacecrafts=new ArrayList<>();
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot,spacecrafts);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot,spacecrafts);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return spacecrafts;
    }
    private void fetchData(DataSnapshot snapshot,ArrayList<String> spacecrafts)
    {
        spacecrafts.clear();
        for (DataSnapshot ds:snapshot.getChildren())
        {
//            String name=ds.getValue(Spacecraft.class).getName();
//            spacecrafts.add(name);
        }
    }
}