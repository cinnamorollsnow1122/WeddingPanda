package com.example.onpus.weddingpanda.constant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.onpus.weddingpanda.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by onpus on 2018/4/1.
 */

public class CustomDialogClassQr extends Dialog implements android.view.View.OnClickListener {
    public Activity activity;
    public Dialog dialog;

    @BindView(R.id.backQr)
    Button qrBack;

    public CustomDialogClassQr(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qrcode_dialog);
        ButterKnife.bind(this);
        getQrcode();

    }

    private void getQrcode() {
         final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference mgetQr = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Genqr");
         mgetQr.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 String qrcode = currentUser.getUid();
                 for (DataSnapshot childlist: dataSnapshot.getChildren()){
                     qrcode = qrcode+","+childlist.getKey();

                 }
                 //create qrcode
                 MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                 try {
                     BitMatrix bitMatrix = multiFormatWriter.encode(qrcode, BarcodeFormat.QR_CODE,200,200);
                     BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                     Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                     ImageView qrcodeImage = (ImageView)findViewById(R.id.qrcodeAlbum);
                     qrcodeImage.setImageBitmap(bitmap);
                 } catch (WriterException e) {
                     e.printStackTrace();
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backQr:
                activity.finish();
                break;

            default:
                break;
        }
        dismiss();
    }
}

