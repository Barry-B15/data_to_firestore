package com.example.barry.datatofirestore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RegisterUserActivity";

    // def the views
    EditText editTextUserName, editTextEmail, editTextPassword;
    ProgressBar progressbar;
    private TextView verifyUserEmail;
    private TextView loginTxt;
    private Button registerBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // init the views
        editTextEmail = (EditText) findViewById(R.id.txtEmail);
        editTextPassword = (EditText) findViewById(R.id.txtPassword);
        editTextUserName = (EditText) findViewById(R.id.txtUsername);
        loginTxt = (TextView)  findViewById(R.id.txtLogin);
        registerBtn = (Button) findViewById(R.id.btnRegister);

        //verifyUserEmail = (TextView) findViewById(R.id.tvVerifyUserEmail);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        // init mAuth
        mAuth = FirebaseAuth.getInstance();

        //findViewById(R.id.btnRegister).setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        //findViewById(R.id.txtLogin).setOnClickListener(this);
        loginTxt.setOnClickListener(this);
        //verifyUserEmail.setOnClickListener(this);

        Log.d(TAG, "onCreate: started");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnRegister:
                registerUser(); // red, create this method
                break;

            case R.id.txtLogin:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void registerUser() {
        // 1st get user details
        //String username = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // validate email data
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        // check for valid email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        // validate password data
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        // check for min length password (android recommends at least 6)
        if (password.length()<6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        // while registering user, progressbar is visible
        progressbar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override // when user registration is completed, this method is called
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // when registration is completed, progressbar is invisible
                        progressbar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // finish() if task is successful
                            finish();

                            //and take user to profile screen
                    /*Intent intent = new Intent(RegisterUser.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // this will clear all activities in the stack and open a new one.
                    // otherwise if user pushes down button from the Profile, it will take the user back to login activity
                    startActivity(intent);*/

                            //11/17 ###########################################
                            sendVerificationEmail();
                            mAuth.getInstance().signOut();
                            redirectToLoginScreen();
                            //startActivity(new Intent(RegisterUser.this, MainActivity.class)); // shorter code
                        }
                        else { // user already exists on db
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(),
                                        "User already exist", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterUserActivity.this,
                                        RegisterUserActivity.class));
                                editTextEmail.requestFocus();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        // be sure to enable email and password registration in fb
                    }
                });

    }

    private void redirectToLoginScreen() {
        Log.d(TAG, "redirectToLoginScreen: redirecting to login screen");

        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //33333333 trying to send email to user 11/17 #########################
    public void sendVerificationEmail() {

        // get fb
        final FirebaseUser user = mAuth.getInstance().getCurrentUser();

       /* // verify user email
        if (user.isEmailVerified()) {
            verifyUserEmail.setText("Verified User");
        }
        else { //############## verifyUserEmail not yet init ####################
            verifyUserEmail.setText("Email not verified (Click to Verify)");
            // add on click listener to enable user click to verify
            verifyUserEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            Toast.makeText(RegisterUser.this,
                                    "Verification Email sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }*/
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                                //startActivity(new Intent(RegisterUser.this, MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Couldn't send email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}
