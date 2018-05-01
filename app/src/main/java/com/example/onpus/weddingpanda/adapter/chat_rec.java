package com.example.onpus.weddingpanda.adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;

/**
 * Created by alice on 1/5/2018.
 */

public class chat_rec extends RecyclerView.ViewHolder  {



    public TextView leftText;
    public TextView rightText;
    public ImageView rightImage;
    public ImageView leftImage;

    public chat_rec(View itemView){
        super(itemView);

        leftText = (TextView)itemView.findViewById(R.id.leftText);
        rightText = (TextView)itemView.findViewById(R.id.rightText);
        rightImage = (ImageView)itemView.findViewById(R.id.rightImage);
        leftImage = (ImageView)itemView.findViewById(R.id.leftImage);


    }
}