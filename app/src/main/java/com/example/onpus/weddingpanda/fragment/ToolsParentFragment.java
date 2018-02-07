package com.example.onpus.weddingpanda.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ToolsParentFragment extends Fragment {
    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    public ToolsParentFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tools_parent, container, false);
        ButterKnife.bind(this,view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        initViewPager();
        return view;
    }

    private void initViewPager() {
        List<String> titles = new ArrayList();
        titles.add("Task");
        titles.add("Budget");
        titles.add("Guest");

        for(int i=0;i<titles.size();i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new TasklistFragment());
        fragments.add(new BudgetFragment());
        fragments.add(new GuestFragment());

        FragmentAdapter mFragmentAdapteradapter =
                new FragmentAdapter(getFragmentManager(), fragments, titles);
        //给ViewPager设置适配器
        mViewpager.setAdapter(mFragmentAdapteradapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewpager);
        //将TabLayout和ViewPager关联起来。
        //给TabLayout设置适配器
        mTabLayout.setTabsFromPagerAdapter(mFragmentAdapteradapter);
    }

}
