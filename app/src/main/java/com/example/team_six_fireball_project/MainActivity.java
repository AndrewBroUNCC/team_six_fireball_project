package com.example.team_six_fireball_project;
//Andrew Brown

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//https://www.youtube.com/watch?v=EBhmRaa8nhE drop down menu guide.
//https://www.youtube.com/watch?v=bjYstsO1PgI navigation menu guide.
// OKHttpClient information: https://square.github.io/okhttp/
//creating popup window: https://www.google.com/search?q=andriod+studio+popup+window&rlz=1C1JZAP_enUS937US937&oq=andriod+studio+popup+window&aqs=chrome..69i57j0i13i512l5j0i22i30l4.8002j0j4&sourceid=chrome&ie=UTF-8#fpstate=ive&vld=cid:da640af1,vid:4GYKOzgQDWI

public class MainActivity extends AppCompatActivity implements GraphFragment.IGraphFragment, ActivityCompat.OnRequestPermissionsResultCallback, MapsFragment.IMapsFragment, ProfileFragment.IProfileFragment, LoginFragment.ILoginFragment, RegisterFragment.IRegisterFragment, MainFragment.IMainFragment, CreateCommentFragment.ICreateCommentFragment, CommentFragment.ICommentFragment, ForumsFragment.IForumsFragment, NavigationView.OnNavigationItemSelectedListener{

    //TODO: map markers. (implementation last) (easy) -Drew
    //TODO:visual page. (implement data) (med) -Drew, (DashBoard) -Anders (unknown difficulty)
    //TODO:Home page (functionality) (easy) -JuiceMan
    //TODO:General page (more stuff) (easy) -Justin L
    //TODO: contact page (easy) -patrick

    private static final String TAG = "demo";
    private final OkHttpClient client = new OkHttpClient();
    //popup update window builder (updateProfileDialog)
    public AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    public Button buttonUpdatePopUpPictureSave, buttonUpdatePopUpNameSave, buttonPopUpUpdate;
    public TextView updatePopUpCancel;
    private EditText updatePopUpGetName, editTextPopUpUrl;
    private ImageView profilePic, popUpPic;
    private String url;
    

    ArrayList<FireBall> fireBallList = new ArrayList<>();

    NavigationView navigationView;
    DrawerLayout drawer;
    String id;
    TextView name;
    ImageView navPic;
    FirebaseAuth mAuth;

    HashMap<String, Object> userUpdate;
    QueryDocumentSnapshot docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this is to keep night mode off
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

/*       HOW-TO request permissions
        String[] perms = {"android.permission.READ_MEDIA_IMAGES","android.permission.MANAGE_EXTERNAL_STORAGE","android.permission.READ_MEDIA_IMAGES","android.permission.FINE_LOCATION", "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.INTERNET"};
        int permsRequestCode = 200;
        requestPermissions(perms, permsRequestCode);
*/
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
        navPic = headerView.findViewById(R.id.imageNavPicture);


        if (mAuth.getCurrentUser() != null){
            name.setText(mAuth.getCurrentUser().getDisplayName());
            setNavPic();
        } else {
            name.setText(R.string.guest);
            navPic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.meteor_icon));
        }


        getAllFireBallData();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void updateProfileDialog(View profileView){

        dialogBuilder = new AlertDialog.Builder(this);
        final View updatePopUp = getLayoutInflater().inflate(R.layout.popup, null);

        //EditText editText = updatePopUp.findViewById(R.id.editTextPopUpUrl);
        updatePopUpCancel = updatePopUp.findViewById(R.id.textViewUpdatePopUpCancel);
        buttonUpdatePopUpPictureSave = updatePopUp.findViewById(R.id.buttonUpdatePopUpUpdatePicture);
        buttonUpdatePopUpNameSave = updatePopUp.findViewById(R.id.buttonUpdatePopUpUpdateName);
        updatePopUpGetName = updatePopUp.findViewById(R.id.editTextUpdatePopUpUserName);
        popUpPic = updatePopUp.findViewById(R.id.imageViewPopUpImage);
        editTextPopUpUrl = updatePopUp.findViewById(R.id.editTextPopUpUrlSave);
        buttonPopUpUpdate = updatePopUp.findViewById(R.id.buttonPopUpUpdate);

        dialogBuilder.setView(updatePopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        if(mAuth.getCurrentUser() != null) {
            updatePopUpGetName.setText(mAuth.getCurrentUser().getDisplayName());
            setPopUpPic();
        }

        profilePic = findViewById(R.id.imageViewProfileFragProfileImage);


        buttonUpdatePopUpNameSave.setOnClickListener(view -> {
            //update name in auth and db
            updatePopUpGetName = updatePopUp.findViewById(R.id.editTextUpdatePopUpUserName);
            FirebaseUser user = mAuth.getCurrentUser();
            String updateName = updatePopUpGetName.getText().toString();
            String userId = mAuth.getCurrentUser().getUid();
            if (!updateName.isEmpty()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(updateName)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            mAuth.updateCurrentUser(user).addOnCompleteListener(task1 -> {
                                //Log.d(TAG, "onComplete: "+userId);
                                updateUserNameInDB(updateName, userId);
                            });
                            //Log.d(TAG, "onComplete: User has been registered successfully");
                        });
            } else {
                        //how to build an Alert Dialog
                        new AlertDialog.Builder(updatePopUp.getContext())
                                .setTitle("Error")
                                .setMessage("Name is Empty")
                                .setPositiveButton("Ok", (dialogInterface, i) -> {
                                }).show();
            }
        });
        buttonUpdatePopUpPictureSave.setOnClickListener(view -> {
            //handle picture pop up and saving
            if (editTextPopUpUrl.getText() == null || editTextPopUpUrl.getText().toString().isEmpty()) {
                new AlertDialog.Builder(updatePopUp.getContext())
                        .setTitle("Invalid Input")
                        .setMessage("Url is empty")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {

                        }).show();
            } else {
                String urlTemp = editTextPopUpUrl.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("userList")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                if (user.getUserID().compareTo(mAuth.getCurrentUser().getUid()) == 0) {
                                    HashMap<String, Object> userUpdate = new HashMap<>();
                                    userUpdate.put("email", user.getEmail());
                                    userUpdate.put("joinDate", user.getJoinDate());
                                    userUpdate.put("name", user.getName());
                                    userUpdate.put("uri", urlTemp);
                                    userUpdate.put("userID", user.getUserID());

                                    db.collection("userList").document(document.getId()).update(userUpdate)
                                            .addOnCompleteListener(task -> {
                                                dialog.dismiss();
                                                setProfilePic(view);
                                            });

                                }
                            }
                        });
            }
        });
        updatePopUpCancel.setOnClickListener(view -> dialog.dismiss());
        buttonPopUpUpdate.setOnClickListener(view -> {

            if (editTextPopUpUrl.getText() == null || editTextPopUpUrl.getText().toString().isEmpty()) {
                new AlertDialog.Builder(updatePopUp.getContext())
                        .setTitle("Invalid Input")
                        .setMessage("Url is empty")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                        }).show();
            } else {
                String urlTemp = editTextPopUpUrl.getText().toString();
                Picasso.get()
                        .load(urlTemp)
                        .fit()
                        .placeholder(R.drawable.meteor_icon)
                        .error(R.drawable.meteor_icon)
                        .into(popUpPic);
            }
        });
    }

    private void setPopUpPic() {
        try {


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("userList").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            if (Objects.requireNonNull(mAuth.getCurrentUser()).getUid().compareTo(user.getUserID()) == 0) {
                                url = user.getUri();

                                break;
                            }
                        }
                        if (url == null) {
                            popUpPic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.meteor_icon));
                        } else {
                            Picasso.get()
                                    .load(url)
                                    .fit()
                                    .error(R.drawable.meteor_icon)
                                    .into(popUpPic);
                        }
                    });
        } catch (Exception e){
            Log.d(TAG, "setPopUpPic: "+e.getMessage());
        }
    }

    public void updateUserNameInDB(String updateName, String userId){

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("userList")
                .get().addOnSuccessListener(value -> {
                    for (QueryDocumentSnapshot document: value) {
                        User user = document.toObject(User.class);
                        if(user.getUserID().compareTo(userId)==0){
                            docId = document;

                            userUpdate = new HashMap<>();
                            userUpdate.put("email", user.getEmail());
                            userUpdate.put("joinDate", user.getJoinDate());
                            userUpdate.put("name", updateName);
                            userUpdate.put("uri", user.getUri());
                            userUpdate.put("userID", user.getUserID());

                            //Log.d(TAG, "onEvent: test" + document.getId());
                            break;
                        }
                    }
                }).addOnCompleteListener(task -> db.collection("userList").document(docId.getId()).update(userUpdate)
                        .addOnCompleteListener(task1 -> {
                            if (mAuth.getCurrentUser() != null) {
                                name.setText(mAuth.getCurrentUser().getDisplayName());
                            }
                            dialog.dismiss();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new ProfileFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }));
    }

    public void setProfilePic(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userList").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                        User user = document.toObject(User.class);
                        if (Objects.requireNonNull(mAuth.getCurrentUser()).getUid().compareTo(user.getUserID())==0){
                            url = user.getUri();

                            break;
                        }
                    }
                    profilePic = findViewById(R.id.imageViewProfileFragProfileImage);
                    if (url == null){
                        profilePic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.meteor_icon));
                    } else {
                        Picasso.get()
                                .load(url)
                                .fit()
                                .error(R.drawable.meteor_icon)
                                .into(profilePic);
                        setNavPic();
                    }
                });
    }

    public void setNavPic(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userList").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                        User user = document.toObject(User.class);
                        if (Objects.requireNonNull(mAuth.getCurrentUser()).getUid().compareTo(user.getUserID())==0){
                            url = user.getUri();
                            break;
                        }
                    }
                    if (url == null || url.isEmpty()){
                        navPic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.meteor_icon));
                    } else {
                        Picasso.get()
                                .load(url)
                                .fit()
                                .error(R.drawable.meteor_icon)
                                .into(navPic);
                    }
                });
    }

    @Override
    public ArrayList<FireBall> getFireBallList() {
        return fireBallList;
    }

    @Override
    public ArrayList<FireBall> getResetFireBallList() {
        return fireBallList;
    }

    @Override
    public String getSixMonthsAgoDateMapsFrag() {
        Calendar cal = Calendar.getInstance();  //Get current date/month i.e 03 march, 2023
        //Log.d(TAG, "getCurrentDateMapsFrag: cal: " + cal);
        cal.add(Calendar.MONTH, -6);   //Go to date, 6 months ago 27 July, 2011
        //Log.d(TAG, "getCurrentDateMapsFrag: cal: " + cal);
        //cal.set(Calendar.DAY_OF_MONTH, 1); //set date, to make it 1 July, 2011
        //Log.d(TAG, "getCurrentDateMapsFrag: cal: " + cal);
        //Note
        //cal.getTime();//return date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public String getTwelveMonthsAgoDateMapsFrag() {
        Calendar cal = Calendar.getInstance();  //Get current date/month i.e 03 march, 2023
//        cal.set(Calendar.DAY_OF_MONTH, 1); //set date, to make it the 1st
        cal.add(Calendar.MONTH, -12);   //Go to date, 12 months ago
//        cal.getTime();//return date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public ArrayList<FireBall> getFireBallDataGraph() {
        return fireBallList;
    }

    class FireBallRunnable implements Runnable{

        @Override
        public void run() {

            fireBallList.clear();
            Request request = new Request.Builder()
                    .url("https://ssd-api.jpl.nasa.gov/fireball.api")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "onFailure: "+ e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        return dateFormat.format(new Date());
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
        name.setText(R.string.guest);
        navPic.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.meteor_icon));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .addToBackStack(null)
                .commit();
    }

    public void logIn(){
        if (mAuth.getCurrentUser() != null) {
            name.setText(mAuth.getCurrentUser().getDisplayName());
            setNavPic();
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

    @Override
    public void openUpdatePopUp(View view) {
        updateProfileDialog(view);
    }

    @Override
    public void updateProfilePic(View view) {
        setProfilePic(view);
    }

    class NavRunnable implements Runnable{
        MenuItem item;
        
        public NavRunnable(MenuItem item) {
            this.item = item;
        }

        @SuppressLint("NonConstantResourceId")
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
                                .replace(R.id.fragment_container, new MainFragment(3))
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
                case R.id.nav_graph:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new GraphFragment())
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_forum:
                    if (mAuth.getCurrentUser() == null){

                        //HOW TO RUN ON MAIN THREAD
                        runOnUiThread(() -> getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new MainFragment(1))
                                .addToBackStack(null)
                                .commit());

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

