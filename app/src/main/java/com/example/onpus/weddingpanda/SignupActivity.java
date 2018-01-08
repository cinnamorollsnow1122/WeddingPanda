package com.example.onpus.weddingpanda;

import android.content.Intent;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onpus.weddingpanda.constant.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.OnCheckedChanged;
import rx.Subscription;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;



    @BindView(R.id.input_name)
    EditText nameText;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.btn_signup)
    Button signupButton;
    @BindView(R.id.progressbar)
    ProgressBar loading;
    @BindView(R.id.link_login)
    TextView loginLink;

    String usertype;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();




    }

    @OnClick({R.id.link_login, R.id.btn_signup})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_signup) {
            signup();
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        }
        if (view.getId() == R.id.link_login) {
            startActivity(new Intent(SignupActivity.this, LoginAct.class));
        }
    }



    @OnCheckedChanged({R.id.couple, R.id.guest})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if(checked) {
            switch (button.getId()) {
                case R.id.guest:
                    // do stuff
                    usertype = "guest";
                    break;
                case R.id.couple:
                    // do stuff
                    usertype = "couple";
                    break;
            }
        }
    }

    public void signup() {
        final String name = nameText.getText().toString().trim().toLowerCase();
        final String email = emailText.getText().toString().trim().toLowerCase();
        String password = passwordText.getText().toString().trim().toLowerCase();
        //register(name, email, password);
        //vaild

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewUser(name,email);
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loading.setVisibility(View.GONE);
    }

    private void writeNewUser(String name, String email){
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user=new User(name,email,usertype);
        //mDatabase.child("users").push().setValue(user);
        Log.d("id",userId);
        mDatabase.child("Users").child(userId).setValue(user);
    }


  }



