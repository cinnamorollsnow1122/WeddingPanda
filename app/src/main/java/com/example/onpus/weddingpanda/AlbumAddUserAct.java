package com.example.onpus.weddingpanda;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.Game.LotteryWheelAct;
import com.example.onpus.weddingpanda.Game.WaitingRmAct;
import com.example.onpus.weddingpanda.constant.CustomDialogClassQr;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAddUserAct extends AppCompatActivity {
    @BindView(R.id.genQrlist)
    ListView userList;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    private AlbumAddUserAct.DataListAdapter adapter;
    private com.google.firebase.database.Query mQueryType;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference mgetQr = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Genqr");
    private String type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_add_user);
        ButterKnife.bind(this);

        AlbumAddUserAct.this.setTitle("Gen your Qr code");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get Bundle
        type = getIntent().getExtras().getString("type");
        initialize();
    }

    public void initialize() {



        mgetQr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> genQrlist = new ArrayList<>();
                for (DataSnapshot childlist: dataSnapshot.getChildren()){

                    genQrlist.add(childlist.getKey());


                    Log.d("key",childlist.getKey());
                }

                if(!genQrlist.isEmpty()){
                    ref.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<SearchActivity.User> userinfo = new ArrayList<>();
                            for (DataSnapshot childlist: dataSnapshot.getChildren()){
                                for(String id :genQrlist ){
                                    if (childlist.getKey().equals(id)){
                                        SearchActivity.User temp = childlist.getValue(SearchActivity.User.class);
                                        temp.setId(id);
                                        userinfo.add(temp);
                                        break;

                                    }
                                }

                            }
                            if(!userinfo.isEmpty()) {
                                adapter = new AlbumAddUserAct.DataListAdapter(userinfo,AlbumAddUserAct.this);
                                userList.setAdapter(adapter);


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






    //menu
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.gamemenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check guest

        switch (item.getItemId()) {
            case R.id.add_Guest:

                Intent pIntent = new Intent(this, SearchActivity.class);
                Bundle pBundle = new Bundle();
                pBundle.putString("Waitrm","album");
                pIntent.putExtras(pBundle);
                startActivity(pIntent);
                return true;

            case R.id.action_send:
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                CustomDialogClassQr cdd=new CustomDialogClassQr(AlbumAddUserAct.this);
                cdd.show();
                //take list

//                Intent sIntent = new Intent(this, LotteryWheelAct.class);
//                Bundle sBundle = new Bundle();
//                sBundle.putString("Waitrm","waiting");
//                sIntent.putExtras(sBundle);
//                startActivity(sIntent);
//                ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("send").setValue(true);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    class DataListAdapter extends BaseAdapter {
        ArrayList<SearchActivity.User> userinfo = new ArrayList<>();
        private Context context;
        private DatabaseReference mgetQr = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Genqr");

        public DataListAdapter(ArrayList<SearchActivity.User> userinfo,Context context) {
            this.userinfo = userinfo;
            this.context = context;

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return userinfo.size();
        }

        public SearchActivity.User getItem(int arg0) {
            // TODO Auto-generated method stub
            return userinfo.get(arg0);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.waitinglistitem, parent, false);
            final TextView username,type;
            ImageView icon;
            username = (TextView) row.findViewById(R.id.iconName);
            icon=(ImageView)row.findViewById(R.id.iconWList);
            type=(TextView)row.findViewById(R.id.typeUser);
            username.setText(getItem(position).getName());
            type.setText(getItem(position).getUserType());
            Picasso.with(getApplicationContext()).load(userinfo.get(position).getUserPic()).into(icon);


            icon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Delete this user")
                            .setContentText("Are you sure?")
                            .setConfirmText("Yes,delete it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override

                                public void onClick(SweetAlertDialog sDialog) {
                                if (getItem(position).getId()!=null) {
                                    mgetQr.child(getItem(position).getId()).removeValue();
                                    sDialog
                                            .setTitleText("Deleted!")
                                            .setContentText("Your imaginary file has been deleted!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                                }
                            })
                            .show();
                    return true;
                }
            });
            return (row);
        }
    }
}





