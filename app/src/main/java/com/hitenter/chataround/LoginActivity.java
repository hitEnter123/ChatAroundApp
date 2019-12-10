package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    TextView signUp;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference firebaseDataRef = firebaseDatabase.getReference().child("user_list/");
    public static UserModel user;



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        //TODO PT1-4 : check if any user is logged in
        if (currentUser != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            LoadingDialogClass loadingDialogClass = new LoadingDialogClass();
            loadingDialogClass.show(ft, "dialog");
            Log.d("Login", "signInWithEmail: user is " + currentUser.getDisplayName());



            if (currentUser.getDisplayName() == null || currentUser.getDisplayName().equals("")) {
                Intent mapIntent = new Intent(LoginActivity.this, UserProfileSetupActivity.class);
                startActivity(mapIntent);
                LoginActivity.this.finish();
            } else {


                firebaseDataRef.child(currentUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("findUser", "value : " + dataSnapshot.getValue(UserModel.class));
                        //TODO PT2 - 6 create static user and INTENT CONDITION
                        user = dataSnapshot.getValue(UserModel.class);



                        Intent mapIntent = new Intent(LoginActivity.this, MapsMainActivity.class);
                        startActivity(mapIntent);
                        LoginActivity.this.finish();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                login(email, password);
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
                LoginActivity.this.finish();


            }
        });


    }


    //TODO PT1 - 2: login logic
    private void login(EditText email, EditText password) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        Boolean emptyPass = false;


        emptyPass = emptyCheck(email, password);

        if (emptyPass) {


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            final LoadingDialogClass loadingDialogClass = new LoadingDialogClass();
            loadingDialogClass.show(ft, "dialog");

            firebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();


                        firebaseDataRef.child(currentUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Log.d("findUser", "value : " + dataSnapshot.getValue(UserModel.class));

                                user = dataSnapshot.getValue(UserModel.class);

                                Intent mapIntent = new Intent(LoginActivity.this, MapsMainActivity.class);


                                startActivity(mapIntent);


                                LoginActivity.this.finish();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    } else {
                        Log.w("Login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        loadingDialogClass.dismiss();

                    }


                }
            });
        }


    }


    void bindViews() {
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.sign_up);
    }

    private Boolean emptyCheck(EditText email, EditText password) {

        EditText[] editTexts = {email, password};

        Boolean pass = false;


        for (int i = 0; i < editTexts.length; i++) {

            if (editTexts[i].getText().toString().isEmpty()) {

                editTexts[i].setError("Please Fill in");
                pass = false;

            } else pass = true;


        }


        return pass;


    }


}
