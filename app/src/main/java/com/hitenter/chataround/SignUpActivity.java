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

                checkAndSignUp(email, password, confirmPassword);
            }
        });


    }


    private void bindViews() {

        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);

        confirmPassword = findViewById(R.id.sign_up_confirm_password);


        signUpSubmitButton = findViewById(R.id.sign_up_submit_button);


    }

    //TODO PT1 - 1 : Signup logic refer to google documentation , intent

    private void signUp(String email, String password) {


        Log.d("signup", "signUp: Signing Up");

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("signup", "createUserWithEmail:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    Intent mainActivityIntent = new Intent(SignUpActivity.this, LoginActivity.class);
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


    //TODO PT1 -3 : Need to do some checks - empty , password matching, RegExp
    private void checkAndSignUp(EditText email, EditText password, EditText confirmPassword) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        Boolean emptyPass= false, similarPass= false, patternCorrect= false;




        emptyPass = emptyCheck(email, password, confirmPassword);
        similarPass = confirmPasswordCheck(password,confirmPassword);

        if ( similarPass){
        patternCorrect = patternCorrectCheck(email,password,confirmPassword);}

        if(emptyPass && similarPass && patternCorrect)
        {signUp(emailString,passwordString);}

    }


    private Boolean emptyCheck(EditText email, EditText password, EditText confirmPassword) {

        EditText[] editTexts = {email, password, confirmPassword};

        Boolean pass = false;


        for (int i = 0; i < editTexts.length; i++) {

            if (editTexts[i].getText().toString().isEmpty()) {

                editTexts[i].setError("Please Fill in");
                pass = false;

            } else pass = true;


        }


        return pass;


    }


    private Boolean confirmPasswordCheck(EditText password, EditText confirmPassword) {


        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            password.setError("Passwords do not match");
            confirmPassword.setError("Password do not match");


            return false;

        } else return true;


    }



    private Boolean patternCorrectCheck (EditText email, EditText password, EditText confirmPassword){


        Boolean pass = true;

        final String EMAIL_ADDRESS_PATTERN =
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+";

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*?[#?!@$%^&*-])(?=\\S+$).{8,20}$";



        if (!Pattern.matches(EMAIL_ADDRESS_PATTERN, email.getText().toString())){
            email.setError("This is not an email");
            pass = false;
        }


        if(!Pattern.matches(PASSWORD_PATTERN, password.getText().toString()) ){
            password.setError("Doesn't match requirements : 1 Capital letter, 1 Small letter, 1 Special character");
            confirmPassword.setError("Doesn't match requirements 1 Capital letter, 1 Small letter, 1 Special character");
            pass = false;
        }

return pass;
    }
}
