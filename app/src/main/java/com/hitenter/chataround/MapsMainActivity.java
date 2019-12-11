package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;


//TODO PT1-5 create XML layout according to docs and set mapfragment

public class MapsMainActivity extends AppCompatActivity implements OnMapReadyCallback {


    Button signOut;
    TextView usernameAppBar;

    TextView textViewBottomSheetState;
    Button toggleBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);

        bindViews();


        //SignOut
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignOut();
            }
        });



       SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

       mapFragment.getMapAsync(this);



       ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
       textViewBottomSheetState = findViewById(R.id.bottom_sheet_text);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        textViewBottomSheetState.setText("STATE HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        textViewBottomSheetState.setText("STATE EXPANDED");
                        // update toggle button text
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        textViewBottomSheetState.setText("STATE COLLAPSED");
                        // update collapsed button text
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        textViewBottomSheetState.setText("STATE DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        textViewBottomSheetState.setText("STATE SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("MAPS MAIN USER ", "onCreate:  " + LoginActivity.user.age);

        usernameAppBar.setText(LoginActivity.user.name);




    }







    private void bindViews ()  {

        signOut = findViewById(R.id.sign_out);
        usernameAppBar = findViewById(R.id.username_appbar);



    }



    //TODO PT1-6 signOut logic
    private void userSignOut( )  {

        FirebaseAuth.getInstance().signOut();
        Intent authPageIntent = new Intent(MapsMainActivity.this, LoginActivity.class);
        startActivity(authPageIntent);
        MapsMainActivity.this.finish();




    }
}
