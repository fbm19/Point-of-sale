package com.sifast.appsocle.views;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.tasks.UserDataSettingsTask;

import java.util.regex.Pattern;


public class FragSettings extends Fragment {

    private EditText txtResetEmail,txtNewPassword,txtRepeatPassword,txtCurrentPassword;
    private Button butEditMail,  butSaveData;
    private User authUser;
    private SharedPreferences sharedPreferences;
    private String email, password, username;
    boolean checkInputs = true;

    public FragSettings(User authUser) {
        this.authUser = authUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_settings, container, false);
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        //initialisation des zones textes
        txtResetEmail = (EditText) getActivity().findViewById(R.id.txtResetMail);
        butSaveData = (Button) getActivity().findViewById(R.id.butSaveEdite);
        txtNewPassword= (EditText) getActivity().findViewById(R.id.txtNewPassword);
        txtRepeatPassword= (EditText) getActivity().findViewById(R.id.txtRenterNewPassword);
        txtCurrentPassword=(EditText) getActivity().findViewById(R.id.txtCurrentPassword);
        //récpération des données du sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = sharedPreferences.getString("username", null);
        password = sharedPreferences.getString("password", null);
        email = sharedPreferences.getString("email", null);
        txtResetEmail.setText(email);

        //calling the procedures
        checkEmail();
        checkCurrentPassword();
       checkRepeatPassword();
        checkPasswordLength();
        saveData();
        super.onViewCreated(view, savedInstanceState);

    }
public void checkCurrentPassword(){



    TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(! String.valueOf(txtCurrentPassword.getText()).equals(password)){
                    txtCurrentPassword.setError("Wrong Password");
                    checkInputs=false;
                }
                else checkInputs=true;

        }
    };
    txtCurrentPassword.addTextChangedListener(fieldValidatorTextWatcher);
}
    public void saveData() {

        Firebase.setAndroidContext(getActivity());
        butSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = txtResetEmail.getText().toString();
                String newPassword = String.valueOf(txtNewPassword.getText());
                UserDataSettingsTask setUserData = new UserDataSettingsTask(newEmail, newPassword, username,getActivity());
                setUserData.execute();
            }
        });

    }
    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void checkEmail() {

        Firebase.setAndroidContext(getActivity());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (validEmail(String.valueOf(txtResetEmail.getText().toString()))) {
                    //setting connexion parameter
                    String dbUsersUrl=getActivity().getResources().getString(R.string.dbUsersUrl);
                    final Firebase ref = new Firebase(dbUsersUrl);
                    Query query = ref.orderByChild("email").equalTo(String.valueOf(txtResetEmail.getText().toString()));


                    //get the data from th DB
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //checking if the user exist
                            if (dataSnapshot.exists()) {

                                txtResetEmail.setError("This email was token");
                                checkInputs=false;
                            }
                            else checkInputs=true;


                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } else {
                    txtResetEmail.setError("wrong email adress");
                    checkInputs=false;
                }

            }
        };
        txtResetEmail.addTextChangedListener(fieldValidatorTextWatcher);


    }

    public void checkPasswordLength() {
final int minPasswordLength=5;
        Firebase.setAndroidContext(getActivity());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txtNewPassword.getText().length() < minPasswordLength) {
                    txtNewPassword.setError("this password is too short");
                    checkInputs = false;
                }
                else  checkInputs=true;

            }




        };
        txtNewPassword.addTextChangedListener(fieldValidatorTextWatcher);


    }

    public void checkRepeatPassword() {

        Firebase.setAndroidContext(getActivity());

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!String.valueOf(txtNewPassword.getText()).equals(String.valueOf(txtRepeatPassword.getText()))) {
                    txtRepeatPassword.setError("passwords are not alike");
                    checkInputs = false;
                }
                else checkInputs=true;

            }


        };
        txtRepeatPassword.addTextChangedListener(fieldValidatorTextWatcher);


    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
