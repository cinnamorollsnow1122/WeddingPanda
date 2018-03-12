package com.example.onpus.weddingpanda.Game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

import com.example.onpus.weddingpanda.LoginAct;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.User;
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
    private DataListAdapter adapter;
    private com.google.firebase.database.Query mQueryType;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference newUser=ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("waitingrm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_rm);
        ButterKnife.bind(this);
        WaitingRmAct.this.setTitle("Waiting Room");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.gamemenu);
        enter();
        initialize();
    }






    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.gamemenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                    mQueryType = db.child("Users").orderByChild("guest/"+id).equalTo(false);
                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String coupleid = null;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                coupleid = child.getKey();
                            }


                            if(coupleid!=null){
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
                    setAdapter(id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setAdapter(String id){
        ref.child("Games").child(id).child("DrawCircle").child("waitingrm").addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void enter(){
            //getUser
        newUser.child(currentUser.getUid()).setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        newUser.child(currentUser.getUid()).removeValue();

    }

    @Override
    protected void onResume() {
        super.onResume();
        newUser.child(currentUser.getUid()).setValue("true");

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
