package com.example.onpus.weddingpanda.Scanner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.GridHolder;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.adapter.MyCameraTakeAdapter;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.InAlbumitem;
import com.example.onpus.weddingpanda.fragment.Fragment_item_album;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoTakenActivity extends AppCompatActivity {
    // This tag is used for error or debug log.
    private static final String TAG_TAKE_PICTURE = "TAKE_PICTURE";
    int REQUEST_CODE_ASK_PERMISSIONS = 123;
    // This is the request code when start camera activity use implicit intent.
    public static final int REQUEST_CODE_TAKE_PICTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @BindView(R.id.expanded_image_camera)
    public ImageView expandViewCamera;

   @BindView(R.id.take_picture_image_view)
   ImageView takePictureImageView;

   @BindView(R.id.lyj_recycler_take)
    RecyclerView recyclerView_camera;
    String albumid;
    // This output image file uri is used by camera app to save taken picture.
    private Uri outputImgUri;

    // Save the camera taken picture in this folder.
    private File pictureSaveFolderPath;

    // Save imageview currently displayed picture index in all camera taken pictures..
    private int currentDisplayImageIndex = 0;
    ArrayList<Bitmap> temp = new ArrayList<>() ;
    ArrayList<Uri> temp2 = new ArrayList<>() ;
    List nameList = new ArrayList<>() ;

    MyCameraTakeAdapter adapter;

    //database
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    String userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_taken);
//        ActivityCompat.requestPermissions(PhotoTakenActivity.this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        setTitle("Take your photo");
        ButterKnife.bind(this);
        initialiseView();
        Intent intent = getIntent();
        userList = intent.getStringExtra("key"); //if it's a string you stored.


        for (String retval: userList.split(",")) {
            nameList.add(retval);
        }


        pictureSaveFolderPath = getExternalCacheDir();
        takePictureImageView = (ImageView)findViewById(R.id.take_picture_image_view);
        takePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pictureSaveFolderPath!=null) {
                    // Get all camera taken pictures in picture save folder.
                    File imageFiles[] = pictureSaveFolderPath.listFiles();
                    if (imageFiles!=null)
                    {
                        // Get content resolver object.
                        ContentResolver contentResolver = getContentResolver();
                        int allImagesCount = imageFiles.length;
                        // If current display picture index is bigger than image count.
                        if(currentDisplayImageIndex >= allImagesCount-1)
                        {
                            currentDisplayImageIndex = 0;
                        }else
                        {
                            currentDisplayImageIndex++;
                        }

                        // Get to be displayed image file object.
                        File displayImageFile = imageFiles[currentDisplayImageIndex];

                        // Get display image Uri wrapped object.
                        Uri displayImageFileUri = getImageFileUriByOsVersion(displayImageFile);

                        try {
                            // Open display image input stream.
                            InputStream inputStream = contentResolver.openInputStream(displayImageFileUri);

                            // Decode the image input stream to a bitmap use BitmapFactory.
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);

                            // Set the image bitmap in the image view component to display it.
                            takePictureImageView.setImageBitmap(pictureBitmap);

                        }catch(FileNotFoundException ex) {
                            Log.e(TAG_TAKE_PICTURE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        });

        // Get the take picture button object.
        Button takePictureButton = (Button)findViewById(R.id.take_picture_button);




    }

    @OnClick(R.id.camera_floatBtn)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View view) {
        try {

            // Create a random image file name.
            String imageFileName = "outputImage_" + System.currentTimeMillis() + ".png";

            // Construct a output file to save camera taken picture temporary.
            File outputImageFile = new File(pictureSaveFolderPath, imageFileName);

            // If cached temporary file exist then delete it.
            if (outputImageFile.exists()) {
                outputImageFile.delete();
            }

            // Create a new temporary file.
            outputImageFile.createNewFile();

            // Get the output image file Uri wrapper object.
            outputImgUri = getImageFileUriByOsVersion(outputImageFile);

            // Startup camera app.
            // Create an implicit intent which require take picture action..
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Specify the output image uri for the camera app to save taken picture.
            if (outputImgUri!=null){
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputImgUri);
                // Start the camera activity with the request code and waiting for the app process result.
                if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);

                }
                else{
                    ActivityCompat.requestPermissions(PhotoTakenActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }



        }catch(IOException ex)
        {
            Log.e(TAG_TAKE_PICTURE, ex.getMessage(), ex);
        }
    }

    /* Get the file Uri object by android os version.
    *  return a Uri object. */
      Uri getImageFileUriByOsVersion(File file)
    {
        Uri ret = null;

        // Get output image unique resource identifier. This uri is used by camera app to save taken picture temporary.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            // /sdcard/ folder link to /storage/41B7-12F1 folder
            // so below code return /storage/41B7-12F1
            File externalStorageRootDir = Environment.getExternalStorageDirectory();

            // contextRootDir = /data/user/0/com.dev2qa.example/files in my Huawei mate 8.
            File contextRootDir = getFilesDir();

            // contextCacheDir = /data/user/0/com.dev2qa.example/cache in my Huawei mate 8.
            File contextCacheDir = getCacheDir();

            // For android os version bigger than or equal to 7.0 use FileProvider class.
            // Otherwise android os will throw FileUriExposedException.
            // Because the system considers it is unsafe to use local real path uri directly.
            Context ctx = getApplicationContext();
            ret = FileProvider.getUriForFile(ctx, "com.example.onpus.weddingpanda.fileprovider", file);
        }else
        {
            // For android os version less than 7.0 there are no safety issue,
            // So we can get the output image uri by file real local path directly.
            ret = Uri.fromFile(file);
        }

        return ret;
    }
//permission


    /* This method is used to process the result of camera app. It will be invoked after camera app return.
    It will show the camera taken picture in the image view component. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // Process result for camera activity.
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {

                // If camera take picture success.
                if (resultCode == RESULT_OK) {

                    // Get content resolver.
                    ContentResolver contentResolver = getContentResolver();

                    // Use the content resolver to open camera taken image input stream through image uri.
                    InputStream inputStream = contentResolver.openInputStream(outputImgUri);

                    // Decode the image input stream to a bitmap use BitmapFactory.
                    Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);

                    //resize bitmap
                    Bitmap resized = Bitmap.createScaledBitmap(pictureBitmap,(int)(pictureBitmap.getWidth()*0.4), (int)(pictureBitmap.getHeight()*0.4), true);

                    // Set the camera taken image bitmap in the image view component to display.
                    takePictureImageView.setImageBitmap(resized);
                    temp.add(resized);
                    temp2.add(outputImgUri);

                    adapter = new MyCameraTakeAdapter(PhotoTakenActivity.this,temp,temp2,this);
                    recyclerView_camera.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

//                    updataview();

                }
            }
        }catch(FileNotFoundException ex)
        {
            Log.e(TAG_TAKE_PICTURE, ex.getMessage(), ex);
        }
    }

    private void initialiseView() {
        recyclerView_camera.setHasFixedSize(true);
        recyclerView_camera.setItemViewCacheSize(20);
        recyclerView_camera.setDrawingCacheEnabled(true);
        recyclerView_camera.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

//        int spanCount = 2; // 3 columns
//        int spacing = 5; // 50px
//        boolean includeEdge = false;
//        recyclerView_camera.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        recyclerView_camera.setLayoutManager(mLayoutManager);

    }
    @OnClick(R.id.sendCamera)
    public void onClick(){
        if (temp2.isEmpty())
            Toast.makeText(PhotoTakenActivity.this, "No photos!", Toast.LENGTH_SHORT).show();
        else{
            ArrayList<Uri> newTemp = new ArrayList<>();
            for (Bitmap map:temp){
                if (map!=null)
                    if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {

                       if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED){
                           newTemp.add(getImageUri(PhotoTakenActivity.this,map));

                       }else{
                           ActivityCompat.requestPermissions(PhotoTakenActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);

                       }

                    }
                    else{
                        ActivityCompat.requestPermissions(PhotoTakenActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                        ActivityCompat.requestPermissions(PhotoTakenActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                    }
            }
            upload(newTemp);

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //


        Bitmap resized = Bitmap.createScaledBitmap(inImage,(int)(inImage.getWidth()*0.4), (int)(inImage.getHeight()*0.4), true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), resized, "Title", null);
        return Uri.parse(path);

    }

    private void upload(final ArrayList<Uri> listphotos) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(listphotos != null)
        {   String uuid = "";
            final ProgressDialog progressDialog = new ProgressDialog(PhotoTakenActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            for (int i =0 ; i < listphotos.size(); i++) {
                uuid = UUID.randomUUID().toString();


                final StorageReference addalbumref = storageReference.child("album/" + uuid + ".jpg");

                final int finalI1 = i;
                final int finalI = i;
                addalbumref.putFile(listphotos.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();

                                // getUrl;
                                addalbumref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//                                    String cap = captioninput.getText().toString();
                                        //put album to database
//                                    if(cap.equals(""))
//                                        cap = " ";
                                        if(finalI ==0)
                                            newAlbumItem(uri.toString(), "temp", "commenttemp",userList,0);
                                        else
                                            newAlbumItem(uri.toString(), "temp", "commenttemp",userList,1);


                                    }
                                });

                                Toast.makeText(PhotoTakenActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(PhotoTakenActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    public void newAlbumItem(final String images,final String sender, final String comment, final String usersList,int i){
        //add album
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newAlbum=ref.child("albums").push();
        final String[] pushkey = {""};
        //new Album

            //check if bigday exist
        ref.child("albums").orderByChild("creator").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String albumkey = "";
                    if (dataSnapshot.getValue()==null)
                    {   albumkey = newAlbum.getKey();
                        newAlbum.setValue(new AlbumItem(albumkey, images, "BigDay",currentUser.getUid()));
                    }else{
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            if (child.child("caption").getValue().equals("BigDay")){
//                                albumkey = newAlbum.getKey();
//                                newAlbum.setValue(new AlbumItem(albumkey, images, "BigDay"));
//                            }else{
                                //exist, get Album get key
                                albumkey = child.getKey();
                                break;
                            }

                        }
                    }

                    if (albumkey!=""){
                        final DatabaseReference newPhotos=ref.child("albums").child(albumkey).child("photos").push();
                        String push = newPhotos.getKey();

                        //add album to database
                        newPhotos.setValue(new NewAlbumItem(images,currentUser.getUid(),comment,push));
                        newPhotos.child("userList").setValue(nameList);

//                        for( int i=0;i<nameList.size();i++){
//                            newPhotos.child("userList").child(nameList.get(i));
//
//                        }

//                        for (String retval: userList.split(",")) {
//                            newPhotos.child("userList").child(retval).setValue(true);
//                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//
//        if (pushkey[0]!=null){
//            final DatabaseReference newPhotos=ref.child("Users").child(currentUser.getUid()).child("album").child(pushkey[0]).child("photos").push();
//            String push = newPhotos.getKey();
//
//            //add album to database
//            newPhotos.setValue(new NewAlbumItem(images,currentUser.getUid(),comment,push));
//            newPhotos.child("userList").setValue(nameList);
//        }


    }
    @Override
    protected  void onResume() {
        super.onResume();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pictureSaveFolderPath!=null){
            File imageFiles[] = pictureSaveFolderPath.listFiles();
            if (imageFiles!=null){
                pictureSaveFolderPath.delete();
            }

        }
    }

    public static class NewAlbumItem{
        private String image;
        private String sender;
        private String comment;
        private String id;
        private ArrayList<String> userlists;




    public NewAlbumItem(){

        }



    public NewAlbumItem(String image, String sender, String comment,String id ) {
            this.image = image;
            this.sender = sender;
            this.comment = comment;
            this.id = id;
//            this.userlists = userlists;

        }

        public ArrayList<String> getUserlists() {
            return userlists;
        }

        public void setUserlists(ArrayList<String> userlists) {
            this.userlists = userlists;
        }


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }


        public void setComment(String comment) { this.comment = comment;}
        public String getComment(){ return comment;}

        public void setId(String id) { this.id = id;}
        public String getId(){ return id;}
    }
}
