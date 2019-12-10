package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class UserProfileSetupActivity extends AppCompatActivity {

    private EditText username, userage;
    String useremail;
    private RadioButton usermale, userfemale, userneutral;
    private Button submitButton;
    Boolean nameExists;


    String usergenderString;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference userFolderStoRef = firebaseStorage.getReference().child("user/");
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userListDataRef = firebaseDatabase.getReference("user_list/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_setup);


        bindViews();



        useremail = firebaseUser.getEmail();

        //TODO EXTRA- FOCUSLISTENER
       /* username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("focus", "onCreate:  ");

                if (!hasFocus){
                    userListDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.d("data", "onDataChange:  " + dataSnapshot.hasChild(username.getText().toString()));

                            nameExists = dataSnapshot.hasChild(username.getText().toString());

                            if( nameExists || username.getText().toString().equals("")) {
                                username.setError("The username you picked is unavailable");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });*/


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                createUserProfile(username, userage, useremail, usergenderString);


            }
        });



    }



    private void bindViews() {
        submitButton = findViewById(R.id.user_profile_submit_button);
        username = findViewById(R.id.user_profile_username);
        userage = findViewById(R.id.user_profile_age);
        usermale = findViewById(R.id.male_radio);
        userfemale = findViewById(R.id.female_radio);
        userneutral = findViewById(R.id.neutral_radio);

    }




    //TODO 1: Demonstrate different method of linking onClick to View
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.male_radio:
                if (checked)
                    Log.d("RADIO", "onRadioButtonClicked: TEXT");
                usergenderString ="male";

                break;
            case R.id.female_radio:
                if (checked)
                    usergenderString ="female";

                break;


            case R.id.neutral_radio:
                if(checked)
                    usergenderString ="neutral";

                break;


            default:
                usergenderString ="none";
                break;
        }

    }

    //Create User Profile
    //TODO 2 : ADD LOGIC
    private void createUserProfile(EditText username, EditText userage, String useremail , String usergenderString) {

        String usernameString = username.getText().toString();
        String userageString = userage.getText().toString();
        Log.d("Upload", " usergender  " +usergenderString);


        if (usergenderString != null && !usernameString.isEmpty() && !userageString.isEmpty()) {

            UserModel newUser = new UserModel(usernameString, usergenderString, useremail, userageString);


            checkUserNameAvailability(usernameString, newUser);




        }  else {


            showErrorDialog("Oops ! " , "Please complete the form.");
        }


    }


    //TODO 3: UserProfile Upload
    private void uploadUserProfileToDatabase (final String usernameString, final UserModel newUser){

        userListDataRef.child(usernameString).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(usernameString)
                        .build();

                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserProfileSetupActivity.this, "Firebase User Profile update successful.",
                                Toast.LENGTH_SHORT).show();

                        Intent mapsMainIntent = new Intent(UserProfileSetupActivity.this, MapsMainActivity.class);
                        startActivity(mapsMainIntent);
                        UserProfileSetupActivity.this.finish();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorDialog("Error ! ","Profile submission failed" + e);
            }
        });
    }

    //TODO 4: Error handling - show Error Dialog * IF form not complete ? *
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




    //TODO 5: Check if username already Exists
    private void checkUserNameAvailability(final String usernameString, final UserModel newUser) {

        userListDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("data", "onDataChange:  " + dataSnapshot.hasChild(username.getText().toString()));

             if ( dataSnapshot.hasChild(username.getText().toString())){


                 showErrorDialog("Username Taken " , "Username already exists");

             }else {
                 uploadUserProfileToDatabase(usernameString, newUser);
             }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }











}
