package com.example.onpus.weddingpanda.Game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameCustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_custom);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.addQuestionFloat)
    public void linktosignup(){
            startActivity(new Intent(GameCustomActivity.this, QuestionAddRBActivity.class));
        }


}
