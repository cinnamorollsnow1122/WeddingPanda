package com.example.onpus.weddingpanda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.Game.GameCustomActivity;
import com.example.onpus.weddingpanda.Game.GameRB;
import com.example.onpus.weddingpanda.Game.LotteryWheelAct;
import com.example.onpus.weddingpanda.Game.WaitingRmAct;
import com.example.onpus.weddingpanda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Game extends Fragment {
    @BindView(R.id.redblue)
    ImageView redBlue;
    String type;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private com.google.firebase.database.Query mQueryType;

    private com.google.firebase.database.Query mQueryUserType;

    public Game() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this,view);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = this.getArguments();
        if(bundle!=null)
            type = bundle.getString("type");
        Log.d("typefromAlbum",type);
        return view;
    }


    @OnClick({R.id.redblue, R.id.lottery,R.id.guessPhoto,R.id.redPocket})
    public void onClick(final View view) {
        //check guest
        final String[] coupleid = new String[1];
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.child(userId).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue(String.class);
                Log.d("typeA","guest");
                //red blue
                if (view.getId() == R.id.redblue) {
                    if(!type.equals("guest")){
                        //dialog for custom / start
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("")
                                .setContentText("Do you wanna start a new game?")
                                .setCancelText("Custom")
                                .setConfirmText("Start it")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent intent = new Intent(getActivity(), WaitingRmAct.class);
                                        intent.putExtra("type","couple");
                                        intent.putExtra("Game","Redblue");
                                        startActivity(intent);
                                        sweetAlertDialog.dismissWithAnimation();

                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent intent = new Intent(getActivity(), GameCustomActivity.class);
                                        startActivity(intent);
                                        sDialog.cancel();
                                    }
                                })
                                .show();
                    }else{
                        Intent intent = new Intent(getActivity(), WaitingRmAct.class);
                        intent.putExtra("type","guest");
                        intent.putExtra("Game","Redblue");
                        startActivity(intent);
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (view.getId() == R.id.lottery) {
            Toast.makeText(getActivity(), "lottery", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), WaitingRmAct.class);
            intent.putExtra("Game","DrawCircle");
            checkGuest(intent);

        }
        if (view.getId() == R.id.guessPhoto) {
            Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
        }
        if (view.getId() == R.id.redPocket) {
            Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkGuest(final Intent intent){
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        //check if guest
        db.child("Users").child(currentUser.getUid()).child("userType").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                String id = currentUser.getUid();
                Log.d("typeA","guest");
                //check guest
                if(type.equals("guest")) {
                    mQueryType = db.child("Users").orderByChild("guest/"+id).equalTo(false);
                    mQueryType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String coupleid = null;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                coupleid = child.getKey();
                            }


                            if(coupleid!=null){
                                checkGameStart(coupleid,intent);

                            }else{

                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkGameStart(String id, final Intent intent) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("Games").child(id).child("DrawCircle").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("send")) {
                    // run some code

                    new SweetAlertDialog(getActivity())
                            .setTitleText("Sorry!")
                            .setContentText("The game has started!")
                            .show();

                }
                else{
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
