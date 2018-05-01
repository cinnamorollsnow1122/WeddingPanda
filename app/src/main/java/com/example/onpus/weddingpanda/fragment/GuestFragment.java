package com.example.onpus.weddingpanda.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.Weddinginfo_act;
import com.example.onpus.weddingpanda.adapter.SearchListAdapter;
import com.example.onpus.weddingpanda.constant.CustomDialogClassRb;
import com.example.onpus.weddingpanda.constant.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GuestFragment extends Fragment {
    @BindView(R.id.guestlist)
    ListView guestlist;
    @BindView(R.id.acceptNo)
    TextView acceptNo;
    @BindView(R.id.inviteNo)
    TextView inviteNo;

    //firebase
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    com.google.firebase.database.Query mQueryGuest;

    public GuestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guest, container, false);

        ButterKnife.bind(this,view);
        initialiseView();
        return view;
    }
    public void initialiseView(){
        mQueryGuest = db.child("Users").orderByChild("couple/" + userId).equalTo(false);
        mQueryGuest.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 ArrayList<User> guestItem = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                   User temp = child.getValue(User.class);
                    if(!guestItem.contains(temp))
                        guestItem.add(temp);

                }
                if(guestItem!=null) {
                    GuestFragment.DataListAdapter guestListAdapter = new GuestFragment.DataListAdapter(getContext(), guestItem);
                    guestlist.setAdapter(guestListAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
@OnClick(R.id.addInfo)
public void onClick(){
        startActivity(new Intent(getActivity(), Weddinginfo_act.class));
}
    class DataListAdapter extends BaseAdapter {
        ArrayList<User> userinfo = new ArrayList<>();
        Context c ;
        DataListAdapter() {
            userinfo = null;

        }

        public DataListAdapter( Context c,ArrayList<User> userinfo) {
            this.userinfo = userinfo;
            this.c = c;


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
            final TextView username,type,answerstatus;
            ImageView icon;
//            final String partid = userinfo.get(position).getId();
            username = (TextView) row.findViewById(R.id.iconName);
            icon=(ImageView)row.findViewById(R.id.iconWList);
            type=(TextView)row.findViewById(R.id.typeUser);
            answerstatus = (TextView)row.findViewById(R.id.ansstatus);
            username.setText(userinfo.get(position).getName());
            type.setText(userinfo.get(position).getUserType());
            String size = String.valueOf(userinfo.size());
            inviteNo.setText(size);
            acceptNo.setText(size);
            Picasso.with(getContext()).load(userinfo.get(position).getUserPic()).into(icon);

            return (row);
        }
    }
}
