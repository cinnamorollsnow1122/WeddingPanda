package com.example.onpus.weddingpanda.fragment;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.adapter.MyAdapterItem;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.InAlbumitem;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class Fragment_item_album extends Fragment {

    //view
    @BindView(R.id.lyj_recycler2)
    RecyclerView recyclerAlbumView;
    @BindView(R.id.expanded_image)
    public ImageView expandView;
    //upload
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    protected ArrayList<InAlbumitem> albumItems = new ArrayList<>();
    private MyAdapterItem adapter;

    private String albumid;
    //database
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    public Fragment_item_album() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //take data from album
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            albumid = bundle.getString("albumid");
            Log.i("BUNDLE",bundle.toString());
        } else Log.i("BUNDLE","Null");

        View view = inflater.inflate(R.layout.fragment_fragment_item_album, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        ButterKnife.bind(this,view);
        initialiseView();
        return view;
    }

    //photos showing
    private void initialiseView() {
        try {
//            GridLayoutManager  mLayoutManager = new GridLayoutManager(getActivity(), 2);
//            recyclerAlbumView.setLayoutManager(mLayoutManager);
            mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("album").child(albumid).child("photos");
            StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerAlbumView.setLayoutManager(mLayoutManager);
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
                        InAlbumitem temp = child.getValue(InAlbumitem.class);
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

                    adapter = new MyAdapterItem(getActivity(), albumItems,Fragment_item_album.this);}catch(Exception e)
                {}                //Log.d("HH",usernames.get(0)+"");

                recyclerAlbumView.setAdapter(adapter);

                //adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }








    //upload function

    @OnClick(R.id.floatalbum_btn)
    public void click(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        upload(mArrayUri);
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {  MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    private void upload(final ArrayList<Uri> listphotos) {
        if(listphotos != null)
        {   String uuid = "";
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            for (int i =0 ; i < listphotos.size(); i++) {
                uuid = UUID.randomUUID().toString();


                final StorageReference addalbumref = storageReference.child("images/" + uuid + ".jpg");

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
//                                    String cap = captioninput.getText().toString();
                                        //put album to database
//                                    if(cap.equals(""))
//                                        cap = " ";
                                            newAlbumItem(uri.toString(), "temp", "commenttemp");

                                    }
                                });
                                Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void newAlbumItem(final String images,final String sender, final String comment){
        //add album
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newAlbum=ref.child("Users").child(currentUser.getUid()).child("album").child(albumid).child("photos").push();
        final String[] pushkey = {""};

        pushkey[0] = newAlbum.getKey();

        //add album to database
        newAlbum.setValue(new InAlbumitem(images,sender,comment,pushkey[0]));

    }
}
