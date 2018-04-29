package com.example.onpus.weddingpanda.Game.gamePhoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.onpus.weddingpanda.Game.GameCustomActivity;
import com.example.onpus.weddingpanda.QuestionAddRBActivity;
import com.example.onpus.weddingpanda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GamePhotoCustomAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_photo_custom);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.addphotoQ)
    public void linktosignup(){
        startActivity(new Intent(GamePhotoCustomAct.this, QuestionAddPhotoactivit.class));
    }

}
