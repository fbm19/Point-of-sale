package com.sifast.appsocle.views;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;
import com.sifast.appsocle.models.PointOfSale;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.tasks.MarkerInsertionTask;
import com.sifast.appsocle.tasks.FeedbackSendingTask;

import java.util.Date;


public class GMapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmentgmap, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //TODO  handle this api version exception
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //puting the map in the fragment
            MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.myMap2);
            fragment.getMapAsync(this);


        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void onLocationChanged(Location location) {
        //function called when the location is changed

        try {

            mMap.clear();
        } catch (Exception e) {
            Log.e("Map Eroor","Can't load the map");
        }
        LatLng mypos = new LatLng(location.getLatitude(), location.getLongitude());

        //camera annimation
        int zoomValue=70;
        int bearing=45;
        int titl=65;
        String title="here I m ";
        CameraPosition camPos = new CameraPosition.Builder().target(mypos)
                .zoom(zoomValue)
                .bearing(bearing)
                .tilt(titl)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);
        mMap.addMarker(new MarkerOptions().position(mypos).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity().getApplicationContext(),"Please activate your Gps",Toast.LENGTH_LONG).show();
    }


    void getMyLocation() {
        //function called to get the current location
        // definition of the location manager

        LocationManager locManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        //definition of the listenner
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            // Called when a new location is found by the network location provider.
            public void onLocationChanged(Location location) {
                int zoomValue=70;
                int bearing=45;
                int titl=65;
                // setting the camera of the map to insert the marker in the current position
                LatLng myLaLn = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                        .zoom(zoomValue)
                        .bearing(bearing)
                        .tilt(titl)
                        .build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(camUpd3);

                //  setting the marker in the current position
                String markerTitle = "My position";
                String snippet = "Population: 776733";
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(markerTitle)
                        .snippet(snippet));

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
               // Called when the provider is disabled by the user.
                Toast.makeText(getActivity().getApplicationContext(),"Please activate your Gps",Toast.LENGTH_LONG).show();
            }
        };
        //set the frequency of updates
        int frequency=6000;
        int minPositionChnegment=5;
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, frequency, minPositionChnegment, locationListener);
        //check the permission
        Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getMyLocation();
        final Date date = new Date();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                          @Override
                                          public boolean onMarkerClick(final Marker marker) {
                                              //  Take some action here
                                              String titleDialog="Feedback";
                                              dialog = new Dialog(getActivity());
                                              dialog.setContentView(R.layout.fragment_feedback);
                                              dialog.setTitle(titleDialog);


                                              Button butCancel = (Button) dialog.findViewById(R.id.butCancelFeedback);
                                              Button  butSendFeedBack = (Button) dialog.findViewById(R.id.butSendFeedback);

                                              butSendFeedBack.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {

                                                      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                                      String username = sharedPreferences.getString("username", null);
                                                      User user = new User(username, null, null, null, null);
                                                      EditText txtFeedback = (EditText) dialog.findViewById(R.id.txtFeedback);
                                                      String comment = txtFeedback.getText().toString();
                                                      PointOfSale pointOfSale = new PointOfSale(new com.sifast.appsocle.models.Location(marker.getPosition().longitude, marker.getPosition().latitude));

                                                      //decalre the feedback object and set it's attribute
                                                      Feedback feedback = new Feedback();
                                                      feedback.setDeclaredBy(username);
                                                      feedback.setDeclaredLat(String.valueOf(pointOfSale.getLocation().getLatitude()));
                                                      feedback.setDeclaredLong(String.valueOf(pointOfSale.getLocation().getLongitude()));
                                                      feedback.setDeclartionDate(String.valueOf(date.getTime()));
                                                      feedback.setMessageDeclared(comment);

                                                      FeedbackSendingTask sendFeedbackTask = new FeedbackSendingTask(feedback, getActivity());
                                                      sendFeedbackTask.execute();
                                                      dialog.dismiss();
                                                      Toast.makeText(getActivity().getApplicationContext(), "Your feedback was succesfully sent", Toast.LENGTH_LONG).show();

                                                  }
                                              });
                                              butCancel.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      dialog.dismiss();
                                                  }
                                              });
                                              dialog.show();
                                              return true;
                                          }

                                      }
        );

        //loading all the markers
        MarkerInsertionTask insertMarkerTask = new MarkerInsertionTask(mMap,getActivity());
        insertMarkerTask.execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }
}
