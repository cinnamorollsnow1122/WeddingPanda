package com.example.onpus.weddingpanda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onpus.weddingpanda.constant.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginAct extends AppCompatActivity {

    private static final String TAG = "LoginAct";
    private static final int REQUEST_SIGNUP = 0;
    private String userUID;
    private boolean mlogin;
    private DatabaseReference databaseRef;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;


    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.link_signup)
    TextView signupLink;
    @BindView(R.id.pbHeaderProgress)
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            mlogin = true;
            if (mlogin) {
                databaseRef.child("Users").child(auth.getCurrentUser().getUid()).child("userType").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Log.i("value",""+snapshot.getValue(String.class));
                        String type = snapshot.getValue(String.class);
                        if (type.equalsIgnoreCase("couple")) {
                            startActivity(new Intent(LoginAct.this, MainActivity.class));
                            mlogin = false;
                            progressBar.setVisibility(View.VISIBLE);
                            finish();
                        } else {
                            startActivity(new Intent(LoginAct.this, MainGuestActivity.class));
                            mlogin = false;
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(
                    @NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    Log.d("onAuthStateChanged", "登入:"+
                            user.getUid());
                    userUID =  user.getUid();
                }else{
                    Log.d("onAuthStateChanged", "已登出");
                }
            }
        };
    }



    @OnClick(R.id.link_signup)
    public void linktosignup(){
        startActivity(new Intent(LoginAct.this, SignupActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void login(){
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginAct.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(LoginAct.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();

                        } else {
                            mlogin = true;
                            if(mlogin) {
                                databaseRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            UserA users = child.getValue(UserA.class);
                                            if (users.getEmail().equalsIgnoreCase(email)) {
                                                if (users.getUserType().equalsIgnoreCase("couple")) {
                                                    startActivity(new Intent(LoginAct.this, MainActivity.class));
                                                    mlogin = false;
                                                    finish();
                                                } else {
                                                    startActivity(new Intent(LoginAct.this, MainGuestActivity.class));
                                                    mlogin = false;
                                                    finish();
                                                }
                                            }
                                        }
                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    public static class UserA {


        private String name;
        private String email;
        //    private String password;
        private String userType;

        public UserA(){

        }

        public UserA(String email, String userType) {
            this.email = email;
            this.userType = userType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

        public void setUserType(String userType) { this.userType = userType;}
        public String getUserType(){ return userType;}


    }
}


