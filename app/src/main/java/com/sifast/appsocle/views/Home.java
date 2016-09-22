package com.sifast.appsocle.views;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;
import com.sifast.appsocle.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private User authUser;
    String actionBarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);


        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#9887B6"));
        }

        actionBarTitle="Feedbacks";
        setTitle(actionBarTitle);

//check if the user is connected with google
        try {
    if (!FirebaseAuth.getInstance().getCurrentUser().equals(null)) {

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.icSettings).setVisible(false);
    }
}
catch (Exception myExp){
    Menu nav_Menu = navigationView.getMenu();
    nav_Menu.findItem(R.id.icSettings).setVisible(true);
}
        //load feedback
        ProgressDialog progress = ProgressDialog.show(this, "Loading your feedbacks",
                "wait ...", true);
        loadFeedbacks();
        progress.dismiss();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO test this
        //get the auth user is data
        String username = sharedPreferences.getString("username", null);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        authUser = new User(username, email, password, null, null);



    }

    public void loadFeedbacks() {

        //load feedback
        ProgressDialog progress = ProgressDialog.show(this, "Loading your feedbacks",
                "wait ...", true);
        //get connected user
        String username;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //TODO test this
        username = sharedPreferences.getString("username", null);
        User user = new User(username, null, null, null, null);
        //setting connexion parameter
        Firebase.setAndroidContext(getApplicationContext());
        String dbFeedbackUrl=getApplicationContext().getResources().getString(R.string.dbUrlFaeedbacks);;
        final Firebase ref = new Firebase(dbFeedbackUrl);
        Query query = ref.orderByChild("declaredBy").equalTo(user.getUsername());

        //get the data from th DB
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Feedback> feedbacksList = new ArrayList<Feedback>();
                fragmentManager = getFragmentManager();
                //checking if the feedback exist
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        //get each feedback

                        Feedback feedback = userSnapshot.getValue(Feedback.class);
                        feedbacksList.add(feedback);
                        FeedbackListFragment listFragment = new FeedbackListFragment(feedbacksList);
                        fragmentManager.beginTransaction().replace(R.id.myCont, listFragment).commit();

                    }

                } else {
                    FeedbackListFragment listFragment = new FeedbackListFragment(feedbacksList);
                    fragmentManager.beginTransaction().replace(R.id.myCont, listFragment).commit();
                    Toast.makeText(getApplicationContext(),"you have 0 alert",Toast.LENGTH_LONG).show();
                }

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Feedbacks Problem","Check the db Connexion");           }
        });

        progress.dismiss();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        TextView txtUsernameMenu= (TextView) findViewById(R.id.txtUsernameMenu);
        txtUsernameMenu.setText("The connected user :"+authUser.getUsername());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut) {

            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.editor = sharedPreferences.edit();
        //logout and clearing the shared prefernces file
        this.editor.clear();
        this.editor.commit();
        FirebaseAuth.getInstance().signOut();
        //open the login activity after logging out
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        //stop returning to home
        this.finish();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.icHistory) {


            loadFeedbacks();
            actionBarTitle="Feedbacks";
            setTitle(actionBarTitle);

        } else if (id == R.id.icPin) {
            LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //GPS Provider disabled
                Toast.makeText(getBaseContext(),"please enable the gps",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            //open map fragment
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.myCont, new GMapFragment()).commit();
            actionBarTitle="Géolocation";
            setTitle(actionBarTitle);
        } else if (id == R.id.icSettings) {


            FragSettings frag = new FragSettings(authUser);
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.myCont, frag).commit();
            actionBarTitle="Settings";
            setTitle(actionBarTitle);

        } else if (id == R.id.icStar) {
            launchMarket();
        } else if (id == R.id.icInfo) {
            //open the about fragment
            About about = new About();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.myCont, about).commit();
            actionBarTitle="About";
            setTitle(actionBarTitle);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
