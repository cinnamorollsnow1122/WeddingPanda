package com.example.onpus.weddingpanda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.onpus.weddingpanda.Game.gamePhoto.QuestionAddPhotoactivit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Weddinginfo_act extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePathA;
    private Uri filePathB;
    FirebaseStorage storage;
    StorageReference storageReference;
    Boolean uploadA = false;
    Boolean uploadB = false;
    @BindView(R.id.seatingplan)
    ImageView seatplanB;
    @BindView(R.id.imageCover)
    ImageView imageCoverA;


    @BindView(R.id.getvenue)
    EditText getVenue;
    @BindView(R.id.getTime)
    EditText getTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weddinginfo_act);
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @OnClick(R.id.imageCover)
    public void uploadA(){
        uploadA = true;
        uploadB = false;
        choose();


    }
    @OnClick(R.id.seatingplan)
    public void uploadB(){
        uploadB = true;
        uploadA = false;
        choose();


    }

    @OnClick(R.id.submitPhoto)
    public void uploadphoto(){
        upload();

    }

    private void choose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri filePathTemp = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Weddinginfo_act.this.getContentResolver(), filePathTemp);
                if (uploadA){
                    uploadA = false;
                    imageCoverA.setImageBitmap(bitmap);
                    filePathA= filePathTemp;
                }else if (uploadB)
                {
                    uploadB = false;
                    seatplanB.setImageBitmap(bitmap);
                    filePathB= filePathTemp;

                }


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void upload() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("WeddingInfo").child(currentUser.getUid());
        final DatabaseReference newInfo=ref;
        final String[] pushkey = {""};

        ref.child("venue").setValue(getVenue.getText().toString());
        ref.child("date").setValue(getTime.getText().toString());

        pushkey[0] = newInfo.getKey();


        if(filePathA != null&&filePathB!=null)
        {
            ArrayList<Uri> listphotos = new ArrayList<>();
            String uuid;
            listphotos.add(filePathA);
            listphotos.add(filePathB);

            final ProgressDialog progressDialog = new ProgressDialog(Weddinginfo_act.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            for (int i =0 ; i < listphotos.size(); i++) {
                uuid = UUID.randomUUID().toString();


                final StorageReference addalbumref = storageReference.child("photoGame/" + uuid + ".jpg");

                final int finalI1 = i;
                addalbumref.putFile(listphotos.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();

                                // getUrl;
                                addalbumref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//
                                        if (finalI1==0) {
                                            ref.child("coverimage").setValue(uri.toString());
                                        }else if (finalI1==1){
                                            ref.child("seatImage").setValue(uri.toString());
                                            new SweetAlertDialog(Weddinginfo_act.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Sucessful!")
                                                    .show();
                                            getTime.setText("");
                                            getVenue.setText("");
                                            uploadA = false;
                                            uploadB = false;
                                            seatplanB.setImageResource(R.color.colorPrimary);
                                            imageCoverA.setImageResource(R.color.colorPrimary);

                                        }

                                    }
                                });
                                Toast.makeText(Weddinginfo_act.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Weddinginfo_act.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }



        }

    }
    static class NewWeddingInfo {
        String venue,date,coverimage,seatImage;

        public NewWeddingInfo(String venue, String date, String coverimage, String seatImage) {
            this.venue = venue;
            this.date = date;
            this.coverimage = coverimage;
            this.seatImage = seatImage;
        }


        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCoverimage() {
            return coverimage;
        }

        public void setCoverimage(String coverimage) {
            this.coverimage = coverimage;
        }

        public String getSeatImage() {
            return seatImage;
        }

        public void setSeatImage(String seatImage) {
            this.seatImage = seatImage;
        }
    }






    }
