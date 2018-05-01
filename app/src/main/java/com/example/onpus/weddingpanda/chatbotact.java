package com.example.onpus.weddingpanda;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.onpus.weddingpanda.adapter.chat_rec;
import com.example.onpus.weddingpanda.fragment.InvitationFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import butterknife.BindView;

public class chatbotact extends AppCompatActivity implements AIListener{
    //view
    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn;
    @BindView(R.id.expanded_image_chat)
    ImageView expandedImageView;

    View view;

    //firebase
    DatabaseReference ref;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter<InvitationFragment.ChatMessage,chat_rec> adapter;
    //variable
    Boolean flagFab = true;

    private Animator mCurrentAnimator;


    private AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         view = getWindow().getDecorView().getRootView();
        setContentView(R.layout.activity_chatbotact);
        initView();
    }

    private void initView() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.editText);
        addBtn = (RelativeLayout)findViewById(R.id.addBtn);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);

        final ai.api.android.AIConfiguration config = new ai.api.android.AIConfiguration("680b44a66ed9435f981f6f986e0af034",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService;
        aiDataService = new AIDataService(this,config);

        final AIRequest aiRequest = new AIRequest();

        start(config);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(message, currentUser.getUid());
                    ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);

                    aiRequest.setQuery(message);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            new AsyncTask<AIRequest,Void,AIResponse>(){

                                @Override
                                protected AIResponse doInBackground(AIRequest... aiRequests) {
                                    final AIRequest request = aiRequests[0];
                                    try {
                                        final AIResponse response = aiDataService.request(aiRequest);
                                        return response;
                                    } catch (AIServiceException e) {
                                    }
                                    return null;
                                }
                                @Override
                                protected void onPostExecute(AIResponse response) {
                                    if (response != null) {

                                        Result result = response.getResult();

                                        String reply = result.getFulfillment().getSpeech();
                                        //check requirement in database
                                        String coupleid = "";
                                        String bot = currentUser.getUid()+": bot";
                                        for (DataSnapshot child : dataSnapshot.child("Users").child(currentUser.getUid()).child("couple").getChildren()) {
                                            coupleid = child.getKey();
                                            break;
                                        }
                                        if (reply.contains(" going to have their wedding")){

                                            //find couple name
                                            String name = dataSnapshot.child("Users").child(coupleid).child("name").getValue(String.class);
                                                if (name!=null){
                                                    String hello = reply.replace("XXX",name);
                                                    String coverimage = dataSnapshot.child("WeddingInfo").child(coupleid).child("coverimage").getValue(String.class);

                                                    InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(hello, bot);
                                                    chatMessage.setMsgImage(coverimage);
                                                    ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                                }

                                        }else if(reply.contains("wedding will take place in")){
                                            //find venue
                                            String venue = dataSnapshot.child("WeddingInfo").child(coupleid).child("venue").getValue(String.class);
                                            if (venue!=null){
                                                String hello = reply.replace("XXX",venue);
                                                InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(hello, bot);
                                                ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                            }

                                        }else if(reply.contains("wedding will be held on")){
                                            String date = dataSnapshot.child("WeddingInfo").child(coupleid).child("date").getValue(String.class);
                                            if (date!=null){
                                                String hello = reply.replace("XXX",date);
                                                InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(hello, bot);
                                                ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                            }

                                        }else if(reply.contains("Thank you! Do you have"))
                                        {
                                            ref.child("Users").child(currentUser.getUid()).child("couple").child(coupleid).setValue(true);
                                            ref.child("Users").child(coupleid).child("guest").child(currentUser.getUid()).setValue(true);
                                            InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(reply, bot);
                                            ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                        }else if(reply.contains("Ok......I will tell the couple!")||reply.contains("I am sorry to hear that......I will make")){
                                            //guest refuse to go the wedding

                                        }
                                        else if(reply.contains("You will sit ")||reply.contains("You table number")){
                                            //show the seat plan
                                            String seat =  dataSnapshot.child("WeddingInfo").child(coupleid).child("seatImage").getValue(String.class);
                                            String seatid =  dataSnapshot.child("Users").child(currentUser.getUid()).child("Tableno").getValue(String.class);
                                            if (seat != null&&seatid!=null){
                                                 reply = reply.replace("XX",seatid);
                                                InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(reply, bot);
                                                chatMessage.setMsgImage(seat);
                                                ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);


                                            }else{
                                                 reply = "I am sorry! the couple hasnt released the seating plan!";
                                                InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(reply, bot);
                                                ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);

                                            }

                                        }
                                        else{

                                            InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(reply, bot);
                                            ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                        }


                                    }
                                }
                            }.execute(aiRequest);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    aiService.startListening();
                }

                editText.setText("");

            }
        });



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView)findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length()!=0 && flagFab){
                    ImageViewAnimatedChange(chatbotact.this,fab_img,img);
                    flagFab=false;

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(chatbotact.this,fab_img,img1);
                    flagFab=true;

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
        });

        adapter = new FirebaseRecyclerAdapter<InvitationFragment.ChatMessage, chat_rec>(InvitationFragment.ChatMessage.class,R.layout.msglist,chat_rec.class,ref.child("chat").child(currentUser.getUid())) {
            @Override
            protected void populateViewHolder(final chat_rec viewHolder,  InvitationFragment.ChatMessage model, final int position) {
                String bot = currentUser.getUid()+": bot";

                if (model.getMsgUser().equals(currentUser.getUid())) {


                    viewHolder.rightText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                    viewHolder.leftImage.setVisibility(View.GONE);
                    viewHolder.rightImage.setVisibility(View.GONE);
                }
                else if (model.getMsgUser().equals(bot)){
                    viewHolder.leftText.setText(model.getMsgText());
                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                    viewHolder.leftImage.setVisibility(View.GONE);
                    viewHolder.rightImage.setVisibility(View.GONE);

                    if (model.getMsgImage()!=null){
                        viewHolder.leftImage.setVisibility(View.VISIBLE);
                        Picasso.with(chatbotact.this)
                                .load(model.getMsgImage())
                                .into(viewHolder.leftImage);
                        final String url = model.getMsgImage();

                        viewHolder.leftImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Do on click stuff
                                zoomImageFromThumb(viewHolder.leftImage,url,position);

                            }
                        });

                    }

                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);

                }

            }
        });

        recyclerView.setAdapter(adapter);


    }

    private void start(AIConfiguration config) {
       final  AIRequest startaiRequest = new AIRequest();
       final AIDataService aiDataService = new AIDataService(this,config);
       ref.child("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.hasChild("couple")){
                   ref.child("chat").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           if (dataSnapshot.getValue()==null){
                               startaiRequest.setQuery("WELCOME");
                               final String bot = currentUser.getUid()+": bot";

                               new AsyncTask<AIRequest,Void,AIResponse>(){

                                   @Override
                                   protected AIResponse doInBackground(AIRequest... aiRequests) {
                                       final AIRequest request = aiRequests[0];
                                       try {
                                           final AIResponse response = aiDataService.request(startaiRequest);
                                           return response;
                                       } catch (AIServiceException e) {
                                       }
                                       return null;
                                   }
                                   @Override
                                   protected void onPostExecute(AIResponse response) {
                                       if (response != null) {
                                           Result result = response.getResult();
                                           String reply = result.getFulfillment().getSpeech();
                                           InvitationFragment.ChatMessage chatMessage = new InvitationFragment.ChatMessage(reply, bot);
                                           ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);
                                       }
                                   }
                               }.execute(startaiRequest);
                           }
                       }


                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
    @Override
    public void onResult(AIResponse result) {

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    private void zoomImageFromThumb(View thumbView, String url, int position) {
        // 如果有动画正在运行，取消这个动画
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

//        Picasso.with(c).load(data.get(position).getImage()).into(expandedImageView);

        final ImageView expandedImageView  = findViewById(R.id.expanded_image_chat);
        expandedImageView.setVisibility(View.VISIBLE);
        Picasso.with(chatbotact.this).load(url).into(expandedImageView);

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
        final int mShortAnimationDuration = 100;
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
}
