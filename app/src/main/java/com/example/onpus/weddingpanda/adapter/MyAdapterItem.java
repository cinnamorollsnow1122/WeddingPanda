package com.example.onpus.weddingpanda.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.Scanner.PhotoTakenActivity;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.InAlbumitem;
import com.example.onpus.weddingpanda.fragment.Fragment_item_album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onpus on 2018/1/9.
 */

public class MyAdapterItem extends RecyclerView.Adapter<MyAdapterItem.GridItemHolder> {
    ArrayList<PhotoTakenActivity.NewAlbumItem> data;
    Bitmap[] bitmaps;
    Context c;
    View view;
    ImageView expandedImageView ;
    Fragment_item_album fragitem;
    int mShortAnimationDuration = 100;
    private Animator mCurrentAnimator;
    private DatabaseReference mFindUser= FirebaseDatabase.getInstance().getReference().child("Users");
    ;


    public MyAdapterItem(Context c, ArrayList<PhotoTakenActivity.NewAlbumItem> data, Fragment_item_album fragitem) {
        this.c = c;
        this.data = data;
        this.fragitem = fragitem;
        expandedImageView = fragitem.expandView;
    }
    @Override
    public GridItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         view = LayoutInflater.from(c).inflate(R.layout.item_photos, parent, false);
        return new GridItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final GridItemHolder holder, final int position) {
        Animation myFadeInAnimation =    AnimationUtils.loadAnimation(c , R.anim.fadein);

        Picasso.with(c).load(data.get(position).getImage()).into(holder.photo ,new Callback() {
            @Override
            public void onSuccess() {
                Animation myFadeInAnimation =    AnimationUtils.loadAnimation(c , R.anim.fadein);
                holder.photo.startAnimation(myFadeInAnimation);
            }

            @Override
            public void onError() {

            }
        });
//        Picasso.with(c).load(data.get(position).getImage()).into(holder.photo);
//        holder.photo.startAnimation(myFadeInAnimation);

        //find name
        //get userlist name
        mFindUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = "";
                for (DataSnapshot child2:dataSnapshot.getChildren()){
                    if (data.get(position).getUserlists()!=null){
                        for (String temp2:data.get(position).getUserlists()){
                            if(temp2.equals(child2.getKey())){
                                if (username.equals(""))
                                    username =  child2.child("name").getValue(String.class);
                                else
                                    username = username+" , " +child2.child("name").getValue(String.class);
                            }
                        }
                    }

                }
                if (username.equals("")){
                    holder.caption.setVisibility(view.GONE);
                }
                else
                holder.caption.setText("Member : "+ username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do on click stuff
                zoomImageFromThumb((View)holder.card,position);

            }
        });

//            try {
//                URL domain = new URL(data.get(position).getCoverimage());
//
//                Glide.with(c)
//                        .load(data.get(position).getCoverimage())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.imageAlbum);
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void zoomImageFromThumb(View thumbView,int position) {
        // 如果有动画正在运行，取消这个动画
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

//        Picasso.with(c).load(data.get(position).getImage()).into(expandedImageView);
        Picasso.with(c).load(data.get(position).getImage()).into(expandedImageView);

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
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        //对大图进行缩放动画
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());

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


    public static class GridItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivPhoto)
        ImageView photo;
        @BindView(R.id.ivComment)
        TextView caption;
        @BindView(R.id.photositem)
        CardView card;

        public GridItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
