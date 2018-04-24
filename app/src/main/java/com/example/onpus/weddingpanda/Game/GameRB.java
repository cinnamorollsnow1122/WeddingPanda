package com.example.onpus.weddingpanda.Game;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.AlbumAddUserAct;
import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.CustomDialogClassMsgWin;
import com.example.onpus.weddingpanda.constant.CustomDialogClassQr;
import com.example.onpus.weddingpanda.constant.CustomDialogClassRb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameRB extends AppCompatActivity {

    @BindView(R.id.next)
    Button next;
    @BindView(R.id.questionText)
    TextView question;
    @BindView(R.id.btn_optionA)
    Button optionA;
    @BindView(R.id.btn_optionB)
    Button optionB;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.questionNumber)
    TextView questionNumber;
//    @BindView(R.id.survivor)
//    TextView survivor;
    @BindView(R.id.loser)
    TextView loser;

    String type;
    Boolean ansCorrect = false;
    Boolean isoptionAclicked = false;
    Boolean isoptionBclicked = false;
    int i = 0;
    int numberQ = 1;
    protected ArrayList<QuestionAddRBActivity.QuestionRB> questionItems = new ArrayList<>();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    com.google.firebase.database.Query mQueryRB;
    String questionkey = "";
    CustomDialogClassMsgWin cddMsg;

    private ValueEventListener mListenerPress;
    private DatabaseReference mPressReferenceSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rb);
        ButterKnife.bind(this);
        //set toolbar
        GameRB.this.setSupportActionBar(toolbar);
        GameRB.this.setTitle("Red&Blue");
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
        cddMsg = new CustomDialogClassMsgWin(GameRB.this,"noth");
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
                //take list
                //show dialog which allow couple to know the guests answer situation
                CustomDialogClassRb cdd = new CustomDialogClassRb(GameRB.this);
                cdd.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
                    mQueryRB = db.child("Users").orderByChild("guest/" + userId).equalTo(false);
                    mQueryRB.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void updateSurvivor(final String userid) {
        db.child("Games").child(userid).child("Redblue").child("Loser").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int losernum = (int) dataSnapshot.getChildrenCount();


                TextView survivor = (TextView) findViewById(R.id.Textsurvivor);
                int survivorNum = Integer.valueOf(survivor.getText().toString())-losernum;
                survivor.setText(String.valueOf(survivorNum));
                loser.setText(String.valueOf(losernum));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void gameSet(final String userid) {
//        if (cddMsg.isShowing())
//            cddMsg.cancel();
        cddMsg = new CustomDialogClassMsgWin(GameRB.this,"noth");
        if (!isFinishing())
            cddMsg.show();
//        db.child("Games").child(userid).child("Redblue").child("AnswerGuest").addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final String correct;
//                int correntNum = 0;
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    for (DataSnapshot userAns : child.getChildren()) {
//                        if (userAns.getKey().equals(userId) && userAns.getValue().equals(true)) {
//                            correntNum++;
//                        }
//                    }
//                }
//                String correctNumber =  String.valueOf(correntNum);
//                correct = correctNumber;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                            if (!GameRB.this.isFinishing()){
//
////                                new SweetAlertDialog(GameRB.this)
////                                        .setTitleText("Game Set!")
////                                        .setContentText("You have " + correct + " questions right!")
////                                        .setConfirmText("OK")
////                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                            @Override
////                                            public void onClick(SweetAlertDialog sDialog) {
////                                                Intent pIntent = new Intent(GameRB.this, MainActivity.class);
////                                                pIntent.putExtra("Game", "Redblue");
////                                                startActivity(pIntent);
////                                                sDialog.dismissWithAnimation();
////                                                finish();
////                                            }
////                                        })
////                                        .show();
//
//
//                    }
//                }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void initialView(final String userid, final Boolean isGuest) {
        //init survivor and loser
        db.child("Games").child(userid).child("Redblue").child("waitingrm").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("num", String.valueOf((int) dataSnapshot.getChildrenCount()));

                String num = String.valueOf((int) dataSnapshot.getChildrenCount());
//            db.child("Games").child(userid).child("Redblue").child("survivor").setValue(num);
//            db.child("Games").child(userid).child("Redblue").child("loser ").setValue(0);
                TextView survivor = (TextView) findViewById(R.id.Textsurvivor);
                survivor.setText(num);
                String loserNum = "0";
                loser.setText("123");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


        //for question
        db.child("Games").child(userid).child("Redblue").child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                questionItems.clear();
                final Boolean[] correctAns = new Boolean[0];
                //display question
               //only clean if user is couple
                if (userid.equals(userId)){
                    //clean the loser
                    db.child("Games").child(userid).child("Redblue").child("Loser").removeValue();
                    //clean the answer sheet
                    db.child("Games").child(userid).child("Redblue").child("AnswerGuest").removeValue();

                }

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    QuestionAddRBActivity.QuestionRB temp = new QuestionAddRBActivity.QuestionRB();
                    temp.setAnswer(child.child("answer").getValue(String.class));
                    temp.setQuestion(child.child("question").getValue(String.class));
                    temp.setOptionA(child.child("optionA").getValue(String.class));
                    temp.setOptionB(child.child("optionB").getValue(String.class));
                    temp.setQuestionkey(child.getKey());
                    db.child("Games").child(userid).child("Redblue").child("Question").child(child.getKey()).child("read").removeValue();
//                    temp.setQuestionkey(child.child("next").getValue(String.class));

                    questionItems.add(temp);

                }
                final int size = questionItems.size();
                if (!questionItems.isEmpty() && (i == 0)) {
                    questionNumber.setText((numberQ) + "/" + (size));
                    question.setText(questionItems.get(0).getQuestion());
                    optionB.setText(questionItems.get(0).getOptionB());
                    optionA.setText(questionItems.get(0).getOptionA());
                    optionB.setBackgroundColor(getResources().getColor(R.color.blueB));
                    optionA.setBackgroundColor(getResources().getColor(R.color.redA));
                    questionkey = questionItems.get(0).getQuestionkey();

//                        db.child("Games").child(userid).child("Redblue").child("Question").child(questionItems.get(i).getQuestionkey()).child("next").setValue(true);
                    db.child("Games").child(userid).child("Redblue").child("Question").child(questionItems.get(0).getQuestionkey()).child("read").setValue(true);
                    db.child("Games").child(userid).child("Redblue").child("currentQuestionKey").setValue(questionkey);


                }

                //answer question
//                if (i < questionItems.size()) {
//
//                    optionA.setOnClickListener(new View.OnClickListener() {
//                        @SuppressLint("ResourceAsColor")
//                        @Override
//                        public void onClick(View view) {
//
//
//                            // if no option is chosen
//                            if (isoptionBclicked||isoptionAclicked){
//                                Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();
//                            }
//                            //after clicked change color and optiona clicked
//                            else{
//                                optionA.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
//                                isoptionAclicked = true;
//
//                            }
//                            //optionB is not clicked and optionA ans is correct
//                            if (!isoptionBclicked){
//                                optionA.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
//                                if(optionA.getText().equals(questionItems.get(i).getAnswer())){
//                                    ansCorrect = true;
//                                    db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(true);
//
//                                }
//                                else{
//                                    ansCorrect = false;
//                                    db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(false);
//                                    gameSet(userid);
//                                }
//                                isoptionAclicked = false;
//
//                            }
//
//
//                        }
//                    });
//                    optionB.setOnClickListener(new View.OnClickListener() {
//                        @SuppressLint("ResourceAsColor")
//                        @Override
//                        public void onClick(View view) {
//
//
//                            if (isoptionBclicked||isoptionAclicked){
//                                Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                optionB.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
//                                isoptionBclicked = true;
//
//
//                            }
//                            //optionA is not clicked and optionB ans is correct
//                            if (!isoptionAclicked){
//                                optionB.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
//                                if(optionB.getText().equals(questionItems.get(i).getAnswer())){
//                                    ansCorrect = true;
//                                    db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(true);
//                                }
//                                else{
//                                    ansCorrect = false;
//                                    db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(false);
//                                    gameSet(userid);
//                                }
//                                isoptionBclicked = false;
//
//                            }
//
//
//
//
//
//
//                        }
//                    });
//
//                }
                toNextQuestion2(userid, size);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        optionA.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {


                // if no option is chosen
                if (isoptionBclicked || isoptionAclicked) {
                    Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();
                }
                //after clicked change color and optiona clicked
                else {
                    optionA.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                    isoptionAclicked = true;

                }
                //optionB is not clicked and optionA ans is correct
                if (!isoptionBclicked) {
                    optionA.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                    checkans(userid, optionA.getText().toString());
//                        if(optionA.getText().equals(questionItems.get(i).getAnswer())){
//                            ansCorrect = true;
//                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(true);
//
//                        }
//                        else{
//                            ansCorrect = false;
//                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(false);
//                            gameSet(userid);
//                        }
                    isoptionAclicked = false;

                }


            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {


                if (isoptionBclicked || isoptionAclicked) {
                    Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();
                } else {
                    optionB.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                    isoptionBclicked = true;


                }
                //optionA is not clicked and optionB ans is correct
                if (!isoptionAclicked) {
                    optionB.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                    checkans(userid, optionB.getText().toString());

//                        if(optionB.getText().equals(questionItems.get(i).getAnswer())){
//                            ansCorrect = true;
//                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(true);
//                        }
//                        else{
//                            ansCorrect = false;
//                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(false);
//                            gameSet(userid);
//                        }
                    isoptionBclicked = false;

                }


            }
        });


    }

    public void toNextQuestion2(final String userid, final int size) {
        final Boolean falseM = false;
        final Boolean trueM = true;
        db.child("Games").child(userid).child("Redblue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if send is pressed
                final Boolean[] press = new Boolean[1];
                if (dataSnapshot.hasChild("press")) {
                    press[0] = trueM;
                    final DatabaseReference mPressReferencePress = db.child("Games").child(userid).child("Redblue").child("Question");
                    mListenerPress = new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (press[0]) {
                                checkansAfterPress(userid);
                                ArrayList<QuestionAddRBActivity.QuestionRB> questionUpdate = new ArrayList<>();
                                Boolean allread = true;

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    if (!child.hasChild("read")) {
                                        QuestionAddRBActivity.QuestionRB temp = new QuestionAddRBActivity.QuestionRB();
                                        temp.setAnswer(child.child("answer").getValue(String.class));
                                        temp.setQuestion(child.child("question").getValue(String.class));
                                        temp.setOptionA(child.child("optionA").getValue(String.class));
                                        temp.setOptionB(child.child("optionB").getValue(String.class));
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
                                        optionB.setBackgroundColor(getResources().getColor(R.color.blueB));
                                        optionA.setBackgroundColor(getResources().getColor(R.color.redA));

                                        questionkey = questionUpdate.get(0).getQuestionkey();
                                        question.setText(questionUpdate.get(0).getQuestion());
                                        optionA.setText(questionUpdate.get(0).getOptionA());
                                        optionB.setText(questionUpdate.get(0).getOptionB());
                                        db.child("Games").child(userid).child("Redblue").child("currentQuestionKey").setValue(questionkey);
                                        db.child("Games").child(userid).child("Redblue").child("Question").child(questionUpdate.get(0).getQuestionkey()).child("read").setValue(true);
                                    }else{
                                        setContentNextQuestion(userid);
                                    }

//                                db.child("Games").child(userid).child("Redblue").child(questionItems.get(currentpo).getQuestionkey()).child("next").setValue(true);
                                    db.child("Games").child(userid).child("Redblue").child("press").removeValue();
                                    press[0] = falseM;
                                    mPressReferencePress.removeEventListener(mListenerPress);

                                }
                                if (allread) {
                                    gameSet(userid);
                                }
                                //update the textview of surviorand loser
                                updateSurvivor(userid);
                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mPressReferencePress.addValueEventListener(mListenerPress);
                    db.child("Games").child(userid).child("Redblue").child("press").removeValue();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        }
    public void setContentNextQuestion(final String userid) {
        db.child("Games").child(userid).child("Redblue").child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentKey = dataSnapshot.getValue(String.class);
                db.child("Games").child(userid).child("Redblue").child("Question").child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QuestionAddRBActivity.QuestionRB temp = new QuestionAddRBActivity.QuestionRB();
                        temp.setAnswer(dataSnapshot.child("answer").getValue(String.class));
                        temp.setQuestion(dataSnapshot.child("question").getValue(String.class));
                        temp.setOptionA(dataSnapshot.child("optionA").getValue(String.class));
                        temp.setOptionB(dataSnapshot.child("optionB").getValue(String.class));
                        temp.setQuestionkey(dataSnapshot.getKey());

                        optionB.setBackgroundColor(getResources().getColor(R.color.blueB));
                        optionA.setBackgroundColor(getResources().getColor(R.color.redA));
//                                    optionB.setBackgroundColor(R.color.blueB);
//                                    optionA.setBackgroundColor(R.color.redA);
                        questionkey = temp.getQuestionkey();
                        question.setText(temp.getQuestion());
                        optionA.setText(temp.getOptionA());
                        optionB.setText(temp.getOptionB());

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


            public void checkans(final String userid, final String userans) {
        db.child("Games").child(userid).child("Redblue").child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentKey = dataSnapshot.getValue(String.class);
                db.child("Games").child(userid).child("Redblue").child("Question").child(currentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ans = dataSnapshot.child("answer").getValue(String.class);

                        if (userans.equals(ans)) {
                            ansCorrect = true;
                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(true);

                        } else {
                            ansCorrect = false;
                            db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(questionkey).child(userId).setValue(false);
//                            if(!userid.equals(userId))
//                                gameSet(userid);
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
    public void checkansAfterPress(final String userid) {
        db.child("Games").child(userid).child("Redblue").child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentKey = dataSnapshot.getValue(String.class);
                db.child("Games").child(userid).child("Redblue").child("AnswerGuest").child(currentKey).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            Boolean ansCorrect = dataSnapshot.getValue(Boolean.class);
                            if (!ansCorrect){
                                gameSet(userid);
                                db.child("Games").child(userid).child("Redblue").child("Loser").child(userId).setValue(true);

                            }
                        }
                        //if the guest didnt ans the question
//                        else{
//                            db.child("Games").child(userid).child("Redblue").child("Loser").child(userId).setValue(true);
//
//                        }

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
    @Override
    protected void onDestroy() {

        if (cddMsg != null) {
            if (cddMsg.isShowing()) {
                cddMsg.dismiss();
                cddMsg = null;
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        if (cddMsg != null) {
            if (cddMsg.isShowing()) {
                cddMsg.dismiss();
                cddMsg = null;
            }
        }
        super.onPause();
    }
}
