package com.example.onpus.weddingpanda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.onpus.weddingpanda.constant.Budget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionAddRBActivity extends AppCompatActivity {
    @BindView(R.id.getQuestion)
    EditText getQuestion;
    @BindView(R.id.getOptionA)
    EditText getOptionA;
    @BindView(R.id.getOptionB)
    EditText getOptionB;
    @BindView(R.id.getAns)
    EditText getAns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add_rb);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.submitQuestion)
    public void OnClick(){
        newQuestion();
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sucessful!")
                .show();
        getQuestion.setText("");
        getOptionA.setText("");
        getOptionB.setText("");
        getAns.setText("");
    }

    public void newQuestion(){
        //add album
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newItem=ref.child("Games").child(currentUser.getUid()).child("Redblue").child("Question").push();
        final String[] pushkey = {""};

//        pushkey[0] = newAlbum.getKey();
        //add item to database
        newItem.setValue(new QuestionRB(getQuestion.getText().toString(),
                getOptionA.getText().toString(), getOptionB.getText().toString(),getAns.getText().toString()));

    }

    public static class QuestionRB{
        public String getQuestionkey() {
            return Questionkey;
        }

        public void setQuestionkey(String questionkey) {
            Questionkey = questionkey;
        }

        private String Questionkey;
        private String question;
        private String optionA;
        private String optionB;

        public Boolean getNext() {
            return next;
        }

        public void setNext(Boolean next) {
            this.next = next;
        }

        private Boolean next;

        private String answer;

        public QuestionRB(){

        }

        public QuestionRB(String question, String optionA, String optionB,String answer) {
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.answer = answer;
        }



        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getOptionA() {
            return optionA;
        }

        public void setOptionA(String optionA) {
            this.optionA = optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public void setOptionB(String optionB) {
            this.optionB = optionB;
        }
    }
}
