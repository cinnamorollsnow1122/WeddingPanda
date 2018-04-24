package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/4/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.Game.GameRB;
import com.example.onpus.weddingpanda.Game.WaitingRmAct;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by onpus on 2018/4/1.
 */

public class CustomDialogClassMsgWin extends Dialog {
    public Activity activity;
    public Dialog dialog;
    public String coupleid;

    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public CustomDialogClassMsgWin(Activity activity,String coupleid) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.coupleid = coupleid;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rb_win);
        ButterKnife.bind(this);
        initList();

    }

    private void initList() {
        //enter

    }

@OnClick(R.id.backRb)
    public void onClick() {
//    ref.child("Games").child(coupleid).child("Redblue").child("AnswerGuest").addListenerForSingleValueEvent(new ValueEventListener() {
//
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            final String correct;
//            int correntNum = 0;
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                for (DataSnapshot userAns : child.getChildren()) {
//                    if (userAns.getKey().equals(currentUser.getUid()) && userAns.getValue().equals(true)) {
//                        correntNum++;
//                    }
//                }
//            }
//            String correctNumber = String.valueOf(correntNum);
//            correct = correctNumber;
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    });

        Intent pIntent = new Intent(activity, MainActivity.class);
//    pIntent.putExtra("Game", "Redblue");
        activity.startActivity(pIntent);


    }
}

