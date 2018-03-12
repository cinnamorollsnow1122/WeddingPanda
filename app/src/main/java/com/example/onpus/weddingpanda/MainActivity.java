package com.example.onpus.weddingpanda;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import com.example.onpus.weddingpanda.adapter.MyAdapterAlbum;
import com.example.onpus.weddingpanda.adapter.SearchListAdapter;
import com.example.onpus.weddingpanda.constant.AlbumItem;
import com.example.onpus.weddingpanda.constant.User;
import com.example.onpus.weddingpanda.fragment.Album;
import com.example.onpus.weddingpanda.fragment.Fragment_main_couple;
import com.example.onpus.weddingpanda.fragment.Game;

import com.example.onpus.weddingpanda.fragment.ToolsParentFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private FirebaseAuth auth;
    private com.google.firebase.database.Query mQueryGuest;
    private DatabaseReference mDatabase;
    ListView guestListview;
    SearchListAdapter guestListAdapter;
    private ArrayList<User> guestItem = new ArrayList<>();
    private ArrayList<String> guestlist = new ArrayList<>();

    PublishSubject publishSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//
//        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
//        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
//        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
//        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.mainmenu);

        //firebase
        auth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new Fragment_main_couple()).commit();


        //Intent intent = new Intent(getApplicationContext(),LoginAct.class);
        //startActivity(intent);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.home, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.tools, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.game, R.color.colorAccent);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("album", R.drawable.album, R.color.colorAccent);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Game", R.drawable.game, R.color.colorAccent);

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
                    Fragment_main_couple main_couple = new Fragment_main_couple();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,main_couple).commit();
                }else  if (position==1)
                {
                    ToolsParentFragment toolsParentFragment=new ToolsParentFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,toolsParentFragment).commit();

                }else  if (position==2)
                {

                    Game gameFrag = new Game();
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

//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.mainmenu, menu);
//
//        MenuItem menuSearchItem = menu.findItem(R.id.my_search);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView searchView = (SearchView) menuSearchItem.getActionView();
//
//        // Assumes current activity is the searchable activity
//
//
//        ComponentName cn = new ComponentName(this, SearchActivity.class);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
//        searchView.setQueryHint(getResources().getString(R.string.search_hint));
//        // 這邊讓icon可以還原到搜尋的icon
////        searchView.setIconifiedByDefault(true);
////        searchView.setSubmitButtonEnabled(true);
////        searchView.setOnQueryTextListener(this);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginAct.class));
                return true;
            case R.id.my_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOut() {
        auth.signOut();
    }

//search
    @Override
    public void onNewIntent(Intent intent){
        setIntent(intent);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //now you can display the results
        }
    }

    /**
     * Initialize friend list
     */
    private void initFriendList() {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mQueryGuest =  mDatabase.orderByChild("userType").equalTo("guest");
        guestListview = (ListView) findViewById(R.id.guestlist);
        guestListview.setTextFilterEnabled(false);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //ADDED ON 6/4/2017 ALICE
                guestItem.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                        guestlist.add(child.getKey());
                        User temp = child.getValue(User.class);
                        guestItem.add(temp);

                }
                if(guestItem!=null)
//                    guestListAdapter = new SearchListAdapter(getApplicationContext(), guestItem,guestlist,);


                guestListview.setAdapter(guestListAdapter);
                //adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        // use to enable search view popup text
        //friendListView.setTextFilterEnabled(true);

        // set up click listener
//        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0 && position <= friendList.size()) {
//                    handelListItemClick((User)friendListAdapter.getItem(position - 1));
//                }
//            }
//        });
    }

//search
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}
