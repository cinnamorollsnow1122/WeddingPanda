package com.example.onpus.weddingpanda.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.onpus.weddingpanda.MainActivity;
import com.example.onpus.weddingpanda.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by onpus on 2017/12/15.
 */

public class Fragment_navig extends Fragment{

    @BindView(R.id.datepicker)
    Button datepick;
    Boolean[] question = new Boolean[3];
    @BindView(R.id.questionView)
    TextView questionView;
    private int year;
    private int month;
    private int day;

    public Fragment_navig() {
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

        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this,view);
        Arrays.fill(question, Boolean.FALSE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (question[0]) {
            questionView.setText(R.string.question2);
        }
    }
    @OnClick(R.id.datepicker)
    public void onClick(View view){
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getActivity().getFragmentManager(), "datePicker");
//        questionView.setText(R.string.question2);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {;
                questionView.setText(R.string.question2);
            }
        });
    }

}
