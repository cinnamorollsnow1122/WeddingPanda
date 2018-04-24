package com.example.onpus.weddingpanda.Scanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.onpus.weddingpanda.R;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ImageZoomFrag extends Fragment {
    @BindView(R.id.imageZoom)
    ImageView imageZoom;

    String url;

    public ImageZoomFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            Log.i("BUNDLE",bundle.toString());
        } else Log.i("BUNDLE","Null");

        View view = inflater.inflate(R.layout.fragment_image_zoom, container, false);
        ButterKnife.bind(this,view);
        if (url!=null)
        Picasso.with(getContext()).load(url).into(imageZoom);

        return view;
    }

    @OnClick(R.id.imageZoom)
    public void onClick(View view){
        getActivity().getFragmentManager().popBackStack();    }
}
