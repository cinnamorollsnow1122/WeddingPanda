package com.example.onpus.weddingpanda.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.constant.Budget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BudgetaddFragment extends Fragment {
    @BindView(R.id.spinner)
    Spinner category;
    @BindView(R.id.getItemTitle)
    EditText itemTitle;
    @BindView(R.id.getBudgetam)
    EditText budgetam;
    @BindView(R.id.getActualam)
    EditText actualam;
    @BindView(R.id.paidbox)
    CheckBox paid;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;
    final HashMap<String,String> spinnerMap = new HashMap<>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budgetadd, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Budgets").child(currentUser.getUid());

        ButterKnife.bind(this,view);

        //set toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
         setSpinner();


        return view;
    }


    @OnClick(R.id.submitBudget)
    public void onClick(){
        final String[] selected = new String[1];
        category.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        System.out.println(item.toString());
                        selected[0] = spinnerMap.get(category.getSelectedItem());
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        newItemBudget(spinnerMap.get(category.getSelectedItem()));
        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

    }

    public void newItemBudget(String cate){
        //add album
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newItem=ref.child("Budgets").child(currentUser.getUid()).child(cate).child("Items").push();
        final String[] pushkey = {""};

        com.google.firebase.database.Query mQueryFollowing = ref.orderByChild("Budgets/"+currentUser.getUid()+"/").equalTo(true);;

//        pushkey[0] = newAlbum.getKey();

        //add item to database
        newItem.setValue(new Budget.ItemCategory(itemTitle.getText().toString(),
                budgetam.getText().toString(), actualam.getText().toString(),paid.isChecked()));

    }
    public void setSpinner(){

//        final String[] categories = {"雞腿飯", "魯肉飯", "排骨飯", "水餃", "陽春麵"};
        final List<String> temp = new ArrayList<String>();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //ADDED ON 6/4/2017 ALICE
                temp.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        String tempValue = child.child("category").getValue(String.class);
                        Log.d("temp",tempValue);
                        temp.add(tempValue);
                        spinnerMap.put(tempValue,child.getKey());

                    } catch (Exception e) {

                    }
                }
                ArrayAdapter<String> lunchList = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        temp);
                category.setAdapter(lunchList);
                category.setPrompt("Select category");
                String selected = spinnerMap.get(category.getSelectedItem());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }


}
