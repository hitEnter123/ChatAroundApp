package com.hitenter.chataround;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MapsMainActivity extends AppCompatActivity implements OnMapReadyCallback {


    Button signOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);

        bindViews();

        Log.d("MAPS MAIN USER ", "onCreate:  " + LoginActivity.user.age);

        //SignOut
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignOut();
            }
        });



       SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

       mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


    }







    private void bindViews ()  {

        signOut = findViewById(R.id.sign_out);




    }


    private void userSignOut( )  {

        FirebaseAuth.getInstance().signOut();
        Intent authPageIntent = new Intent(MapsMainActivity.this, LoginActivity.class);
        startActivity(authPageIntent);
        MapsMainActivity.this.finish();




    }
}
