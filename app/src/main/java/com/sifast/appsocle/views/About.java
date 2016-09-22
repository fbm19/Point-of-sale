package com.sifast.appsocle.views;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sifast.appsocle.R;


public class About extends Fragment {

    //the text view which is holding the version
    private TextView txtVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        versioDisplay();
        super.onViewCreated(view, savedInstanceState);

    }

    public void versioDisplay() {
        //a function to display the version in the txt view
        //TODO handle this exception add a dialog or something like that
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Gradle Problem","Can t get version");
        }
        String version = packageInfo.versionName;
        txtVersion = (TextView) getView().findViewById(R.id.txtVersion);
        txtVersion.setText(txtVersion.getText() + " " + version);

    }
}
