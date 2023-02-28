package com.example.team_six_fireball_project;
//Andrew Brown

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//https://www.youtube.com/watch?v=bjYstsO1PgI navigation menu guide.

public class MainActivity extends AppCompatActivity implements ProfileFragment.IProfileFragment, LoginFragment.ILoginFragment, RegisterFragment.IRegisterFragment, MainFragment.IMainFragment, CreateCommentFragment.ICreateCommentFragment, CommentFragment.ICommentFragment, ForumsFragment.IForumsFragment, NavigationView.OnNavigationItemSelectedListener{

    //TODO:change what the back button does
    NavigationView navigationView;
    DrawerLayout drawer;
    String id;
    TextView name;
    private static final String TAG = "MainActivity";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this is to keep night mode off
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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

        //how to update the navigation bar
        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.textViewNavAccountType);

        if (mAuth.getCurrentUser() != null){
            name.setText(mAuth.getCurrentUser().getDisplayName());
        } else {
            name.setText("Guest");
        }


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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String createdDate = dateFormat.format(new Date());
        return createdDate;
    }

    @Override
    public void goToHomeFragment() {
        logIn();
    }

    @Override
    public String getCurrentDateRegisterFrag() {
        return getCreateDate();
    }

    public void logOut(){
        mAuth.signOut();
        name.setText("Guest");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    public void logIn(){
        name.setText(mAuth.getCurrentUser().getDisplayName());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void mainSignOut() {
        logOut();
    }

    @Override
    public void loginSignIn() {
        logIn();
    }

    @Override
    public void profileToHome() {
        logIn();
    }

    class NavRunnable implements Runnable{
        MenuItem item;
        
        public NavRunnable(MenuItem item) {
            this.item = item;
        }

        @Override
        public void run() {

            switch (item.getItemId()){
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MainFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_profile:
                    if(mAuth.getCurrentUser() != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ProfileFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new MainFragment(2))
                                .addToBackStack(null)
                                .commit();
                    }
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
                    logOut();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
        }
    }

}

