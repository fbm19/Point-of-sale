package com.sifast.appsocle.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.internal.client.ThinAdSizeParcel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Location;

/**
 * Created by Asus on 29/06/2016.
 */

public class MarkerInsertionTask extends AsyncTask {

    private GoogleMap mMap;
    Activity activity;

    public MarkerInsertionTask(GoogleMap mMap,Activity  activity) {
        this.activity=activity;
        this.mMap = mMap;
    }

    public void connect() {
        //TODO pass the context so i can call getResources
        //setting connexion parameter
        String dbUrlPointOfSale=activity.getResources().getString(R.string.dbUrlPointOfSale);
        final Firebase ref = new Firebase(dbUrlPointOfSale);
        Query query = ref.orderByChild("longitude");

        //get the data from the DB
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //checking if the user exist
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        //get each user which has the target username
                        Location location = userSnapshot.getValue(Location.class);
                        LatLng markerLatLon=new LatLng(location.getLatitude(), location.getLongitude());
                        String markerTitle="Point Of sale";
                        String markerSnippet="Point Of sale";
                        mMap.addMarker(new MarkerOptions()
                                .position(markerLatLon)
                                .title(markerTitle)
                                .snippet(markerSnippet)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        //if the password is true , the data will be storaged in the sharedPreferences file and a Home activity will be launched


                    }

                } else {

                    Toast.makeText(activity.getApplicationContext(),"You have no markers to load",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Can't load markers","Markers loading problem");

            }
        });
    }

    @Override
    protected Object doInBackground(Object[] params) {
        connect();
        return null;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }
}
