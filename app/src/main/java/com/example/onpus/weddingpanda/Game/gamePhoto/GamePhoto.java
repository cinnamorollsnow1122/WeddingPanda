package com.example.onpus.weddingpanda.Game.gamePhoto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onpus.weddingpanda.Game.GameRB;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.CustomDialogClassRb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Bitmap.createBitmap;

public class GamePhoto extends AppCompatActivity{

    //declare

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gameSet)
    CardView gamesetView;
    @BindView(R.id.cardView)
    CardView questionCard;
    @BindView(R.id.msgPhoto)
    TextView msgPhoto;
    @BindView(R.id.scoreValue)
    TextView scoreValue;
        @BindView(R.id.questionContent)
        TextView question;
        @BindView(R.id.photoA)
        ImageView imageA;
        @BindView(R.id.photoB)
        ImageView imageB;

        @BindView(R.id.questionNumber)
        TextView questionNumber;

        protected ArrayList<QuestionAddPhotoactivit.NewPhotoQuesiton> questionItems = new ArrayList<>();
        String questionkey = "";
        String type;
    Boolean isImageAClicked = false;
    Boolean isImageBClicked = false;

        int i = 0;
        int numberQ = 1;

        //firebase
         String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
         DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        com.google.firebase.database.Query mQueryGamePhoto;

    private ValueEventListener mListenerPress;
    private DatabaseReference mPressReferenceSend;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game_photo);
            ButterKnife.bind(this);

            //set toolbar
            GamePhoto.this.setSupportActionBar(toolbar);
            GamePhoto.this.setTitle("PhotoGame");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed(); // Implemented by activity
                }
            });
            Bundle b = getIntent().getExtras();
            if (b != null) {
                type = b.getString("type");
            } else Log.i("BUNDLE", "Null");


            //check guest and initialize view
            checkGuest();
        }

        public void checkGuest() {
            //check guest
            final String[] coupleid = new String[1];

            db.child("Users").child(userId).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    type = dataSnapshot.getValue(String.class);
                    Log.d("typeA", "guest");
                    //red blue
                    if (!type.equals("guest")) {
                        //dialog for custom / start
//                        next.setVisibility(View.VISIBLE);
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

                        initialView(userId, type.equals("guest"));
//                        toNextQuestion(userId);

                    } else {
                        mQueryGamePhoto = db.child("Users").orderByChild("guest/" + userId).equalTo(true);
                        mQueryGamePhoto.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    coupleid[0] = child.getKey();
                                    Log.d("coup;eid", coupleid[0]);
                                }
                                initialView(coupleid[0], type.equals("guest"));
//                                toNextQuestion(coupleid[0]);

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

        public void initialView(final String userid, final Boolean isGuest) {
            questionCard.setVisibility(View.VISIBLE);
            gamesetView.setVisibility(View.GONE);
            //gameset
            db.child("Games").child(userid).child("PhotoGame").child("GameSet").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){
                        gameSet(userid);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //for question
            db.child("Games").child(userid).child("PhotoGame").child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    questionItems.clear();
                    final Boolean[] correctAns = new Boolean[0];
                    //display question
                    //only clean if user is couple
                    if (userid.equals(userId)){
                        db.child("Games").child(userid).child("PhotoGame").child("AnswerGuest").removeValue();
                        db.child("Games").child(userid).child("PhotoGame").child("Score").removeValue();
                        db.child("Games").child(userid).child("PhotoGame").child("GameSet").removeValue();

                    }

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        QuestionAddPhotoactivit.NewPhotoQuesiton temp = new QuestionAddPhotoactivit.NewPhotoQuesiton();
                        temp.setAnswer(child.child("answer").getValue(String.class));
                        temp.setQuestion(child.child("question").getValue(String.class));
                        temp.setImageA(child.child("imageA").getValue(String.class));
                        temp.setImageB(child.child("imageB").getValue(String.class));
                        temp.setQuestionkey(child.getKey());
                        db.child("Games").child(userid).child("PhotoGame").child("Question").child(child.getKey()).child("read").removeValue();
//                    temp.setQuestionkey(child.child("next").getValue(String.class));

                        questionItems.add(temp);

                    }
                    final int size = questionItems.size();
                    if (!questionItems.isEmpty() && (i == 0)) {
                        questionNumber.setText((numberQ) + "/" + (size));
                        question.setText(questionItems.get(0).getQuestion());
                        Picasso.with(GamePhoto.this).load(questionItems.get(0).getImageA()).into(imageA);
                        Picasso.with(GamePhoto.this).load(questionItems.get(0).getImageB()).into(imageB);
//                        optionB.setBackgroundColor(getResources().getColor(R.color.blueB));
//                        optionA.setBackgroundColor(getResources().getColor(R.color.redA));
                        questionkey = questionItems.get(0).getQuestionkey();

                        db.child("Games").child(userid).child("PhotoGame").child("Question").child(questionItems.get(0).getQuestionkey()).child("read").setValue(true);
                        db.child("Games").child(userid).child("PhotoGame").child("currentQuestionKey").setValue(questionkey);


                    }



                    toNextQuestion2(userid, size);




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        //imageClick
            imageA.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    // if no option is chosen
                    if(!isImageAClicked&&!isImageBClicked){
                        isImageAClicked = true;
                        Animation scaleDown = AnimationUtils.loadAnimation(GamePhoto.this, R.anim.juimp);
                        imageA.startAnimation(scaleDown);
                        imageA.setAlpha(100);
                         checkans(userid, "imageA");

                    }else{
                        Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();

                    }



                }
            });
            imageB.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {

                    if(!isImageAClicked&&!isImageBClicked){
                        isImageBClicked = true;
                        Animation scaleDown = AnimationUtils.loadAnimation(GamePhoto.this, R.anim.juimp);
                        imageB.startAnimation(scaleDown);
                        imageB.setAlpha(100);
                        checkans(userid, "imageB");


                    }else{
                        Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();

                    }


                }
            });

        }

    public void checkans(final String userid, final String userans) {


        db.child("Games").child(userid).child("PhotoGame").child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentKey = dataSnapshot.getValue(String.class);
                db.child("Games").child(userid).child("PhotoGame").child("Question").child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ans = dataSnapshot.child("answer").getValue(String.class);

                        if (userans.equals(ans)) {
//                            ansCorrect = true;
                            db.child("Games").child(userid).child("PhotoGame").child("AnswerGuest").child(questionkey).child(userId).setValue(true);
                            db.child("Games").child(userid).child("PhotoGame").child("Score").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild(userId))
                                        db.child("Games").child(userid).child("PhotoGame").child("Score").child(userId).setValue(100);
                                    else{
                                        int score = dataSnapshot.child(userId).getValue(Integer.class);
                                        score = score +100;
                                        db.child("Games").child(userid).child("PhotoGame").child("Score").child(userId).setValue(score);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {

                            db.child("Games").child(userid).child("PhotoGame").child("AnswerGuest").child(questionkey).child(userId).setValue(false);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void toNextQuestion2(final String userid, final int size) {
        final Boolean falseM = false;
        final Boolean trueM = true;
        final String[] questionKeyLast = new String[1];
        db.child("Games").child(userid).child("PhotoGame").child("press").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if send is pressed
                final Boolean[] press = new Boolean[1];
                if (dataSnapshot.getValue()!=null) {
                    press[0] = trueM;
                    if (userid.equals(userId)) {

//                        db.child("Games").child(userid).child("PhotoGame").child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener() {
//
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                String currentKey = dataSnapshot.getValue(String.class);
//                                db.child("Games").child(userid).child("PhotoGame").child("Question").child(currentKey).child("read").setValue(true);
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
                    }
                    final DatabaseReference mPressReferencePress = db.child("Games").child(userid).child("PhotoGame").child("Question");
                    mListenerPress = new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (press[0]) {

                                updatescore(userid);
                                isImageAClicked = false;
                                isImageBClicked = false;
                                imageA.setAlpha(255);
                                imageB.setAlpha(255);
//                                db.child("Games").child(userid).child("PhotoGame").child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
//
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        ArrayList< QuestionAddPhotoactivit.NewPhotoQuesiton> questionUpdate = new ArrayList<>();
//
//                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                            if (!child.hasChild("read")) {
//                                                QuestionAddPhotoactivit.NewPhotoQuesiton temp = new QuestionAddPhotoactivit.NewPhotoQuesiton();
//                                                temp.setAnswer(child.child("answer").getValue(String.class));
//                                                temp.setQuestion(child.child("question").getValue(String.class));
//                                                temp.setImageA(child.child("imageA").getValue(String.class));
//                                                temp.setImageB(child.child("imageB").getValue(String.class));
//                                                temp.setQuestionkey(child.getKey());
////                    temp.setQuestionkey(child.child("next").getValue(String.class));
//                                                questionUpdate.add(temp);
//                                                String currentKey = temp.getQuestionkey();
//                                                db.child("Games").child(userid).child("PhotoGame").child("Question").child(currentKey).child("read").setValue(true);
//
//                                                break;
//
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }});


                                //get current key and set read

                                ArrayList< QuestionAddPhotoactivit.NewPhotoQuesiton> questionUpdate = new ArrayList<>();
                                Boolean allread = true;

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    if (!child.hasChild("read")) {
                                        QuestionAddPhotoactivit.NewPhotoQuesiton temp = new QuestionAddPhotoactivit.NewPhotoQuesiton();
                                        temp.setAnswer(child.child("answer").getValue(String.class));
                                        temp.setQuestion(child.child("question").getValue(String.class));
                                        temp.setImageA(child.child("imageA").getValue(String.class));
                                        temp.setImageB(child.child("imageB").getValue(String.class));
                                        temp.setQuestionkey(child.getKey());
//                    temp.setQuestionkey(child.child("next").getValue(String.class));
                                        questionUpdate.add(temp);
                                        allread = false;
                                    }
                                }

                                if (!questionUpdate.isEmpty()) {
                                    numberQ++;
                                    questionNumber.setText((numberQ) + "/" + size);
                                    if (userid.equals(userId)){

                                        questionkey = questionUpdate.get(0).getQuestionkey();
                                        question.setText(questionUpdate.get(0).getQuestion());
                                        try {
                                            Picasso.with(GamePhoto.this).load(questionUpdate.get(0).getImageA()).into(imageA);
                                         Picasso.with(GamePhoto.this).load(questionUpdate.get(0).getImageB()).into(imageB);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                                        Picasso.with(GamePhoto.this).load(questionItems.get(0).getImageA()).into(imageA);
//                                        Picasso.with(GamePhoto.this).load(questionItems.get(0).getImageB()).into(imageB);
//                                        if (numberQ ==size-1){
//                                             questionKeyLast[0] = questionkey;
//                                        }
                                        db.child("Games").child(userid).child("PhotoGame").child("currentQuestionKey").setValue(questionkey);
                                        db.child("Games").child(userid).child("PhotoGame").child("Question").child(questionkey).child("read").setValue(true);
                                    }else{
//                                        questionkey = questionUpdate.get(0).getQuestionkey();
//                                        question.setText(questionUpdate.get(0).getQuestion());
//                                        try {
//                                            Picasso.with(GamePhoto.this).load(questionUpdate.get(0).getImageA()).into(imageA);
//                                            Picasso.with(GamePhoto.this).load(questionUpdate.get(0).getImageB()).into(imageB);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        Pic
                                        setContentNextQuestion(userid);
                                    }

//                                db.child("Games").child(userid).child("Redblue").child(questionItems.get(currentpo).getQuestionkey()).child("next").setValue(true);
                                    db.child("Games").child(userid).child("PhotoGame").child("press").removeValue();
                                    press[0] = falseM;
                                     setContentNextQuestion(userid);

                                    mPressReferencePress.removeEventListener(mListenerPress);

                                }

                                if (allread) {
                                    if (userid!=userId){
                                        numberQ++;
                                        questionNumber.setText((numberQ) + "/" + size);
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            questionUpdate.clear();
                                            if (child.hasChild("read")) {
                                                QuestionAddPhotoactivit.NewPhotoQuesiton temp = new QuestionAddPhotoactivit.NewPhotoQuesiton();
                                                temp.setAnswer(child.child("answer").getValue(String.class));
                                                temp.setQuestion(child.child("question").getValue(String.class));
                                                temp.setImageA(child.child("imageA").getValue(String.class));
                                                temp.setImageB(child.child("imageB").getValue(String.class));
                                                temp.setQuestionkey(child.getKey());
//                    temp.setQuestionkey(child.child("next").getValue(String.class));
                                                questionUpdate.add(temp);
                                            }
                                        }
                                        int size2 = questionUpdate.size();
                                            question.setText(questionUpdate.get(size2-1).getQuestion());
                                            Picasso.with(GamePhoto.this).load(questionUpdate.get(size2-1).getImageA()).into(imageA);
                                            Picasso.with(GamePhoto.this).load(questionUpdate.get(size2-1).getImageB()).into(imageB);

                                    }else{
                                        db.child("Games").child(userid).child("PhotoGame").child("GameSet").setValue(true);
                                        //gameSet(userid);

                                    }

                                }
                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mPressReferencePress.addValueEventListener(mListenerPress);
                    db.child("Games").child(userid).child("PhotoGame").child("press").removeValue();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void updatescore(final String userid){

                            db.child("Games").child(userid).child("PhotoGame").child("Score").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child: dataSnapshot.getChildren()){
                                        if (child.getKey().equals(userId)){
                                            int add2 = child.getValue(Integer.class);

                                            scoreValue.setText(Integer.toString(add2));
                                        }

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


    public void setContentNextQuestion(final String userid) {
        db.child("Games").child(userid).child("PhotoGame").child("currentQuestionKey").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentKey = dataSnapshot.getValue(String.class);
                db.child("Games").child(userid).child("PhotoGame").child("Question").child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QuestionAddPhotoactivit.NewPhotoQuesiton temp = new QuestionAddPhotoactivit.NewPhotoQuesiton();
                        temp.setAnswer(dataSnapshot.child("answer").getValue(String.class));
                        temp.setQuestion(dataSnapshot.child("question").getValue(String.class));
                        temp.setImageA(dataSnapshot.child("imageA").getValue(String.class));
                        temp.setImageB(dataSnapshot.child("imageB").getValue(String.class));
                        temp.setQuestionkey(dataSnapshot.getKey());

                        questionkey = temp.getQuestionkey();
                        question.setText(temp.getQuestion());
                        Picasso.with(GamePhoto.this).load(temp.getImageA()).into(imageA);
                        Picasso.with(GamePhoto.this).load(temp.getImageB()).into(imageB);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void gameSet(final String userid){
        questionCard.setVisibility(View.GONE);
        gamesetView.setVisibility(View.VISIBLE);
        if (!userid.equals(userId)){
            db.child("Games").child(userid).child("PhotoGame").child("Score").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null){
                        int score = dataSnapshot.getValue(Integer.class);
                        msgPhoto.setText("Your score is "+score);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{

            db.child("Games").child(userid).child("PhotoGame").child("Score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int highest =0; final ArrayList<String> useridWinner = new ArrayList();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        int score = child.getValue(Integer.class);
                        if (score >=highest){
                            highest = score;
                        }


                        }
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        int score = child.getValue(Integer.class);
                        if (score == highest) {
                            useridWinner.add(child.getKey());
                        }
                    }   final int hig = highest;
//                        mQueryGamePhoto = db.child("Users").orderByChild("guest/" + userId).equalTo(false);
                        db.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    for(String userid: useridWinner){
                                        if (child.getKey().equals(userid)){
                                            String name = child.child("name").getValue(String.class);
                                            msgPhoto.setText("Winner is "+name+ ", Score:"+Integer.toString(hig)+"");
                                            break;

                                        }

                                    }
                                }


                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
    //menu

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        //check if guest
        db.child("Users").child(userId).child("userType").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                String id = userId;
                Log.d("typeA", "guest");
                //check guest
                if (type.equals("guest")) {

                } else {
                    getMenuInflater().inflate(R.menu.gamemenu, menu);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            //check guest

            switch (item.getItemId()) {

                case R.id.action_send:
                    Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                    //take list根
                    //show dialog which allow couple to know the guests answer situation

                    CustomDialogClassRb cdd = new CustomDialogClassRb(GamePhoto.this,"PhotoGame");
                    cdd.show();
//                    db.child("Games").child(userId).child("PhotoGame").child("press").setValue(true);
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);

            }
        }


    @OnClick(R.id.backphoto)
    public void onClick(){
        Intent pIntent = new Intent(GamePhoto.this, MainActivity.class);
//    pIntent.putExtra("Game", "Redblue");
        GamePhoto.this.finish();
        GamePhoto.this.startActivity(pIntent);

    }

}
