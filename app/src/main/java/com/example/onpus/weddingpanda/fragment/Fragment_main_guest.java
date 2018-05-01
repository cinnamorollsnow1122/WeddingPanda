package com.example.onpus.weddingpanda.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_main_guest extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    @BindView(R.id.imageCover)
    ImageView imageCover;
    @BindView(R.id.msgForguest)
    TextView msgforguest;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    public Fragment_main_guest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_main_guest, container, false);
        ButterKnife.bind(this,view);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        toolbar.inflateMenu(R.menu.mainmenu);
        setCoverImage();

        return view;
    }
    private void setCoverImage() {
        db.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String coupleid = "";
                Boolean accpeted = false;
               if (dataSnapshot.hasChild("couple")){
                   for (DataSnapshot child :dataSnapshot.child("couple").getChildren()){
                       coupleid = child.getKey();
                       accpeted = child.getValue(Boolean.class);
                   }
               }else{
                   msgforguest.setVisibility(View.VISIBLE);
                   msgforguest.setText("You havent join any wedding!");
                   //imageCover.setVisibility(View.GONE);
               }

                if (accpeted){

                    db.child("WeddingInfo").child(coupleid).child("coverimage").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null){
                                String imageUrl = dataSnapshot.getValue(String.class);
                                msgforguest.setVisibility(View.GONE);
                                imageCover.setVisibility(View.VISIBLE);
                                Picasso.with(getContext())
                                        .load(imageUrl)
                                        .into(imageCover);
                            }
                            else{
                                msgforguest.setVisibility(View.VISIBLE);
                                imageCover.setVisibility(View.GONE);
                                msgforguest.setText("Couple didnt add any coverpic!");

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


}
