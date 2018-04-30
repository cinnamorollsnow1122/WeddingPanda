package com.example.onpus.weddingpanda.Game.gamePhoto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.Game.GameCustomActivity;
import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GamePhotoCustomAct extends AppCompatActivity {
    @BindView(R.id.questionListPH)
    ListView questionListV;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_photo_custom);
        ButterKnife.bind(this);
        initialView();
    }

    @OnClick(R.id.addphotoQ)
    public void linktosignup(){
        startActivity(new Intent(GamePhotoCustomAct.this, QuestionAddPhotoactivit.class));
    }

    public void initialView(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        DatabaseReference mReferenceQuestion = db.child("Games").child(userId).child("PhotoGame").child("Question");
        mReferenceQuestion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList< QuestionAddPhotoactivit.NewPhotoQuesiton> questionList = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                        QuestionAddPhotoactivit.NewPhotoQuesiton temp = new  QuestionAddPhotoactivit.NewPhotoQuesiton();
                        temp.setAnswer(child.child("answer").getValue(String.class));
                        temp.setQuestion(child.child("question").getValue(String.class));
                        temp.setImageA(child.child("imageA").getValue(String.class));
                        temp.setImageB(child.child("imageB").getValue(String.class));
                        temp.setQuestionkey(child.getKey());
//                    temp.setQuestionkey(child.child("next").getValue(String.class));
                        questionList.add(temp);

                }
                if(questionList!=null) {
                    GamePhotoCustomAct.DataListAdapter guestListAdapter = new GamePhotoCustomAct.DataListAdapter(GamePhotoCustomAct.this, questionList);
                    questionListV.setAdapter(guestListAdapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    class DataListAdapter extends BaseAdapter {
        ArrayList<QuestionAddPhotoactivit.NewPhotoQuesiton> questionList = new ArrayList<>();
        Context c ;
        DataListAdapter() {
            questionList = null;

        }

        public DataListAdapter( Context c,ArrayList<QuestionAddPhotoactivit.NewPhotoQuesiton> questionList) {
            this.questionList = questionList;
            this.c = c;


        }

        public int getCount() {
            // TODO Auto-generated method stub
            return questionList.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_item_customgame, parent, false);
            final TextView qstitle;

            qstitle=(TextView)row.findViewById(R.id.questionItem);
            qstitle.setText(questionList.get(position).getQuestion());

            return (row);
        }
    }

}
