package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/4/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.Game.WaitingRmAct;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by onpus on 2018/4/1.
 */

public class CustomDialogClassRb extends Dialog {
    public Activity activity;
    public Dialog dialog;
    String game;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @BindView(R.id.answersheet)
    ListView answersheetView;

    public CustomDialogClassRb(Activity activity,String game) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.game = game;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rbdialog);
        ButterKnife.bind(this);
        initList();

    }

    private void initList() {
        //enter

        ref.child("Games").child(currentUser.getUid()).child(game).child("waitingrm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for waitingrm
                final ArrayList<String> waitingListid = new ArrayList<>();
                for (DataSnapshot childlist : dataSnapshot.getChildren()) {

                    waitingListid.add(childlist.getKey());

                    Log.d("key", childlist.getKey());
                }

                //get  current question key

                ref.child("Games").child(currentUser.getUid()).child(game).child("currentQuestionKey").addListenerForSingleValueEvent(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String questionKey = dataSnapshot.getValue(String.class);

                        //compare the waitinglist and the answerlist, see who havent answered
                        ref.child("Games").child(currentUser.getUid()).child(game).child("AnswerGuest").child(questionKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<String> answerlistid = new ArrayList<>();
                                final HashMap answermap = new HashMap();
                                for (DataSnapshot childlist: dataSnapshot.getChildren()){
                                    answermap.put(childlist.getKey(),childlist.getValue());
                                    answerlistid.add(childlist.getKey());


                                    Log.d("key",childlist.getKey());
                                }

                                if(!answerlistid.isEmpty()){
                                    //get user details by searching with waitinglist id
                                    ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ArrayList<SearchActivity.User> userinfo = new ArrayList<>();
                                            for (DataSnapshot childlist: dataSnapshot.getChildren()){
                                                //get user details
                                                for(String id :waitingListid ){
                                                    if (childlist.getKey().equals(id)){
                                                        SearchActivity.User temp = childlist.getValue(SearchActivity.User.class);
                                                        temp.setId(id);
                                                        userinfo.add(temp);
                                                        break;
                                                    }
                                                }

                                            }
                                            if(!userinfo.isEmpty()) {
                                                CustomDialogClassRb.DataListAdapter adapter = new CustomDialogClassRb.DataListAdapter(answerlistid,userinfo,answermap);
                                                answersheetView.setAdapter(adapter);


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

                            }});


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




//                if (start){
//                    Intent sIntent = new Intent(WaitingRmAct.this, LotteryWheelAct.class);
//                    Bundle sBundle = new Bundle();
//                    sBundle.putString("Waitrm","waiting");
//                    sIntent.putExtras(sBundle);
//                    startActivity(sIntent);
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

//    public void dialogClicked(final View view)
//    {
//    Toast.makeText(getContext(), "You sent your answer", Toast.LENGTH_SHORT).show();
//    }
@OnClick(R.id.sendAns)
public void dialogClicked(View v) {
    Toast.makeText(getContext(), "You sent your answer", Toast.LENGTH_SHORT).show();
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final DatabaseReference db = ref.child("Games").child(currentUser.getUid()).child(game).child("currentQuestionKey");
    ref.child("Games").child(userId).child(game).child("press").setValue(true);

//    db.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            String questionKey = dataSnapshot.getValue(String.class);
//            db.child("Games").child(userId).child("Redblue").child("Question").child(questionKey).child("next").setValue(true);
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    });
}

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sendAns:
//                Toast.makeText(getContext(), "You sent  your answer", Toast.LENGTH_SHORT).show();
//                break;
//
//            default:
//                break;
//        }
//        dismiss();
//    }
    class DataListAdapter extends BaseAdapter {
        ArrayList<SearchActivity.User> userinfo = new ArrayList<>();
         HashMap answermap = new HashMap();
         ArrayList<String> answerlistid = new ArrayList<>();

        DataListAdapter() {
            userinfo = null;

        }

        public DataListAdapter( ArrayList<String> answerlistid ,ArrayList<SearchActivity.User> userinfo,HashMap answermap) {
            this.answerlistid = answerlistid;
            this.userinfo = userinfo;
            this.answermap = answermap;


        }

        public int getCount() {
            // TODO Auto-generated method stub
            return userinfo.size();
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
            row = inflater.inflate(R.layout.answerstatussheet, parent, false);
            final TextView username,type,answerstatus;
            ImageView icon;
            final String partid = userinfo.get(position).getId();
            username = (TextView) row.findViewById(R.id.iconName);
            icon=(ImageView)row.findViewById(R.id.iconWList);
            type=(TextView)row.findViewById(R.id.typeUser);
            answerstatus = (TextView)row.findViewById(R.id.ansstatus);
            username.setText(userinfo.get(position).getName());
            type.setText(userinfo.get(position).getUserType());
            //check guest ans status : 1. not answer 2. Correct ans 3. Wrong ans 4. Lose
            if (!answerlistid.contains(userinfo.get(position).getId())){
                answerstatus.setText("Not answered");

            }
            else if (answermap.get(partid).equals(true))
                answerstatus.setText("Correct");
            else{
                answerstatus.setText("Wrong");
            }
            //check if user lose
            if (game.equals("Redblue")){
                DatabaseReference mLoser = ref.child("Games").child(currentUser.getUid()).child("Redblue").child("Loser");
                mLoser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(partid)){
                            answerstatus.setText("Lose");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


            Picasso.with(getContext()).load(userinfo.get(position).getUserPic()).into(icon);

            return (row);
        }
    }

}

