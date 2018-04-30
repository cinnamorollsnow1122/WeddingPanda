package com.example.onpus.weddingpanda.Game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.constant.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class LotteryWheelAct extends AppCompatActivity {
    @BindView(R.id.rewardpage)
            View rewardpage;
    @BindView(R.id.getAwardCicle)
    ImageView awardIcon;
    @BindView(R.id.rewardUser)
    TextView rewardUser;
    @BindView(R.id.luckyWheel)
     LuckyWheelView luckyWheelView;
    final List<Target> targets = new ArrayList<>();
    List<LuckyItem> data = new ArrayList<>();
    private com.google.firebase.database.Query mQueryType;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    int index;
    final DatabaseReference newUser=ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("waitingrm");
    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lottery_wheel);
        ButterKnife.bind(this);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.text = "100";
        luckyItem1.icon = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
        luckyItem1.color = 0xffFFE0B2;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.text = "200";
        luckyItem2.icon = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        luckyItem2.color = 0xffFFF3E0;
        data.add(luckyItem2);


        checkguest("");


//
        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.text = "300";
        luckyItem2.icon = BitmapFactory.decodeResource(getResources(), R.drawable.test3);
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);


////

        luckyWheelView.setLuckyWheelBackgrouldColor(0xFF8BC34A);
        luckyWheelView.setLuckyWheelCenterImage(getResources().getDrawable(R.drawable.wheel));
        luckyWheelView.setLuckyWheelTextColor(0xFF424242);

        /*luckyWheelView.setLuckyWheelBackgrouldColor(0xff0000ff);
        luckyWheelView.setLuckyWheelTextColor(0xffcc0000);
//        luckyWheelView.setLuckyWheelCenterImage(getResources().getDrawable(R.drawable.icon));
        luckyWheelView.setLuckyWheelCursorImage(R.drawable.ic_cursor);*/

//        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //start the wheel
//                int index = getRandomIndex();
////                ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("index").setValue(index);
//                Log.d("daisie",  String.valueOf(index));
//
//                luckyWheelView.startLuckyWheelWithTargetIndex(index+1);
//
//            }
//        });

        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                int index2 = index-1;
                if(index2<0)
                    index2 = data.size()-1;
                Log.d("daisie2",  String.valueOf(index)+":"+ data.get(index2).text);

                Toast.makeText(LotteryWheelAct.this, String.valueOf(index)+":"+ data.get(index2).text, Toast.LENGTH_SHORT).show();
                awardIcon.setImageBitmap(data.get(index2).icon);
                rewardUser.setText("Congrat!"+ data.get(index2).text+" get the reward!");
                rewardpage.setVisibility(View.VISIBLE);


            }
        });


    }
    @OnClick(R.id.getAwardCicle)
    public void onClick(){
        checkguest("clean");
        Intent pIntent = new Intent(LotteryWheelAct.this, WaitingRmAct.class);
        pIntent.putExtra("Game","DrawCircle");
        startActivity(pIntent);
        finish();
        //clean the record
        //call the guest and clean the everything

    }

    //check guest
    public void checkguest(final String clean){
        //check if guest
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("Users").child(currentUser.getUid()).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                String id = currentUser.getUid();
                Log.d("typeA","guest");
                //check guest
                if(type.equals("guest")) {
                    findViewById(R.id.play).setVisibility(View.GONE);
                    mQueryType = ref.child("Users").orderByChild("guest/"+id).equalTo(false);
                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String coupleid = null;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                coupleid = child.getKey();
                            }

                            if(coupleid!=null){
                                    setupData(coupleid);


                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else {
                    findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //start the wheel
                            int index = getRandomIndex()+1;
                            ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("index").setValue(index);
//                            luckyWheelView.startLuckyWheelWithTargetIndex(index);

                        }
                    });
                    if (clean.equals("clean")){
                        cleanData(id);
                    }else{

                    setupData(id);

                }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //clean data
    public void cleanData(String id){
        ref.child("Games").child(id).child("DrawCircle").child("index").removeValue();
        ref.child("Games").child(id).child("DrawCircle").child("send").removeValue();

    }

    //check index
    public void checkIndex(String id){
        ref.child("Games").child(id).child("DrawCircle").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("index")) {
                        index =  dataSnapshot.child("index").getValue(Integer.class);
                        luckyWheelView.startLuckyWheelWithTargetIndex(index);


                }
//                    DataSnapshot childSnap = (DataSnapshot) dataSnapshot.getChildren();
//                    int index =  dataSnapshot.getChildren().child("send").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }


    //set up data
    public void setupData(final String id){
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Games").child(id).child("DrawCircle").child("waitingrm");

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ref2",ref2.toString());
                final ArrayList<String> waitingListid = new ArrayList<>();
                for (DataSnapshot childlist: dataSnapshot.getChildren()){

                        waitingListid.add(childlist.getKey());

                    Log.d("key",childlist.getKey());

                }

                if(!waitingListid.isEmpty()){
                    ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            data.clear();

                            ArrayList<User> userinfo = new ArrayList<>();
                            for (DataSnapshot childlist: dataSnapshot.getChildren()){
                                for(String id :waitingListid ){
                                    if (childlist.getKey().equals(id)){
                                        User temp = childlist.getValue(User.class);
                                        userinfo.add(temp);

                                    }
                                }

                            }
                            if(!userinfo.isEmpty()) {
                                int i =1;
                                int j =1;
                                for(User temp : userinfo){
                                    final LuckyItem luckyItem = new LuckyItem();
                                    luckyItem.text = temp.getName();


                                     mTarget = new Target() {
                                        @Override
                                        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                                            //Set it in the ImageView
                                            luckyItem.icon = bitmap;
                                            targets.remove(this);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                            Log.d("FailBit","fail");
                                            targets.remove(this);
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    };
                                    j++;
                                    targets.add(mTarget);
                                    Picasso.with(LotteryWheelAct.this)
                                            .load( temp.getUserPic())
                                            .resize(100, 100)
                                            .into(mTarget);

//                                    Glide
//                                            .with(getApplicationContext())
//                                            .load(userinfo.get(p).getUserPic())
//                                            .asBitmap()
//                                            .into(new SimpleTarget<Bitmap>(100,100) {
//                                                @Override
//                                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                                                                                                luckyItem.icon = resource;
//
//                                                }
//                                            });

                                    if(i%2==0)
                                        luckyItem.color = 0xFFE8F5E9;
                                    else{
                                        luckyItem.color = 0xFFFFAB91;

                                    }

                                    data.add(luckyItem);
                                    i++;

                                }


                                if (!data.isEmpty()) {
                                    luckyWheelView.setData(data);
                                    luckyWheelView.setRound(getRandomRound());
                                }

                            }
                            checkIndex(id);

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





    private int getRandomIndex() {
        Random rand = new Random();

        return rand.nextInt(data.size() - 1) + 0;
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //check guest
        final DatabaseReference newUser=ref.child("Games").child(currentUser.getUid()).child("DrawCircle").child("waitingrm");

//        ref.child("Users").child(currentUser.getUid()).child("userType").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String type = dataSnapshot.getValue(String.class);
//                String id = currentUser.getUid();
//                Log.d("typeA","guest");
//                //check guest
//                if(type.equals("guest")) {
//                    mQueryType = ref.child("Users").orderByChild("guest/"+id).equalTo(false);
//                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String coupleid = null;
//                            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                coupleid = child.getKey();
//                            }
//
//
//                            if(coupleid!=null){
//                                ref.child("Games").child(coupleid).child("DrawCircle").child("waitingrm").child(currentUser.getUid()).setValue(true);
//
//                            }else{
//
//                            }
//
//
//                        }
//
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }else{
//                    newUser.child(currentUser.getUid()).setValue("true");
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }



    }


