package com.example.onpus.weddingpanda.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.chat_rec;
import com.example.onpus.weddingpanda.chatbotact;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InvitationFragment extends Fragment implements ai.api.AIListener {
    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn;
    DatabaseReference ref;
    FirebaseRecyclerAdapter<ChatMessage,chat_rec> adapter;
    Boolean flagFab = true;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private AIService aiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invitation, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        editText = (EditText)view.findViewById(R.id.editText);
        addBtn = (RelativeLayout)view.findViewById(R.id.addBtn);

        ButterKnife.bind(this,view);
        //initialiseView();
        return view;
    }

    @OnClick(R.id.chatbot)
    public void click(){
        startActivity(new Intent(getActivity(),chatbotact.class));
    }
    public void aiSetting(){
//        final AIConfiguration config = new AIConfiguration("CLIENT_ACCESS_TOKEN",
//                AIConfiguration.SupportedLanguages.English,
//                AIConfiguration.RecognitionEngine.System);
//
//        aiService = AIService.getService(getContext(), (ai.api.android.AIConfiguration) config);
//        aiService.setListener(new InvitationFragment());
//
//        final AIDataService aiDataService = new AIDataService(config);
//
//        final AIRequest aiRequest = new AIRequest();
    }
    private void initialiseView() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new    LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    ChatMessage chatMessage = new ChatMessage(message, "user");
                    ref.child("chat").child(currentUser.getUid()).push().setValue(chatMessage);

                }

                editText.setText("");

            }
        });


        adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(ChatMessage.class,R.layout.msglist,chat_rec.class,ref.child("chat").child(currentUser.getUid())) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {

                if (model.getMsgUser().equals("user")) {


                    viewHolder.rightText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                }
                else {
                    viewHolder.leftText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };


        recyclerView.setAdapter(adapter);

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

    public static class ChatMessage {

        private String msgText;
        private String msgUser;
        String msgImage;



        public ChatMessage(String msgText, String msgUser){
            this.msgText = msgText;
            this.msgUser = msgUser;

        }


        public ChatMessage(){

        }

        public String getMsgText() {
            return msgText;
        }

        public void setMsgText(String msgText) {
            this.msgText = msgText;
        }

        public String getMsgUser() {
            return msgUser;
        }

        public void setMsgUser(String msgUser) {
            this.msgUser = msgUser;
        }

        public String getMsgImage() {
            return msgImage;
        }

        public void setMsgImage(String msgImage) {
            this.msgImage = msgImage;
        }
    }

}
