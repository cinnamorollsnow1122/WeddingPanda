package com.example.onpus.weddingpanda;

import android.content.Intent;
import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.onpus.weddingpanda.adapter.PageView;
import com.example.onpus.weddingpanda.fragment.Album;
import com.example.onpus.weddingpanda.fragment.Fragment_main_couple;
import com.example.onpus.weddingpanda.fragment.Fragment_navig;
import com.example.onpus.weddingpanda.fragment.Game;
import com.example.onpus.weddingpanda.fragment.ToolsParentFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//
//        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
//        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
//        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
//        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment_navig navig = new Fragment_navig();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,navig).commit();


        //Intent intent = new Intent(getApplicationContext(),LoginAct.class);
        //startActivity(intent);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.msg, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.tools, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.home, R.color.colorAccent);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("album", R.drawable.album, R.color.colorAccent);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Game", R.drawable.game, R.color.colorAccent);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.setCurrentItem(1);
        Fragment_main_couple main_couple = new Fragment_main_couple();
        //go to main fragment home page
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame,main_couple).commit();
// Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

    // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

    // Enable the translation of the FloatingActionButton
    //        bottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);

    // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

    // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Display color under navigation bar (API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
        bottomNavigation.setTranslucentNavigationEnabled(true);

        // Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // Enable / disable item & set disable color
        bottomNavigation.enableItemAtPosition(2);
//        bottomNavigation.disableItemAtPosition(2);
        bottomNavigation.setItemDisableColor(Color.parseColor("#3A000000"));

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
//                switch(position) {
//                    case 0:
//                        Album albumfragment = new Album();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,albumfragment).commit();
//                    case 1:
//                        Fragment_main_couple main_couple = new Fragment_main_couple();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,main_couple).commit();
//                    case 2:
//                        Game gamefragment=new Game();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,gamefragment).commit();
//
//                }
                if (position==0)
                {
                    Album albumfragment = new Album();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,albumfragment).commit();
                }else  if (position==1)
                {
                    ToolsParentFragment toolsParentFragment=new ToolsParentFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,toolsParentFragment).commit();

                }else  if (position==2)
                {
                    Fragment_main_couple main_couple = new Fragment_main_couple();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,main_couple).commit();
                }else  if (position==3)
                {
                    Album albumfragment=new Album();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,albumfragment).commit();
                }

                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
                // Manage the new y position
            }
        });


    }

}
