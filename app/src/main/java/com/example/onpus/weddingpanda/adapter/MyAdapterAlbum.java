package com.example.onpus.weddingpanda.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.fragment.Album;
import com.example.onpus.weddingpanda.fragment.Fragment_item_album;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by onpus on 2018/1/5.
 */

public class MyAdapterAlbum extends RecyclerView.Adapter<GridHolder> {
    ArrayList<AlbumItem> data;
    Bitmap[] bitmaps;
    Context c;
    String userid;

    public MyAdapterAlbum(Context c,ArrayList<AlbumItem> data,String userid) {
            this.c = c;
            this.data = data;
            this.userid = userid;
    }
        @Override
        public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.item_album, parent, false);
        return new GridHolder(view);
        }

        @Override
        public void onBindViewHolder(GridHolder holder, final int position) {

            Picasso.with(c).load(data.get(position).getCoverimage()).into(holder.imageAlbum);
            holder.caption.setText(data.get(position).getCaption());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Do on click stuff
                    Bundle i = new Bundle();
                    i.putString("albumid", data.get(position).getAlbumid());
                    i.putString("caption", data.get(position).getCaption());
                    i.putString("creator", data.get(position).getCreator());
                    //if guest pass the couple id
                    if (data.get(position).getCaption().equals("BigDay"))
                        i.putString("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Fragment_item_album frag = new Fragment_item_album();
                    frag.setArguments(i);
                    ((AppCompatActivity) c).getSupportFragmentManager().beginTransaction().replace(R.id.frame, frag).addToBackStack(null).commit();

                }
            });

//            try {
//                URL domain = new URL(data.get(position).getCoverimage());
//
//                Glide.with(c)
//                        .load(data.get(position).getCoverimage())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.imageAlbum);
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }

    }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }



