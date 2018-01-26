package com.example.onpus.weddingpanda.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onpus.weddingpanda.R;


public class Fragment_main_couple extends Fragment {


    public Fragment_main_couple() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_main_couple newInstance(String param1, String param2) {
        Fragment_main_couple fragment = new Fragment_main_couple();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_main_couple, container, false);

        return view;
    }

}
