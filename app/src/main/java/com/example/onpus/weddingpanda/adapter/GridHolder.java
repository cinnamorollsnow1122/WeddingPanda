package com.example.onpus.weddingpanda.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onpus on 2018/1/4.
 */

public class GridHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivImage)
    ImageView imageAlbum;
    @BindView(R.id.tvCaption)
    TextView caption;
    @BindView(R.id.cardalbum)
    CardView card;

    public GridHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

    }
}
