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

public class MainActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    TextView signUp;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser!= null ){
            Intent mapIntent = new Intent(MainActivity.this, MapsMainActivity.class);
            startActivity(mapIntent);
            MainActivity.this.finish();}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                login(email,password);
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
                MainActivity.this.finish();


            }
        });


    }

    private void login (EditText email, EditText password ) {
      String emailString =   email.getText().toString();
      String passwordString=   password.getText().toString();



      firebaseAuth .signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

              if(task.isSuccessful()){
                  FirebaseUser user =firebaseAuth.getCurrentUser();

                  Log.d("Login", "signInWithEmail: user is "  + user );

                    Intent mapIntent = new Intent(MainActivity.this, MapsMainActivity.class);
                    startActivity(mapIntent);
                    MainActivity.this.finish();

                   }
              else {
                  Log.w("Login", "signInWithEmail:failure", task.getException());
                  Toast.makeText(MainActivity.this, "Authentication failed.",
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
