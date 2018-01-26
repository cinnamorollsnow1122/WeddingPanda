package com.example.onpus.weddingpanda.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.GridHolder;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Album extends Fragment {

    @BindView(R.id.lyj_recycler)
    RecyclerView recyclerAlbumView;
    private MyAdapterAlbum adapter;
    protected ArrayList<AlbumItem> albumItems = new ArrayList<>();
    private DatabaseReference mAlbumRef;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;
    private com.google.firebase.database.Query mQueryAlbum;
    private FirebaseAuth mAuth;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ButterKnife.bind(this,view);
        initialiseView();
        return view;
    }

    @OnClick(R.id.floatalbum_btn)
    public void onClick(View view){
        AlbumAddIFragment frag = new AlbumAddIFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.albumFrag
                        , frag)
                .addToBackStack(null)
                .commit();

    }

    private void initialiseView() {
        try {
//            GridLayoutManager  mLayoutManager = new GridLayoutManager(getActivity(), 2);
//            recyclerAlbumView.setLayoutManager(mLayoutManager);
            mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("album");
//            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//            recyclerAlbumView.setLayoutManager(mLayoutManager);
            recyclerAlbumView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerAlbumView.setHasFixedSize(true);
            recyclerAlbumView.setItemViewCacheSize(20);
            recyclerAlbumView.setDrawingCacheEnabled(true);
            recyclerAlbumView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//            mQueryAlbum =  mDatabase.orderByChild("Users/"+user.getUid());
            //mListRef.removeValue();
            setupAdapter();

        } catch (Exception e) {

        }
    }


    private void setupAdapter() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //ADDED ON 6/4/2017 ALICE
                albumItems.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        AlbumItem temp = child.getValue(AlbumItem.class);
//                        AlbumItem temp = new AlbumItem();
//
//                        temp.setCoverimage(child.child("albumid").getValue(String.class));
//                        temp.setCaption(child.child("caption").getValue(String.class));
//                        temp.setAlbumid(child.child("coverimage").getValue(String.class));
                        albumItems.add(temp);
                    } catch (Exception e) {

                    }
                }
                try{

                    adapter = new MyAdapterAlbum(getActivity(), albumItems);}catch(Exception e)
                {}                //Log.d("HH",usernames.get(0)+"");

                recyclerAlbumView.setAdapter(adapter);

                //adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

}
