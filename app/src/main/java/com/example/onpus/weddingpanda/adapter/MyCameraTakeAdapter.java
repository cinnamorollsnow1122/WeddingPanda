package com.example.onpus.weddingpanda.adapter;

import android.animation.Animator;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.Scanner.ImageZoomFrag;
import com.example.onpus.weddingpanda.Scanner.PhotoTakenActivity;

import com.example.onpus.weddingpanda.fragment.AlbumAddIFragment;
import com.example.onpus.weddingpanda.fragment.Fragment_item_album;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onpus on 2018/4/3.
 */

public class MyCameraTakeAdapter extends RecyclerView.Adapter<MyCameraTakeAdapter.GridItem2Holder> {
    File data[];
   ArrayList<Bitmap> bitmaps;
   ArrayList<Uri> uris;
    Context c;
    ImageView expandedImageView ;
    PhotoTakenActivity takenActivity;

    View view;
    int mShortAnimationDuration = 100;
    private Animator mCurrentAnimator;

    public MyCameraTakeAdapter(Context c, ArrayList<Bitmap> bitmaps,ArrayList<Uri> uris,PhotoTakenActivity photoTakenActivity) {
        this.c = c;
        this.bitmaps = bitmaps;
        this.uris = uris;
        this.takenActivity = photoTakenActivity;
        expandedImageView = takenActivity.expandViewCamera;

    }
    @Override
    public GridItem2Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(c).inflate(R.layout.item_camera, parent, false);
        return new GridItem2Holder(view);
    }


    @Override
    public void onBindViewHolder(final GridItem2Holder holder, final int position) {

//        Picasso.with(c).load(data.get(position).getCoverimage()).into(holder.imageAlbum);

        //set image
        if (bitmaps!=null)
        {
//            holder.imageAlbum.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            holder.imageAlbum.setMaxWidth(300);
//            holder.imageAlbum.setMaxHeight(100);
//            Picasso.with(holder.imageAlbum.getContext())
//                    .load(uris.get(position))
//                    .resize(500, 500)
//                    .centerInside()
//                    .into(holder.imageAlbum);
            final Bitmap resized = Bitmap.createScaledBitmap(bitmaps.get(position),(int)(bitmaps.get(position).getWidth()*0.4), (int)(bitmaps.get(position).getHeight()*0.4), true);

            holder.photo.setImageBitmap(resized);
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //change frag
//                    Bundle i = new Bundle();
//                    i.putString("url", String.valueOf(uris.get(position)));
//                    ImageZoomFrag frag = new ImageZoomFrag();
//                    frag.setArguments(i);
//                    ((AppCompatActivity) c).getSupportFragmentManager().beginTransaction().replace(R.id.frag_photo, frag).addToBackStack(null).commit();

                    expandedImageView.setVisibility(View.VISIBLE);
//                    Animation animation = AnimationUtils.loadAnimation(c, R.anim.scale);
//                    expandedImageView.startAnimation(animation );
                    zoomImageFromThumb(holder.photo,position);
                }
            });


                holder.photo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new SweetAlertDialog(c, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes,delete it!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        File pictureSaveFolderPath = c.getExternalCacheDir();
                                        if (pictureSaveFolderPath!=null){
                                            File imageFiles[] = pictureSaveFolderPath.listFiles();
                                            if (imageFiles!=null){
                                                File displayImageFile = imageFiles[position];
                                                if (displayImageFile.exists())
                                                    displayImageFile.delete();
                                            }
                                        }

                                        bitmaps.remove(position);
                                        uris.remove(position);
                                        notifyDataSetChanged();
                                        sDialog
                                                .setTitleText("Deleted!")
                                                .setContentText("Your imaginary file has been deleted!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                        return false;
                    }
                });


        }


    }


    @Override
    public int getItemCount() {
        return bitmaps.size();
    }




    public static class GridItem2Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.camerImage)
        ImageView photo;

        public GridItem2Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    private void zoomImageFromThumb(View thumbView,int position) {
        // 如果有动画正在运行，取消这个动画
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }


        Picasso.with(c).load(uris.get(position)).fit().centerInside().noFade().into(expandedImageView);

        // 计算初始小图的边界位置和最终大图的边界位置。
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // 小图的边界就是小ImageView的边界，大图的边界因为是铺满全屏的，所以就是整个布局的边界。
        // 然后根据偏移量得到正确的坐标。
        thumbView.getGlobalVisibleRect(startBounds);
        view.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // 计算初始的缩放比例。最终的缩放比例为1。并调整缩放方向，使看着协调。
        float startScale=0;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // 横向缩放
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // 竖向缩放
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // 隐藏小图，并显示大图
//        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // 将大图的缩放中心点移到左上角。默认是从中心缩放
        expandedImageView.setPivotX(0.5f);
        expandedImageView.setPivotY(0.5f);

        //对大图进行缩放动画
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());

        set.start();
        mCurrentAnimator = set;

        // 点击大图时，反向缩放大图，然后隐藏大图，显示小图。
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
//                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}