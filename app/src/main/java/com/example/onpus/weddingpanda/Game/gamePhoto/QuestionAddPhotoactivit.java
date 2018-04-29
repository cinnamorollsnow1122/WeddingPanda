package com.example.onpus.weddingpanda.Game.gamePhoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionAddPhotoactivit extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePathA;
    private Uri filePathB;
    FirebaseStorage storage;
    StorageReference storageReference;
    Boolean uploadA = false;
    Boolean uploadB = false;
    @BindView(R.id.photoB)
    ImageView photoBImage;
    @BindView(R.id.photoA)
    ImageView photoAImage;

    @BindView(R.id.ansforA)
    RadioButton radioBtnA;

    @BindView(R.id.ansforB)
    RadioButton radioBtnB;

    @BindView(R.id.getQuestion)
    EditText getQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add_photoactivit);
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


    }



    @OnClick(R.id.photoA)
    public void uploadA(){
        uploadA = true;
        uploadB = false;
        choose();


    }
    @OnClick(R.id.photoB)
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(QuestionAddPhotoactivit.this.getContentResolver(), filePathTemp);
                if (uploadA){
                    uploadA = false;
                    photoAImage.setImageBitmap(bitmap);
                    filePathA= filePathTemp;
                }else if (uploadB)
                {
                    uploadB = false;
                    photoBImage.setImageBitmap(bitmap);
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
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Games").child(currentUser.getUid()).child("PhotoGame");
        final DatabaseReference newPhoto=ref.child("Question").push();
        final String[] pushkey = {""};

        newPhoto.child("question").setValue(getQuestion.getText().toString());

        pushkey[0] = newPhoto.getKey();

        if (radioBtnA.isChecked())
            ref.child("Question").child(pushkey[0]).child("answer").setValue("imageA");
        else if (radioBtnB.isChecked())
            ref.child("Question").child(pushkey[0]).child("answer").setValue("imageB");

        if(filePathA != null&&filePathB!=null)
        {
            ArrayList<Uri> listphotos = new ArrayList<>();
            String uuid;
            listphotos.add(filePathA);
            listphotos.add(filePathB);

            final ProgressDialog progressDialog = new ProgressDialog(QuestionAddPhotoactivit.this);
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
                                            ref.child("Question").child(pushkey[0]).child("imageA").setValue(uri.toString());
                                        }else if (finalI1==1){
                                            ref.child("Question").child(pushkey[0]).child("imageB").setValue(uri.toString());
                                            new SweetAlertDialog(QuestionAddPhotoactivit.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Sucessful!")
                                                    .show();
                                            getQuestion.setText("");
                                            uploadA = false;
                                            uploadB = false;
                                            radioBtnA.setChecked(false);
                                            radioBtnB.setChecked(false);
                                            photoAImage.setImageResource(R.color.colorPrimary);
                                            photoBImage.setImageResource(R.color.colorPrimary);

                                        }

                                    }
                                });
                                Toast.makeText(QuestionAddPhotoactivit.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(QuestionAddPhotoactivit.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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





    static class NewPhotoQuesiton{
        private String questionkey;
        private String imageA;
        private String imageB;
        private String question;
        private String answer;

        public NewPhotoQuesiton( String question, String answer) {
            this.question = question;
            this.answer = answer;
        }


        public NewPhotoQuesiton(){

        }


        public String getImageA() {
            return imageA;
        }

        public void setImageA(String imageA) {
            this.imageA = imageA;
        }

        public String getImageB() {
            return imageB;
        }

        public void setImageB(String imageB) {
            this.imageB = imageB;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getQuestionkey() {
            return questionkey;
        }

        public void setQuestionkey(String questionkey) {
            this.questionkey = questionkey;
        }
    }


    // object



}
