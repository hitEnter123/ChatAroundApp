package com.hitenter.chataround;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class UserProfileSetupActivity extends AppCompatActivity {

  private  EditText username, userage;
  String useremail;
  private CheckBox usermale;
  private  Button submitButton;


  FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
  FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_setup);


        bindViews();




        useremail = firebaseUser.getEmail();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






            }
        });























    }









    private void bindViews(){
        submitButton = findViewById( R.id.user_profile_submit_button);
        username = findViewById(R.id.user_profile_username);
        userage = findViewById( R.id.user_profile_age);

    }






    private void createUserProfile(EditText username, EditText userage, String useremail, Switch usergender ) {

        String usernameString = username.getText().toString();
        String userageString = userage.getText().toString();
        String useremailString = useremail;



        //Create UserModel Class









    }
}
