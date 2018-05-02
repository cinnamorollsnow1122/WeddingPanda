package com.example.onpus.weddingpanda.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.AlbumAddUserAct;
import com.example.onpus.weddingpanda.Game.GameRB;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.Scanner.PhotoTakenActivity;
import com.example.onpus.weddingpanda.Scanner.SimpleScannerActivity;
import com.example.onpus.weddingpanda.Scanner.SimpleScannerFragment;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.adapter.GridHolder;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.FirebaseHelper;
import com.example.onpus.weddingpanda.constant.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Album extends Fragment {

    @BindView(R.id.lyj_recycler)
    RecyclerView recyclerAlbumView;

    @BindView(R.id.iconprof)
    ImageView icon;
    @BindView(R.id.username)
    TextView username;

    private MyAdapterAlbum adapter;
    protected ArrayList<AlbumItem> albumItems = new ArrayList<>();
    private DatabaseReference mAlbumRef;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;
    private com.google.firebase.database.Query mQueryAlbum;
    private FirebaseAuth mAuth;
    private String type;

    //for qr code scanner
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;



    public Album() {
        // Required empty public constructor
    }

    public static Album newInstance(String param1, String param2) {
        Album fragment = new Album();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get type

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
//        Bundle bundle = this.getArguments();
//        if(bundle!=null)
//            type = bundle.getString("type");
//        Log.d("typefromAlbum",type);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ButterKnife.bind(this,view);
        initialiseView();
        return view;
    }


    @OnClick({R.id.item1_btn_addAlbum,R.id.item2_btn_addphoto,R.id.item3_btn_genqrcode})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.item1_btn_addAlbum:
                AlbumAddIFragment frag = new AlbumAddIFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.albumFrag
                                , frag)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.item2_btn_addphoto:
//                launchActivity(SimpleScannerFragment.class);

                SimpleScannerFragment frag2 = new SimpleScannerFragment();
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .add(R.id.albumFrag
                                , frag2)
                        .addToBackStack(null)
                        .commit();
//                Intent pIntent2 = new Intent(getActivity(), SimpleScannerActivity.class);
//                startActivity(pIntent2);

                break;
            case R.id.item3_btn_genqrcode:
                Intent pIntent = new Intent(getActivity(), AlbumAddUserAct.class);
                Bundle pBundle = new Bundle();
                pBundle.putString("Waitrm","album");
                pBundle.putString("type",type);
                pIntent.putExtras(pBundle);
                startActivity(pIntent);
                break;

        }
    }

    @OnClick(R.id.iconprof)
    public void clickicon(){
        Intent intent = new Intent(getActivity(), EditProfiloActivity.class);
        startActivity(intent);
    }

    //scanner



    private void initialiseView() {
        recyclerAlbumView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAlbumView.setHasFixedSize(true);
        recyclerAlbumView.setItemViewCacheSize(20);
        recyclerAlbumView.setDrawingCacheEnabled(true);
        recyclerAlbumView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //check guest
        final String[] coupleid = new String[1];
        final String[] type = new String[1];
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");

        //update icon
        db.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userPic = dataSnapshot.child("userPic").getValue(String.class);
                if(userPic!=null)
                    Picasso.with(getContext()).load(userPic).into(icon);
                String name = dataSnapshot.child("name").getValue(String.class);
                username.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        db.child(userId).child("userType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type[0] = dataSnapshot.getValue(String.class);
                Log.d("typeA","guest");
                //check guest
                if(type[0].equals("guest")){
                    mQueryAlbum = db.orderByChild("guest/"+userId).equalTo(true);
                    mQueryAlbum.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                coupleid[0] = child.getKey();
                                Log.d("coup;eid",coupleid[0]);
                            }


                            if(coupleid[0]!=null){
//                                mDatabase= FirebaseDatabase.getInstance().getReference().child("albums").child(coupleid[0]);
                                setupAdapter(coupleid[0],"guest");
                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else{
//                    mDatabase= FirebaseDatabase.getInstance().getReference().child("albums").child(currentUser.getUid());
                    setupAdapter(currentUser.getUid(),"couple");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//            GridLayoutManager  mLayoutManager = new GridLayoutManager(getActivity(), 2);
//            recyclerAlbumView.setLayoutManager(mLayoutManager);

//            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//            recyclerAlbumView.setLayoutManager(mLayoutManager);

//            mQueryAlbum =  mDatabase.orderByChild("Users/"+user.getUid());
            //mListRef.removeValue();

    }


    private void setupAdapter(final String userid, final String isGuest) {
        mDatabase= FirebaseDatabase.getInstance().getReference().child("albums");
        final Query queryForcoupleItem = mDatabase.orderByChild("creator").equalTo(userid);

        DatabaseReference mBigDay= FirebaseDatabase.getInstance().getReference().child("albums");
        Query query = mBigDay.orderByChild("caption").equalTo("BigDay");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                albumItems.clear();
                final String[] pass = new String[1];
                String isBigDay = "yes";

                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        AlbumItem temp2 = child.getValue(AlbumItem.class);
                        if (temp2.getCreator().equals(currentUser.getUid())) {
                            if (!albumItems.contains(temp2))
                                albumItems.add(temp2);
                            isBigDay = "yes";
                            break;
                        } else {
                            isBigDay = "none";
                        }
                    }
                if (dataSnapshot.getValue()==null||isBigDay.equals("none")){
                    String image = "https://firebasestorage.googleapis.com/v0/b/weddingpanda-f980f.appspot.com/o/album%2FVideo-BigDay-940x529.jpg?alt=media&token=20bb97f1-da2c-4e94-8e80-f8c095d573f0";
                    AlbumAddIFragment.newAlbum(image, "BigDay");
                }

                queryForcoupleItem.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //ADDED ON 6/4/2017 ALICE
                        //check if there is isBigDay album
                        //check any big day
                        for (DataSnapshot child : snapshot.getChildren()) {
                            try {
                                AlbumItem temp = child.getValue(AlbumItem.class);
                                if (!temp.getCaption().equals("BigDay")){ //skip couple big day album
                                    if (!albumItems.contains(temp))
                                        albumItems.add(temp);

                                }


//                        AlbumItem temp = new AlbumItem();
//
//                        temp.setCoverimage(child.child("albumid").getValue(String.class));
//                        temp.setCaption(child.child("caption").getValue(String.class));
//                        temp.setAlbumid(child.child("coverimage").getValue(String.class));
                            } catch (Exception e) {

                            }
                        }

                        if (dataSnapshot!=null){
                            adapter = new MyAdapterAlbum(getActivity(), albumItems, userid);
                            recyclerAlbumView.setAdapter(adapter);
                        }else{
                            pass[0] = "pass";
                        }

                        //adapter.notifyDataSetChanged();
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                if (!albumItems.isEmpty()&&pass[0]!=null) {
                    adapter = new MyAdapterAlbum(getActivity(), albumItems, userid);
                    recyclerAlbumView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
