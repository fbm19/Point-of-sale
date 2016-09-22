package com.sifast.appsocle.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.tasks.ConnectionTask;
import com.sifast.appsocle.tasks.GoogleConnectionTask;
import com.sifast.appsocle.tasks.RememberMeTask;
import com.sifast.appsocle.tasks.PasswordChangmentTask;

import java.util.regex.Pattern;

import static com.sifast.appsocle.R.id.txtViewLinKForgetPass;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Login";
    private static final int RC_Sigin_In = 9001;
    boolean checkInputs = true;
    private Dialog dialogMail;
    private EditText txtMailDialog;
    private Button butReset;
    private String mail;
    private Dialog dialog;
    private TextView txtViewRegister;
    private EditText txtUsername, txtPassword;
    private Button butLogin;
    private com.google.android.gms.common.SignInButton googleButton;
    private CheckBox checkRememberMe;
    private SharedPreferences sharedPreferences;
    private TextView txtViewForgotPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient mGoogleApiClient;
     private  static final  int RC_SIGN_IN=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtViewRegister = (TextView) findViewById(R.id.txtViewLinkRegister);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        googleButton = (SignInButton) findViewById(R.id.sign_in_button);
        butLogin = (Button) findViewById(R.id.butLogin);
        txtViewForgotPassword = (TextView) findViewById(txtViewLinKForgetPass);
        checkRememberMe = (CheckBox) findViewById(R.id.checkRememberMe);
        this.sharedPreferences = getSharedPreferences("rememberMePreferences", getApplicationContext().MODE_PRIVATE);
        openRegister();
        logIn();
        autoFill();
        resetPassword();
        //
        mAuth=FirebaseAuth.getInstance();
        //check if the user is connecting with google
        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() !=null){


                    String username = mAuth.getCurrentUser().getDisplayName();
                    String email= mAuth.getCurrentUser().getEmail();
                    User authentificatedUser = new User(username, null, email, null, null);
                    GoogleConnectionTask googleConnectionTask = new GoogleConnectionTask(authentificatedUser, Login.this, null);
                    googleConnectionTask.execute();
                }
            }
        };



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this,new GoogleApiClient.OnConnectionFailedListener(){

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getApplicationContext(),"u got an error",Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            signIn();

            } });






    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


            } else {
                // Google Sign In failed, update UI appropriate
                Toast.makeText(getApplicationContext(),"u failed to connect with google",Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void openRegister() {
        //a function called once you click on register button
        txtViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening the home activity
                Intent i = new Intent(getApplicationContext(), Registeration.class);
                startActivity(i);
            }
        });
    }

    private boolean checkEmailRegularExp(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void checkEmail(final String mail) {
        //this function allow to check if the email exist
        Firebase.setAndroidContext(getApplicationContext());
        if (checkEmailRegularExp(mail)) {
            //setting connexion parameter
            String dbUrlUsers=getBaseContext().getResources().getString(R.string.dbUsersUrl);
            final Firebase ref = new Firebase(dbUrlUsers);
            Query query = ref.orderByChild("email").equalTo(String.valueOf(mail));


            //get the data from th DB
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //checking if the user exist
                    if (!dataSnapshot.exists()) {
                        checkInputs = false;

                    }


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                Log.e("Email problem","Can't check the email");
                }
            });
        } else checkInputs = false;
    }

    public void resetPassword() {

        final EditText[] txtMail = new EditText[1];
        mail = null;

        //a function called once you click on register button
        txtViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleForgetPasswordDialog="Forgot password";
                dialogMail = new Dialog(Login.this);
                dialogMail.setContentView(R.layout.dialogmail);
                dialogMail.setTitle(titleForgetPasswordDialog);
                dialogMail.show();
                butReset = (Button) dialogMail.findViewById(R.id.butReset);
                //the click listener of but ok in the dialog
                butReset.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Firebase.setAndroidContext(getApplicationContext());
                        txtMail[0] = (EditText) dialogMail.findViewById(R.id.txtMailDialog);
                        mail = txtMail[0].getText().toString();
                        checkEmail(mail);
                        if (checkInputs) {
                            PasswordChangmentTask resetPasswordTask = new PasswordChangmentTask(Login.this, mail);
                            resetPasswordTask.execute();
                            dialogMail.dismiss();
                        } else
                        { String errorMsgEmail="this mail is not valid";
                            txtMail[0].setError(errorMsgEmail);
                        }
                    }
                });


            }
        });
    }

    public void logIn() {
//function to login the user
        butLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Firebase.setAndroidContext(getApplicationContext());
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                User authentificatedUser = new User(username, password, null, null, null);
                ConnectionTask connectTask = new ConnectionTask(authentificatedUser, Login.this, null);
                connectTask.execute();
                if (checkRememberMe.isChecked()) {
                    RememberMeTask rememberMeTask = new RememberMeTask(sharedPreferences, authentificatedUser);
                    rememberMeTask.execute();
                }



            }
        });


    }

    public void autoFill() {
//a function called to autoFill in case the username and password are registered in the sharedPreferences file
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String memorizedPass=sharedPreferences.getString(txtUsername.getText().toString() + "password", null);
                txtPassword.setText(memorizedPass);
                checkRememberMe.setChecked(true);
            }


        };
        txtUsername.addTextChangedListener(fieldValidatorTextWatcher);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthStateListener);
        super.onStart();
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    public static String getTAG() {
        return TAG;
    }

    public static int getRC_Sigin_In() {
        return RC_Sigin_In;
    }
}
