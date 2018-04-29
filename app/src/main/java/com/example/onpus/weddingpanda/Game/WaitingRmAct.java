package com.example.onpus.weddingpanda.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.onpus.weddingpanda.Game.gamePhoto.GamePhoto;
import com.example.onpus.weddingpanda.LoginAct;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.User;
import com.example.onpus.weddingpanda.fragment.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaitingRmAct extends AppCompatActivity {
    @BindView(R.id.waitinglist)
    ListView waitingList;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    private final int WAIT_TIME = 2500;
    private Handler uiHandler;
    private DataListAdapter adapter;
    private com.google.firebase.database.Query mQueryType;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String game;
    final DatabaseReference newUser=ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("waitingrm");
    private ValueEventListener mListener;
    private ValueEventListener mListenerSend;
    private DatabaseReference mReadReference =  ref.child("Users").child(currentUser.getUid()).child("userType");
    private DatabaseReference mReadReferenceSend ;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting_rm);
        ButterKnife.bind(this);
        WaitingRmAct.this.setTitle("Waiting Room");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
            Bundle b = getIntent().getExtras();
            if (b != null) {
                game = b.getString("Game");
            } else Log.i("BUNDLE","Null");

            initialize();

    }



    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        //check if guest
        db.child("Users").child(currentUser.getUid()).child("userType").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                String id = currentUser.getUid();
                Log.d("typeA","guest");
                //check guest
                if(type.equals("guest")) {

                }else{
                    getMenuInflater().inflate(R.menu.gamemenu, menu);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check guest

        switch (item.getItemId()) {
            case R.id.add_Guest:

                Intent pIntent = new Intent(this, SearchActivity.class);
                Bundle pBundle = new Bundle();
                pBundle.putString("Waitrm","waiting");
                pIntent.putExtras(pBundle);
                startActivity(pIntent);
                return true;

            case R.id.action_send:
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                //take list
                //send to start game
                if (game.equals("DrawCircle")){
                    Intent sIntent = new Intent(this, LotteryWheelAct.class);
                    startActivity(sIntent);
                }
                if (game.equals("Redblue")){
                    Intent sIntent = new Intent(this, GameRB.class);
                    startActivity(sIntent);
                }
                if (game.equals("PhotoGame")){
                    Intent sIntent = new Intent(this, GamePhoto.class);
                    startActivity(sIntent);
                }

                ref.child("Games").child(currentUser.getUid()).child(game).child("send").setValue(true);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public void initialize() {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        //check if guest
        db.child("Users").child(currentUser.getUid()).child("userType").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                String id = currentUser.getUid();
                Log.d("typeA","guest");
                //check guest
                if(type.equals("guest")) {
                    //if it is guest
                    mQueryType = db.child("Users").orderByChild("guest/"+id).equalTo(false);
                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String coupleid = null;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                coupleid = child.getKey();
                            }


                            if(coupleid!=null){
                                //find couple id
                                send(coupleid);
                                setAdapter(coupleid);


                            }else{
                                new SweetAlertDialog(WaitingRmAct.this)
                                        .setTitleText("Sorry!")
                                        .setContentText("You didnt join any wedding!")
                                        .show();
                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
//                    send(id);//for testing
                    setAdapter(id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void send(String id){
        mReadReferenceSend = ref.child("Games").child(id).child(game);
        mListenerSend =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("send")) {
                    // run some code
                    if (game.equals("DrawCircle")){
                        Intent sIntent = new Intent(WaitingRmAct.this, LotteryWheelAct.class);
                        startActivity(sIntent);
                    }
                    if (game.equals("Redblue")){
                        Intent sIntent = new Intent(WaitingRmAct.this, GameRB.class);
                        startActivity(sIntent);
                    }

                    if (game.equals("PhotoGame")){
                        Intent sIntent = new Intent(WaitingRmAct.this, GamePhoto.class);
                        startActivity(sIntent);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReadReferenceSend.addValueEventListener(mListenerSend);


    }

    public void setAdapter(String id){
        //enter
        ref.child("Games").child(id).child(game).child("waitingrm").child(currentUser.getUid()).setValue("true");

        ref.child("Games").child(id).child(game).child("waitingrm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> waitingListid = new ArrayList<>();
                for (DataSnapshot childlist: dataSnapshot.getChildren()){

                        waitingListid.add(childlist.getKey());


                    Log.d("key",childlist.getKey());
                }

                if(!waitingListid.isEmpty()){
                    ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<User> userinfo = new ArrayList<>();
                            for (DataSnapshot childlist: dataSnapshot.getChildren()){
                                for(String id :waitingListid ){
                                    if (childlist.getKey().equals(id)){
                                        User temp = childlist.getValue(User.class);
                                        userinfo.add(temp);
                                        break;

                                    }
                                }

                            }
                            if(!userinfo.isEmpty()) {
                                adapter = new DataListAdapter(userinfo);
                                waitingList.setAdapter(adapter);


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
//                if (start){
//                    Intent sIntent = new Intent(WaitingRmAct.this, LotteryWheelAct.class);
//                    Bundle sBundle = new Bundle();
//                    sBundle.putString("Waitrm","waiting");
//                    sIntent.putExtras(sBundle);
//                    startActivity(sIntent);
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //dialog for leave rm
        final DatabaseReference newUser=ref.child("Games").child(currentUser.getUid()).child(game).child("waitingrm");

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Leave room?")
                .setContentText("After game start, you may not able to come in")
                .setCancelText("Yes!")
                .setConfirmText("No!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        //del member in database
                        mListener =new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String type = dataSnapshot.getValue(String.class);
                                String id = currentUser.getUid();
                                Log.d("typeA","guest");
                                //check guest
                                if(type.equals("guest")) {
                                    mQueryType = ref.child("Users").orderByChild("guest/"+id).equalTo(false);
                                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String coupleid = null;
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                coupleid = child.getKey();
                                            }


                                            if(coupleid!=null){
                                                ref.child("Games").child(coupleid).child(game).child("waitingrm").child(currentUser.getUid()).removeValue();

                                            }else{

                                            }


                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else{
                                    newUser.child(currentUser.getUid()).removeValue();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        mReadReference.addValueEventListener(mListener);
                        sDialog.cancel();
                        WaitingRmAct.super.onBackPressed();

                    }
                })
                .show();
    }


//    }
@Override
public void onStop() {

    // Remove post value event listener
    if (mListener != null && mReadReference!=null) {
        mReadReference.removeEventListener(mListener);

    }
    if (mReadReference!=null&&mReadReferenceSend!=null)
        mReadReferenceSend.removeEventListener(mListenerSend);
    super.onStop();
}

    @Override
    protected void onPause() {
        // Remove post value event listener
        if (mListener != null && mReadReference!=null) {
            mReadReference.removeEventListener(mListener);


        }

        if (mReadReference!=null&&mReadReferenceSend!=null)
            mReadReferenceSend.removeEventListener(mListenerSend);
        super.onPause();

    }



    class DataListAdapter extends BaseAdapter {
        ArrayList<User> userinfo = new ArrayList<>();

        DataListAdapter() {
            userinfo = null;

        }

        public DataListAdapter(ArrayList<User> userinfo) {
            this.userinfo = userinfo;

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return userinfo.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.waitinglistitem, parent, false);
            TextView username,type;
            ImageView icon;
            username = (TextView) row.findViewById(R.id.iconName);
            icon=(ImageView)row.findViewById(R.id.iconWList);
            type=(TextView)row.findViewById(R.id.typeUser);
            username.setText(userinfo.get(position).getName());
            type.setText(userinfo.get(position).getUserType());
            Picasso.with(getApplicationContext()).load(userinfo.get(position).getUserPic()).into(icon);

            return (row);
        }
    }

}
