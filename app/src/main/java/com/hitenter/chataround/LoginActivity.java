package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        if (currentUser != null) {


            Log.d("Login", "signInWithEmail: user is " + currentUser.getDisplayName());

            //TODO LT2 - 6 create static user and INTENT CONDITION

            if (currentUser.getDisplayName() == null || currentUser.getDisplayName().equals("")) {
                Intent mapIntent = new Intent(LoginActivity.this, UserProfileSetupActivity.class);
                startActivity(mapIntent);
                LoginActivity.this.finish();
            } else {


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

    private void login(EditText email, EditText password) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();


        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    Log.d("Login", "signInWithEmail: user is " + user);


                    Intent mapIntent = new Intent(LoginActivity.this, MapsMainActivity.class);


                    startActivity(mapIntent);


                    LoginActivity.this.finish();


                } else {
                    Log.w("Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();


                }


            }
        });


    }


    void bindViews() {
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.sign_up);
    }
}
