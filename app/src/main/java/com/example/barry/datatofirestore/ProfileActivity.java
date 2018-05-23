package com.example.barry.datatofirestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private static final int CHOOSE_IMAGE = 1001;

    // declare the views
    ImageView imageView;
    EditText editText;
    TextView verifyUserEmail, signUp, mMain;
    Button saveBtn, logOutBtn;

    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        // init the views
        editText = (EditText) findViewById(R.id.edTxtDisplayName);
        imageView = (ImageView) findViewById(R.id.imageView);
        saveBtn = (Button) findViewById(R.id.btnSave);
        signUp = (TextView) findViewById(R.id.signUpTxt);
        mMain = (TextView) findViewById(R.id.mainBtn);

        verifyUserEmail = (TextView) findViewById(R.id.tvVerifyUserEmail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView.setOnClickListener(this);
        editText.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        mMain.setOnClickListener(this);

        logOutBtn = (Button) findViewById(R.id.signOutBtn);
        logOutBtn.setOnClickListener(this);
        logOutBtn.setEnabled(true);

        // loadUserInfo after defining all the views
        loadUserInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) { // if current user is not null
            finish();     // finish here and go to profile Activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // load the user info
    private void loadUserInformation() {

        // Get the firebaseUser
        final FirebaseUser user = mAuth.getCurrentUser();

        // To display user info we need to use Glide library
        // Search Glide on Google, add Glide to gradle, then

        if (user != null) {

            /* ####################### Image Disabled for now ################################
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }*/

            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }
        }
        //String photoUrl = user.getPhotoUrl().toString();
        //String displayName = user.getDisplayName();

        // verify user email (started having null pointer for user.isEmailverified
        // fix change to user != null && user.isEmailVerified() cos if no user signed in give NPE
        if (user != null && user.isEmailVerified()) {
            verifyUserEmail.setText("Verified User");
        }
        else {
            verifyUserEmail.setText("Email not verified (Click to Verify)");
            // add on click listener to enable user click to verify
            verifyUserEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                    Toast.makeText(ProfileActivity.this,
                                            "Verification Email sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    /* ####################### Image Disabled for now ########################################
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get selected image
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriProfileImage = data.getData(); // get and store the image in uriProfileImage

            try {
                // this method can create an exception (and is redlining) so we need to wrap it in a try/catch block
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                // set the imageView and pass it the bitmap to display
                imageView.setImageBitmap(bitmap);
                // now we have the image but we cannot set a local image as profile pic
                // we need to store/ upload the image on fb. So connect to Fb, add cloud storage to Fb
                // tools > Firebase > storage > Upload/Download a file .. > Add Fb Storage
                // now we can uploadImageToFirebase
                uploadImageToFirebaseStorage(); // we havent created this so alt+Enter > create ...
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/

    /* ####################### Image Disabled for now ########################################
    private void uploadImageToFirebaseStorage() {

        // upload the image to fb storage
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profilepics/"+System.currentTimeMillis() + ".zini.jpg");

        // if the ref is not empty, put it in fb
        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE); // show progressBar as image is uploading

            profileImageRef.putFile(uriProfileImage) // put the file and add and implement on success listener
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // check that the task is successful
                            // hide progressbar when loading is completed
                            progressBar.setVisibility(View.GONE);

                            // get the download url and use as user info
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString(); // redlining but working
                            // go save the user info with the save button
                        }
                    }) // add on failure listener
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // hide progressbar when loading is completed
                            progressBar.setVisibility(View.GONE);

                            // give a toast of failure
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                    *//*.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            // check that the task is successful
                            // hide progressbar when loading is completed
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                task.get
                            }
                        }
                    });*//*
        }

    }*/

    private void saveUserInformation() {
        // This uses the Firebase Auth, so declare and init this (had this when i did signout myself)

        // get the text entered by user
        String displayName = editText.getText().toString();

        if (displayName.isEmpty()){
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }

        // get current user from the fb auth
        FirebaseUser user = mAuth.getCurrentUser();

        /* ####################### Image Disabled for now ########################################
        if (user!=null && profileImageUrl!=null) {

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }*/


    }

    /* ####################### Image Disabled for now ########################################
    // create a method for user to choose image
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, //title: "Select Profile Image" giving error
                "title: Select Profile Image"), CHOOSE_IMAGE);
        // now we can call this method in the ImageView so the user can choose an image to display
    }*/

    public void signOut() {
        mAuth.signOut();
        logOutBtn.setEnabled(false);
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(getApplicationContext(), "signed out", Toast.LENGTH_SHORT).show();
    }

    // switch the listeners
    @Override
    public void onClick(View v) {
        // add switch to select the clicked btn
        switch (v.getId()) {
            case R.id.signUpTxt:
                startActivity(new Intent(getApplicationContext(), RegisterUserActivity.class));
                break;

            /*  ####################### Image Disabled for now ###############################
            case R.id.imageView:
                showImageChooser(); // this will open Image chooser for user to choose a pic for profile
                break;*/

            case R.id.edTxtDisplayName:
                break;

            //case R.id.mainBtn:
            //startActivity(new Intent(getApplicationContext(), MainActivity.class));

            case R.id.btnSave:
                saveUserInformation(); // create this method (alt+Enter > create...)
                break;

            /*case R.id.signInBtn:
                userLogin(); // go create this method
                logOutBtn.setEnabled(true);
                break;*/

            case R.id.signOutBtn:
                signOut();
                break;
        }

    }
}
