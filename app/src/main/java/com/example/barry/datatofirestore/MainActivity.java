package com.example.barry.datatofirestore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentListenOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

/**
 * https://www.youtube.com/watch?v=7hwlMKUgTQc
 * https://youtu.be/7hwlMKUgTQc
 * 1 create the project
 * 2. add the views to the xml file,
 * and connect them in the java file
 * 3. Connect to Firebase Firestore
 * 4. add onClick listener to the btn
 * 5. add a ref to the firestore then init the firestore in onCreate
 * 6. to store data to the firestore we need a hashmap, create one in the onClick
 * 7. Run , Enter data and see it appear in the db.
 * -------------------------------------------------------------------
 * Part 2: Retrieve data from the FireStore
 * <p>
 * https://www.youtube.com/watch?v=VoOLBxN-ln4
 * ____________________________________________________________________
 * 1. Add a new button to load the data from the store
 * 2. init the button
 * 3. add a click listener to the btn (feel free to remove the on success listener from the
 * save btn as we will get an error message if not successful)
 * 4. Run the app, type a name that is on the db, click load and see it loaded
 * (not working for me)
 * But we want the data real time without click so
 * 5. Override an onStart()
 * +==================================================
 * Add Firebase Auth to app
 * +++++++++++++++++++++++++++++++++++++++++++++++
 * 1. create a Profile Activity and a RegisterUserActivity Activity
 * 2. Create the corresponding views
 * 3. Add to firebase Auth (See app: FirebaseRegistration)
 */
public class MainActivity extends AppCompatActivity {// implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    //private static final String FIRE_LOG = "Fire-log";

    private EditText mMainText;
    private EditText mStatusText;
    private Button mSaveBtn;

    // part 2-1
    private TextView mWelcomeText;
    private Button mLoadBtn;

    // ref the firestore

    private FirebaseFirestore mFirestore;

    //@@@@@@@@@@@@@SignIn Register Start @@@@@@@@@@@@@@@@@@@@@@@@@@@

    FirebaseAuth mAuth;

    /*
    // def the views
    EditText editTextUserName, editTextEmail, editTextPassword;
    Button logOutBtn;
    TextView verifyUserEmail;
    ProgressBar progressbar;*/
    //@@@@@@@@@@@@@  End SignIn Register @@@@@@@@@@@@@@@@@@@@@@@@@@@

    // part 2-5.0 Override on Start
    // To get data real time we override onStart
    @Override
    protected void onStart() {
        super.onStart();

        //@@@@@@@@@@@@@ SignIn Register Start @@@@@@@@@@@@@@@@@@@@@@@@@

        /*if (mAuth.getCurrentUser() != null) { // if current user is not null
            finish();     // finish here and go to profile Activity
            startActivity(new Intent(this, ProfileActivity.class));
        }*/
        //@@@@@@@@@@@@@ SignIn Register END @@@@@@@@@@@@@@@@@@@@@@@@@

        // part 2-5.3 to get notified even when data is added locally or on the server
        // metadataChannges is what notifies us if data is on the server or saved locally
        DocumentListenOptions documentListenOptions = new DocumentListenOptions();
        documentListenOptions.includeMetadataChanges();
        // then add this documentListenOptions to the line below inside:
        // .addSnapshotListener()
        // also add the activity context(this) so it will not run in the bgrd when is is paused

        // part 2-5.1
        //mFirestore.collection("users").document("name")
        mFirestore.collection("users").document("one") // instead of get we use eventlistener
                .addSnapshotListener(this, documentListenOptions, // part 2-5.5 (see 2-5.3 notes)
                        new EventListener<DocumentSnapshot>() {
                            @Override
                            // we already have document snapshot so we dont need to retrieve it from our task()
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                                // part 2-5.2
                                if (documentSnapshot.exists() && documentSnapshot != null)
                                {

                                    // retrieve the data from the snapshot, store it as username
                                    String username = documentSnapshot.getString("name");
                                    mWelcomeText.setText("Welcome: " + username); // use it to change the welcome msg

                                    // 2-5.4 check if data is pending locally,
                                    if (documentSnapshot.getMetadata().hasPendingWrites()) {

                                        // if data is pending locally, set color to light
                                        mWelcomeText.setTextColor(getColor(R.color.colorLight));
                                    }
                                    else { // if stored on the db, set color to dark
                                        mWelcomeText.setTextColor(getColor(R.color.colorDark));
                                    }
                                }

                            }
                        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init the firestore
        mFirestore = FirebaseFirestore.getInstance();

        mMainText = (EditText) findViewById(R.id.nameTxt);
        mStatusText = (EditText) findViewById(R.id.statusTxt);
        mSaveBtn = (Button) findViewById(R.id.submitBtn);
        // part 2-2
        mLoadBtn = (Button) findViewById(R.id.loadBtn);
        mWelcomeText = (TextView) findViewById(R.id.welcomeTxt);

        //@@@@@@@@@@@@@ SignIn Register Start @@@@@@@@@@@@@@@@@@@@@@@@@
        /*// init / get the instance of fb Auth
        mAuth = FirebaseAuth.getInstance();

        // init the views
        editTextEmail = (EditText) findViewById(R.id.txtEmail);
        editTextPassword = (EditText) findViewById(R.id.txtPassword);
        editTextUserName = (EditText) findViewById(R.id.txtUsername);

        verifyUserEmail = (TextView) findViewById(R.id.tvVerifyUserEmail);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        // set onclick listener to the signUp text
        findViewById(R.id.signUpTxt).setOnClickListener(this);

        // set onclick listener to the Login/logout button
        findViewById(R.id.signInBtn).setOnClickListener(this);
        // a diff way of doing the above
        logOutBtn = (Button) findViewById(R.id.signOutBtn);
        logOutBtn.setOnClickListener(this);
        logOutBtn.setEnabled(false);

        //setupFirebaseAuth();
        // sendVerificationEmail();*/
        //@@@@@@@@@@@@@ SignIn Register End @@@@@@@@@@@@@@@@@@@@@@@@@@@

        // Retrieving data
        // part 2-3
        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFirestore.collection("users").document("one").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                Log.d(TAG, "onComplete: attempting to load data");
                                // part 2-3.1
                                if (task.isSuccessful()) {

                                    // get the doc snapshot from task
                                    // ***and we can retrieve any doc we want from this result***
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    // part 2-3.2 - check if the Document snapshot exist before proceeding
                                    if (documentSnapshot.exists() && documentSnapshot != null) {
                                        Log.d(TAG, "onComplete: data added to db");

                                        // get the string and store it in username
                                        String username = documentSnapshot.getString("name");
                                        // mWelcomeText.setText("Welcome: " + username );
                                        // name is what we need from db, can do this for any other string needed

                                        //===== my adjustment for multi strings / multi lines ======
                                        String user_status = documentSnapshot.getString("status"); // mine
                                        String userData = username + "\n" + user_status;
                                        // then changed
                                        // change the page welcome text            \\ my test add status: works
                                        // mWelcomeText.setText("Welcome: " + username ); //+ "\n" + user_status);
                                        // to
                                        mWelcomeText.setText(userData);
                                        // ======= adjustment ends ===================================
                                        //Run : This should give us a value when we push the load button, if the data exist

                                    } else {
                                        //Todo
                                    }
                                } else {

                                    Log.d(TAG, "onComplete: " + task.getException().getMessage());

                                }
                                //end part 2-3.1 Run the app, click the load btn and see the message
                            }
                        });
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 1st codes using onSuccess and onFailure listeners: working
                // get the text from the edit text
                String username = mMainText.getText().toString();
                String user_status = mStatusText.getText().toString();


                // create the hashMap to send data to the store
                Map<String, String> userMap = new HashMap<>();

                // add data to the map
                // name must be same as the string in the firestore, username def above
                userMap.put("name", username);
                userMap.put("status", user_status);
                userMap.put("image", "image_link"); // adjust "image_link" accordingly for dynamic use

                // add the map to the firestore
                "users" is the collection in firestore, if we dont have one, this will auto create
                //mFirestore.collection("users").add(userMap);
                // add the map to the firestore + add onSuccess listener to know data added successfully
                mFirestore.collection("users").add(userMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this,
                                "username added to db", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() { // check for failure
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();

                        Toast.makeText(MainActivity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });*/

                // above has onSuccess and onFailure, we only need a message when we fail so now ff
                String mainText = mMainText.getText().toString();
                String status_text = mStatusText.getText().toString();

                Map<String, String> dataToAdd = new HashMap<>();
                dataToAdd.put("name", mainText);
                dataToAdd.put("status", status_text); // mine

                // to add single value to the db, to add more use the old commented above line here
                //mFirestore.collection("users").add(dataToAdd)
                mFirestore.collection("users").document("one").set(dataToAdd)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this,
                                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        //############# Handle listen Error: From Dev site =====================
        /*mFirestore.collection("users").document("one")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "listen: error", e);
                            return;
                        }

                        for (DocumentChange doc : documentSnapshot.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New user: " + doc.getDocument().getData());
                            }
                        }
                    }
                });*/
        // ======================= End +===========================================

    }


}
