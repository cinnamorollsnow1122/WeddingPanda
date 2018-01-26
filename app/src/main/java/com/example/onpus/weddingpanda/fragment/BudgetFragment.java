package com.example.onpus.weddingpanda.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.BudgetExpandableListAdapter;
import com.example.onpus.weddingpanda.constant.Budget;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BudgetFragment extends Fragment {

    @BindView(R.id.material_design_android_floating_action_menu)
    FloatingActionMenu buttonmenu;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;


    private String m_Text = "";

    public BudgetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final List<String> listDataHeader = new ArrayList<>();
        final HashMap<String, List<Budget.ItemCategory>> listDataChild = new HashMap<>();

        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        ButterKnife.bind(this,view);
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);

        // preparing list data
        prepareListData(listDataHeader,listDataChild);

        return view;
    }
    @OnClick({R.id.item1_btn_addcate,R.id.item2_btn_additem})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.item1_btn_addcate:
                addcate();
                break;
            case R.id.item2_btn_additem:
                BudgetaddFragment frag = new BudgetaddFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.budgetpage
                                , frag)
                        .addToBackStack(null)
                        .commit();
                break;

        }
    }

//    public void initialview(){
//
//    }
    public void newCategory(final String cate){
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference newCate=ref.child("Budgets").child(currentUser.getUid()).push();
    final String[] pushkey = {""};

    pushkey[0] = newCate.getKey();

    newCate.child("category").setValue(cate);
//    newCate.child("id").setValue(pushkey);

}
    public void addcate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add your own category");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialoglayout, (ViewGroup) getView(), false);
        // Set up the input

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                newCategory(m_Text);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog d = builder.show();
        int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        int textViewId = d.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) d.findViewById(textViewId);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void prepareListData(final List<String> listDataHeader,final HashMap<String, List<Budget.ItemCategory>> listDataChild) {

         final HashMap<String,String> headermap = new HashMap<>();
//        final List< Budget.ItemCategory> temp = new ArrayList<>();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
         final DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Budgets").child(currentUser.getUid());


        //add header
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //ADDED ON 6/4/2017 ALICE
                if (listDataHeader!=null)
                    listDataHeader.clear();
                if (listDataChild!=null)
                    listDataChild.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                        final String tempValue = child.child("category").getValue(String.class);
                        listDataHeader.add(tempValue);
                        headermap.put(tempValue,child.getKey());
//                        Map<String, List<Budget.ItemCategory>> td = (Map<String, List<Budget.ItemCategory>>) child.child("Items").getValue();
//                        Budget.ItemCategory item = td.values();

//                        Log.d("key", String.valueOf(tddd.));

                FirebaseDatabase.getInstance().getReference().child("Budgets").child(currentUser.getUid()).child(child.getKey()).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot snapshot) {
                                 final List< Budget.ItemCategory> temp = new ArrayList<>();
                                 //ADDED ON 6/4/2017 ALICE
                            for (DataSnapshot child : snapshot.getChildren()) {
                                try {

                                    Log.d("panda2222222",child.child("title").getValue(String.class));
                                   Budget.ItemCategory tempValueItem = new Budget.ItemCategory();
                                    tempValueItem.setTitle(child.child("title").getValue(String.class));
                                    tempValueItem.setActualAmount(child.child("actualAmount").getValue(String.class));
                                    tempValueItem.setBudgetAm(child.child("budgetAm").getValue(String.class));
                                    tempValueItem.setPaid(child.child("paid").getValue(Boolean.class));

                                    temp.add(tempValueItem);
                                }catch (DatabaseException e){

                                }

                            }
                                 listDataChild.put(tempValue,temp); // 標題, 內容

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                    // setting list adapter

                listAdapter = new BudgetExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        //add item
//        for(String key:headermap.keySet()){
////                    mItems[0] = mDatabase.child(headermap.get(key)).child("Items");
//            Log.d("tempkey",headermap.get(key));
//            mDatabase2.child(headermap.get(key)).child("Items").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//
//                    //ADDED ON 6/4/2017 ALICE
//
//                    for (DataSnapshot child : snapshot.getChildren()) {
//                        try {
//                            Budget.ItemCategory tempValueItem = child.getValue(Budget.ItemCategory.class);
//                            temp.add(tempValueItem);
//                        }catch (DatabaseException e){
//
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            listDataChild.put(key, temp); // 標題, 內容
//        }


// Adding child data
//        List<String> first = new ArrayList<String>();
//        first.add("第一回　　張天師祈禳瘟疫　洪太尉誤走妖魔");
//        first.add("第二回　　王教頭私走延安府　九紋龍大鬧史家村");
//        first.add("第三回　　史大郎夜走華陰縣　魯提轄拳打鎮關西");
//        first.add("第四回　　趙員外重修文殊院　魯智深大鬧五臺山");
//        first.add("第五回　　小霸王醉入銷金帳　花和尚大鬧桃花村");
//
//        List<String> second = new ArrayList<String>();
//        second.add("第一回　　甄士隱夢幻識通靈　賈雨村風塵懷閨秀");
//        second.add("第二回　　賈夫人仙逝揚州城　冷子興演說榮國府");
//        second.add("第三回 　　託內兄如海薦西賓　接外孫賈母惜孤女");
//        second.add("第四回　　薄命女偏逢薄命郎　葫蘆僧判斷葫蘆案");
//        second.add("第五回　　賈寶玉神遊太虛境　警幻仙曲演紅樓夢");
//
//        List<String> end = new ArrayList<String>();
//        end.add("第一回　　宴桃園豪傑三結義，斬黃巾英雄首立功");
//        end.add("第二回　　張翼德怒鞭督郵，何國舅謀誅宦豎");
//        end.add("第三回　　議溫明董卓叱丁原，餽金珠李肅說呂布");
//        end.add("第四回　　廢漢帝陳留為皇，謀董賊孟德獻刀");
//        end.add("第五回　　發矯詔諸鎮應曹公，破關兵三英戰呂布");
//
//        listDataChild.put(listDataHeader.get(0), first); // 標題, 內容
//        listDataChild.put(listDataHeader.get(1), second);
//        listDataChild.put(listDataHeader.get(2), end);

    }

}
