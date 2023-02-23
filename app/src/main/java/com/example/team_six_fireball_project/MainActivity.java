package com.example.team_six_fireball_project;
//Andrew Brown
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

//https://www.youtube.com/watch?v=bjYstsO1PgI navigation menu guide.

public class MainActivity extends AppCompatActivity implements RegisterFragment.IRegisterFragment, CreateCommentFragment.ICreateCommentFragment, CommentFragment.ICommentFragment, ForumsFragment.IForumsFragment, NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    DrawerLayout drawer;
    String id;
    String navDisplayName;
    private static final String TAG = "MainActivity";
    FirebaseAuth mAuth;
    //TODO: figure out how to update guest and account name in navigation


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.ContentView);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .addToBackStack(null)
                .commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //running on a new thread to increase app speed
        NavRunnable runnable = new NavRunnable(item);
        new Thread(runnable).start();

        return true;
    }



    @Override
    public void saveCommentArrayToMain(String tempForumID) {
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        ArrayList<String> idTopic = new ArrayList<>();

        id = tempForumID;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CommentFragment())
                .addToBackStack(null)
                .commit();
    }


    @Override
    public String getForumID() {
        return this.id;
    }

    @Override
    public String getCreateForumID() {
        return this.id;
    }

    @Override
    public String getCreateDate() {
        Calendar c;
        String date, amPm;

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        if (c.get(Calendar.AM_PM) == 0) {
            amPm = "AM";
        } else if (c.get(Calendar.AM_PM) == 1){
            amPm = "PM";
        } else {
            amPm = "";
        }

        date = (month+1) + "/" + day + "/" + year + " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.MILLISECOND) + " " + amPm;
        return date;
    }

    @Override
    public void goToHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .commit();
    }

    class NavRunnable implements Runnable{
        MenuItem item;
        
        public NavRunnable(MenuItem item) {
            this.item = item;
        }

        @Override
        public void run() {
//            TextView navAccount = navigationView.findViewById(R.id.textViewNavAccountType);
//            if (mAuth.getCurrentUser() != null){
//                navAccount.setText(mAuth.getCurrentUser().getDisplayName());
//            } else{
//                navAccount.setText("Guest TEST");
//            }
            Log.d(TAG, "run: hamburger");

            switch (item.getItemId()){
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MainFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_login:
                    if(mAuth.getCurrentUser() == null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new MainFragment(2))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case R.id.nav_info:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new GeneralInfoFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_map:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MapsFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_forum:
                    if (mAuth.getCurrentUser() == null){

                        //HOW TO RUN ON MAIN THREAD
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new MainFragment(1))
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });

                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ForumsFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                case R.id.nav_logout:
                    mAuth.signOut();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MainFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
        }
    }

}

