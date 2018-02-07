package com.example.onpus.weddingpanda.Game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.google.firebase.auth.FirebaseAuth;
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
    String type;
    Boolean ansCorrect = false;
    Boolean isoptionAclicked = false;
    Boolean isoptionBclicked = false;
    int i = 0;
    protected ArrayList<QuestionAddRBActivity.QuestionRB> questionItems = new ArrayList<>();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    com.google.firebase.database.Query mQueryRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rb);
        ButterKnife.bind(this);
        checkGuest();

    }

    public void checkGuest(){
        //check guest
        final String[] coupleid = new String[1];

        db.child("Users").child(userId).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue(String.class);
                Log.d("typeA","guest");
                //red blue
                    if(!type.equals("guest")){
                        //dialog for custom / start
                        next.setVisibility(View.VISIBLE);
                        initialView(userId);

                    }else{
                        mQueryRB = db.child("Users").orderByChild("guest/"+userId).equalTo(false);
                        mQueryRB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    coupleid[0] = child.getKey();
                                    Log.d("coup;eid",coupleid[0]);
                                }
                                initialView(coupleid[0]);

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

    public void initialView(final String userid){


        db.child("Games").child(userid).child("Redblue").child("Question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                questionItems.clear();
                final Boolean[] correctAns = new Boolean[0];

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    QuestionAddRBActivity.QuestionRB temp = new QuestionAddRBActivity.QuestionRB();
                    temp.setAnswer(child.child("answer").getValue(String.class));
                    temp.setQuestion(child.child("question").getValue(String.class));
                    temp.setOptionA(child.child("optionA").getValue(String.class));
                    temp.setOptionB(child.child("optionB").getValue(String.class));
                    questionItems.add(temp);

                    }
                    if (!questionItems.isEmpty()){
                        question.setText(questionItems.get(i).getQuestion());
                        optionA.setText(questionItems.get(i).getOptionA());
                        optionB.setText(questionItems.get(i).getOptionB());
                    }

                optionA.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View view) {
                        if (!isoptionBclicked&&optionA.getText().equals(questionItems.get(i).getAnswer())){
                            ansCorrect = true;
                            }
                            else if(isoptionBclicked){
                            Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();
                        }
                        optionA.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                        isoptionAclicked = true;
                    }
                });
                optionB.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View view) {
                        isoptionBclicked = true;
                        if (!isoptionAclicked&&optionB.getText().equals(questionItems.get(i).getAnswer())){
                            ansCorrect = true;
                            optionB.setBackgroundColor(R.color.common_google_signin_btn_text_dark_pressed);
                        }
                        else if(isoptionAclicked){
                            Toast.makeText(getApplicationContext(), "You have already chosen your answer", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
//                //check ans

                if (next.getVisibility() == View.VISIBLE) {
                    // Its visible(couple)
                    next.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View view) {

                            i++;
                            if (questionItems.size()>i) {
                                optionB.setBackgroundColor(R.color.blueB);
                                optionA.setBackgroundColor(R.color.redA);
                                question.setText(questionItems.get(i).getQuestion());
                                optionA.setText(questionItems.get(i).getOptionA());
                                optionB.setText(questionItems.get(i).getOptionB());
                                db.child("Games").child(userid).child("Redblue").child("Next").setValue(true);
                            }
                        }
                    });
                } else {
                    // Either gone or invisible guest view
                    db.child("Games").child(userid).child("Redblue").child("Next").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean checkNext = dataSnapshot.getValue(Boolean.class);
                            if (checkNext){
                                //check ans
                                if (ansCorrect){
                                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();

//                                    new SweetAlertDialog(getApplicationContext())
//                                            .setTitleText("Great Answer!")
//                                            .show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();

//                                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
//                                            .setTitleText("Lose!")
//                                            .setContentText("I am sorry, you get it wrong!")
//                                            .show();
                                }
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


}
