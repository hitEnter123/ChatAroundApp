package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {


    EditText email, password, confirmPassword;
    Button signUpSubmitButton;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bindViews();


        firebaseAuth = FirebaseAuth.getInstance();


        signUpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

checkAndSignUp(email,password,confirmPassword);
            }
        });


    }


    private void bindViews() {

        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);

        confirmPassword = findViewById(R.id.sign_up_confirm_password);


        signUpSubmitButton = findViewById(R.id.sign_up_submit_button);


    }


    private void signUp(String email, String password) {


        Log.d("signup", "signUp: Signing Up");

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("signup", "createUserWithEmail:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    Intent  mainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    SignUpActivity.this.finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signup", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private void checkAndSignUp(EditText email, EditText password, EditText confirmPassword) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        Pattern passwordPattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$";


        passwordPattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = passwordPattern.matcher(passwordString);

        if(emailString.isEmpty() || passwordString.isEmpty() || confirmPasswordString.isEmpty()){

            email.setError("Empty");
            password.setError("Empty");
            confirmPassword.setError("Empty");

        } else

        if (!passwordString.equals(confirmPasswordString)) {

            password.setError("Password is not the same");
            confirmPassword.setError("password is not the same");


        }else  if (!Pattern.matches(PASSWORD_PATTERN, passwordString)) {

            password.setError("Doesn't match requirements : 1 Capital letter, 1 Small letter, 1 Special character");
            confirmPassword.setError("Doesn't match requirements 1 Capital letter, 1 Small letter, 1 Special character");
        }  else  if (Pattern.matches(PASSWORD_PATTERN, passwordString) ) {

            signUp(emailString, passwordString);
        }


    }


}
