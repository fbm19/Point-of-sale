package com.sifast.appsocle.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sifast.appsocle.R;

public class Splash extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    //this class is called only in the begining : the first activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);

        final ImageView imageView = (ImageView) findViewById(R.id.imageViewSplash);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade);
        imageView.startAnimation(animation);


        //Getting the sharedprefrences parameter
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = sharedPreferences.getString("username", null);

          /*cheking if the user is already logged in*/
        animation.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (username != null) {
                    //instanciate the UI thread
                    SharedPreferences.Editor  editor = sharedPreferences.edit();
                    //open the home activity
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);


                }
                //if the user is not logged in
                else {
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);

                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
