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
import android.view.textclassifier.TextLinks;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//https://www.youtube.com/watch?v=bjYstsO1PgI navigation menu guide.
// OKHttpClient information: https://square.github.io/okhttp/

public class MainActivity extends AppCompatActivity implements MapsFragment.IMapsFragment, ProfileFragment.IProfileFragment, LoginFragment.ILoginFragment, RegisterFragment.IRegisterFragment, MainFragment.IMainFragment, CreateCommentFragment.ICreateCommentFragment, CommentFragment.ICommentFragment, ForumsFragment.IForumsFragment, NavigationView.OnNavigationItemSelectedListener{

    //TODO:change what the back button does
    private static final String TAG = "demo";
    private final OkHttpClient client = new OkHttpClient();

    ArrayList<FireBall> fireBallList = new ArrayList<>();

    NavigationView navigationView;
    DrawerLayout drawer;
    String id;
    TextView name;
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

        getAllFireBallData();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public ArrayList<FireBall> getFireBallList() {
        return fireBallList;
    }

    class FireBallRunnable implements Runnable{

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url("https://ssd-api.jpl.nasa.gov/fireball.api")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "onFailure: "+ e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            //how to see all data in api for meteor
                            //Log.d(TAG, "onResponse: "+ jsonObject.get("data"));
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray object = jsonArray.getJSONArray(i);
                                //Log.d(TAG, "onResponse: "+object.get(0)); //date
                                String date, energy, impactE, lat, latDir, lon, lonDir, alt, vel;
                                if (object.isNull(0)){ date= "null"; }
                                else{ date= (String) object.get(0);}
                                if (object.isNull(1)){ energy= "null"; }
                                else{ energy= (String) object.get(1);}
                                if (object.isNull(2)){ impactE= "null"; }
                                else{ impactE= (String) object.get(2);}
                                if (object.isNull(3)){ lat= "null"; }
                                else{ lat= (String) object.get(3);}
                                if (object.isNull(4)){ latDir= "null"; }
                                else{ latDir= (String) object.get(4);}
                                if (object.isNull(5)){ lon= "null"; }
                                else{ lon= (String) object.get(5);}
                                if (object.isNull(6)){ lonDir= "null"; }
                                else{ lonDir= (String) object.get(6);}
                                if (object.isNull(7)){ alt= "null"; }
                                else{ alt= (String) object.get(7);}
                                if (object.isNull(8)){ vel= "null"; }
                                else{ vel= (String) object.get(8);}

                                FireBall fireBall = new FireBall(date, energy, impactE, lat, latDir, lon, lonDir, alt, vel);
                                //Working: Log.d(TAG, "onResponse: "+ fireBall.toString());
                                fireBallList.add(fireBall);
                            }
                            //947 Log.d(TAG, "onResponse: "+fireBallList.size());
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void getAllFireBallData(){
        FireBallRunnable fireBallRunnable = new FireBallRunnable();
        new Thread(fireBallRunnable).start();
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
        toCommentFrag(tempForumID);
    }

    @Override
    public String getForumID() {
        return this.id;
    }

    @Override
    public void commentToHome() {
        logIn();
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
        if (mAuth.getCurrentUser() != null) {
            name.setText(mAuth.getCurrentUser().getDisplayName());
        }
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
    public void saveProfileArrayToMain(String tempForumID) {
        toCommentFrag(tempForumID);
    }

    public void toCommentFrag(String tempForumId){
        id = tempForumId;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CommentFragment())
                .addToBackStack(null)
                .commit();
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

