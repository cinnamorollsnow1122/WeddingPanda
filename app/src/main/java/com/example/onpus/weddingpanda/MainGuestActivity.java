package com.example.onpus.weddingpanda;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.onpus.weddingpanda.R;
import com.example.onpus.weddingpanda.fragment.Album;
import com.example.onpus.weddingpanda.fragment.Fragment_main_couple;
import com.example.onpus.weddingpanda.fragment.Game;
import com.example.onpus.weddingpanda.fragment.ToolsParentFragment;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public class MainGuestActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String userType = "guest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.mainmenu);
        ButterKnife.bind(this);

//
//        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
//        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
//        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
//        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);



            //firebase
            auth = FirebaseAuth.getInstance();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame,new Fragment_main_couple()).commit();


            //Intent intent = new Intent(getApplicationContext(),LoginAct.class);
            //startActivity(intent);

            AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
            AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.home, R.color.colorBg);
            AHBottomNavigationItem item2 = new AHBottomNavigationItem("Invitation", R.drawable.ic_insert_invitation_black_24dp, R.color.colorBg);
            AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.game, R.color.colorBg);
            AHBottomNavigationItem item4 = new AHBottomNavigationItem("Album", R.drawable.album, R.color.colorBg);

        // Add items
            bottomNavigation.addItem(item1);
            bottomNavigation.addItem(item2);
            bottomNavigation.addItem(item3);
            bottomNavigation.addItem(item4);
            bottomNavigation.setCurrentItem(0);
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
                    if (position==0) {
                        Fragment_main_couple main_couple = new Fragment_main_couple();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, main_couple).commit();
                    }
//                    }else  if (position==1)
//                    {
////                        ToolsParentFragment toolsParentFragment=new ToolsParentFragment();
////                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,toolsParentFragment).commit();
//
                    else  if (position==2)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", userType );
                        Log.d("type from Mauin",userType);
                        Game gameFrag = new Game();
                        gameFrag.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,gameFrag).commit();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainGuestActivity.this, LoginAct.class));


                return true;
            //case R.id.action_add:
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOut() {
        auth.signOut();
    }

    //check any invite msg
    public void getinvite(){
        
    }
}
