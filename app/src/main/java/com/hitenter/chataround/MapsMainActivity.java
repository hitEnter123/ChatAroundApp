package com.hitenter.chataround;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//TODO PT 3-1, implement OnMapReadyCallback and implement all the necessary methods
//TODO PT 4-4, implement onMarkerClickListener
public class MapsMainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    //Constant
    //Logcat Tag
    String TAG = "ChatAroundApp";
    final int REQUEST_CODE = 123;
    //Time between location updates (5000 milliseconds or 5 seconds)
    long MIN_TIME = 5000;
    //Distance between location updates (1000m or 1km)
    float MIN_DISTANCE = 1000;
    //Query radius in km
    double RADIUS = 5;
    //Zoom
    float ZOOM = 15.5f;

    //Member Variable
    Button signOutButton;
    Button recenterButton;

    //Map
    private GoogleMap mMap;
    private Location mMyLocation;
    private Marker mMyMarker;
    private Circle mMyCircle;

    //Firebase reference
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    //TODO PT 3-3-1, add GeoFire member variable
    //Geofire
    private GeoFire mGeoFire;

    //GeoQuery
    private GeoQuery mGeoQuery;
    private GeoQueryEventListener mGeoQueryListener;

    //My details
    private String mMyUserName;

    //Other users
    private Map<String, Marker> otherUsersMarkersMap;

    //Location Manager
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    //Loading
    LoadingDialogClass loadingDialogClass = new LoadingDialogClass();


    //BottomSheet
    private ConstraintLayout bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView talkingTo;

    private RecyclerView mRecyclerView;
    private EditText messageEnterEditText;
    private Button sendButton;



    String nowTalkingTo = "";





    List<InstantMessage> messageList = new ArrayList<InstantMessage>();
    final ChatMessageAdapter msgAdapter = new ChatMessageAdapter(messageList);


    final ArrayList<String> tempKey = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bindViews();


        //TODO PT4-9 Adapter Setup (*to be moved later*)
//        final ChatMessageAdapter msgAdapter = new ChatMessageAdapter(messageList);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(llm);
//        mRecyclerView.setAdapter(msgAdapter);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);


        //Initialize firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //Geofire
        DatabaseReference pathToGeoFire = mDatabase.child("geofire");
        mGeoFire = new GeoFire(pathToGeoFire);

        //My name
        mMyUserName = LoginActivity.user.name;

        //Other users
        otherUsersMarkersMap = new HashMap<>();

        //SignOut
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignOut();
            }
        });

        //Recenter
        recenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recenterMarker();
            }
        });






        //TODO PT4-10-2 Read data from database with ValueEventListener
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()
                ) {
                    Log.d("CHAT", "snapshot   key" + snapshot.getKey());
                    InstantMessage message = snapshot.getValue(InstantMessage.class);

                    if (!tempKey.contains(snapshot.getKey())) {
                        tempKey.add(snapshot.getKey());
                        messageList.add(message);
                        Log.d("CHAT", "Message :  " + message.message);
                        mRecyclerView.setAdapter(msgAdapter);
                        mRecyclerView.scrollToPosition(msgAdapter.getItemCount() - 1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        //TODO PT4-3 BottomSheet logic
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        //TESTING BOTTOMSHEETBEHAVIOR SET STATE
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                //TODO PT4-10-1 Read when expanded

                if (i == BottomSheetBehavior.STATE_EXPANDED) {

                    Log.d("CHAT", "onStateChanged:  EXPANDED ! ");

                    DatabaseReference readMessageRef = FirebaseDatabase.getInstance().getReference().child("messages/");
                    if (nowTalkingTo != "")
                        readMessageRef.child(mMyUserName).child(nowTalkingTo).addValueEventListener(valueEventListener);

//                    readMessageRef.child(mMyUserName).child(nowTalkingTo).addChildEventListener(childEventListener);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        //TODO PT4-6 Sending message to a dummy
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REF
                DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("messages/");


                String message = messageEnterEditText.getText().toString();
                String sendingTo = talkingTo.getText().toString();
                String sendingFrom = mMyUserName;

                Date currentTime = Calendar.getInstance().getTime();

                Log.d("CHAT", "onClick:  message Is " + message);
                //Create a message for send and receive
                InstantMessage newMessageSend = new InstantMessage(message, sendingFrom, InstantMessage.MSG_SENT, currentTime.toString());
                InstantMessage newMessageReceive = new InstantMessage(message, sendingFrom, InstantMessage.MSG_RECEIVED, currentTime.toString());

                Log.d("CHAT", "onClick:  message Is " + newMessageSend.getTime());


                messageRef.child(sendingFrom).child(sendingTo).push().setValue(newMessageSend);
                messageRef.child(sendingTo).child(sendingFrom).push().setValue(newMessageReceive);


                messageEnterEditText.setText("");

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getMyCurrentLocation();
    }

    private void bindViews() {
        signOutButton = findViewById(R.id.sign_out_button);
        recenterButton = findViewById(R.id.recenter_button);
        messageEnterEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);
        mRecyclerView = findViewById(R.id.message_recycler_view);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        talkingTo = findViewById(R.id.bottom_sheet_text);

    }


    //TODO PT 3-1-1, add mMap variable and link to googleMap
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

    }

    //TODO PT 3-2, getMyCurrentLocation(), similar to WeatherApp, in onStart

    private void getMyCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() callback received");
                mMyLocation = location;
                updateMyCoordinatesToDatabase();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged() callback received");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled() callback received");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled() callback received");
            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        mMyLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mMyLocation == null) {
            mMyLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        Log.d(TAG, "My last location is " + mMyLocation);
        if (mMyLocation != null) {
            updateMyCoordinatesToDatabase();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (permissions.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionResult(): Permission granted!");
                getMyCurrentLocation();
            } else {
                Log.d(TAG, "Permission denied :(");
            }
        }
    }

    //TODO PT 3-3, updateMyCoordinatesToDatabase(), using GeoFire

    private void updateMyCoordinatesToDatabase() {
        Log.d(TAG, "Updating the location to database");
        mGeoFire.setLocation(mMyUserName, new GeoLocation(mMyLocation.getLatitude(), mMyLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.d(TAG, "There was an error saving the location to GeoFire: " + error);
                } else {
                    Log.d(TAG, "Location saved on server successfully!");
                    if (mMyMarker == null) {
                        initializeMyMarker();
//                        loadingDialogClass.dismiss();
                    } else {
                        updateMyMarker();
                    }
                    initializeGeoQuery();
                }
            }
        });
    }

    //TODO PT 3-4, initializeMyMarker(), marker and circle

    private void initializeMyMarker() {
        Log.d(TAG, "Setting my marker at " + mMyLocation.getLatitude() + "," + mMyLocation.getLongitude());
        LatLng myLocation = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
        mMyMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My current location"));
        mMyMarker.setTag(mMyUserName);
        mMyCircle = mMap.addCircle(new CircleOptions().center(myLocation).radius(RADIUS * 1000).strokeColor(Color.RED).fillColor(0x22f1816c).strokeWidth(5.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM));
    }

    //TODO PT 3-4-1, updateMyMarker(), marker and circle

    private void updateMyMarker() {
        Log.d(TAG, "Updating my marker to " + mMyLocation.getLatitude() + "," + mMyLocation.getLongitude());
        LatLng myLocation = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
        mMyMarker.setPosition(myLocation);
        mMyCircle.setCenter(myLocation);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM));
    }

    //TODO PT 3-5, initializeGeoQuery()

    private void initializeGeoQuery() {
        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(mMyLocation.getLatitude(), mMyLocation.getLongitude()), RADIUS);
        for (Map.Entry<String, Marker> entry : otherUsersMarkersMap.entrySet()) {
            String key = entry.getKey();
            Marker value = entry.getValue();
            value.remove();
        }
        otherUsersMarkersMap.clear();
        mGeoQuery.removeAllListeners();
        mGeoQueryListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                if (!key.equals(mMyUserName)) {
                    Log.d(TAG, key + " has entered the area at " + location.latitude + "," + location.longitude + ".");
                    Log.d("ENTER", "Adding " + key + " to map");
                    updateNearbyMarkers(key, "onKeyEntered");

                }
            }

            @Override
            public void onKeyExited(final String key) {
                if (!key.equals(mMyUserName)) {
                    Log.d(TAG, key + " has exited the area.");
                    updateNearbyMarkers(key, "onKeyExited");
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, "Map is " + otherUsersMarkersMap);
                if (!key.equals(mMyUserName)) {
                    Log.d(TAG, key + " has moved int the area to " + location.latitude + "," + location.longitude + ".");
                    if (otherUsersMarkersMap.containsKey(key)) {
                        updateNearbyMarkers(key, "onKeyMoved");
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "GeoQuery is ready!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG, "Error on GeoQuery: " + error);
            }
        };
        mGeoQuery.addGeoQueryEventListener(mGeoQueryListener);
    }

    //TODO PT 3-6, updateNearbyMarkers(string key, string callback)

    private void updateNearbyMarkers(final String key, final String callback) {
        mDatabase.child("geofire").child(key).child("l").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double lat = 0;
                double lon = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("0")) {
                        lat = Double.parseDouble(snapshot.getValue().toString());
                    }
                    if (snapshot.getKey().equals("1")) {
                        lon = Double.parseDouble(snapshot.getValue().toString());
                    }
                }
                LatLng latlng = new LatLng(lat, lon);
                if (callback == "onKeyEntered") {
                    if (!otherUsersMarkersMap.containsKey(key)) {
                        Log.d("ENTER", "adding marker   " + otherUsersMarkersMap);
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(key));
                        marker.setTag(key);
                        otherUsersMarkersMap.put(key, marker);
                    }
                } else if (callback == "onKeyExited") {
                    if (otherUsersMarkersMap.get(key) != null) {
                        otherUsersMarkersMap.get(key).remove();
                        otherUsersMarkersMap.remove(key);
                    }
                } else if (callback.equals("onKeyMoved")) {
                    if (otherUsersMarkersMap.get(key) != null) {
                        otherUsersMarkersMap.get(key).setPosition(latlng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO PT 3-9, free up resources

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGeoQuery != null) {
            mGeoQuery.removeAllListeners();
            mGeoFire.removeLocation(mMyUserName, new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Log.d(TAG, "There was an error removing the location to GeoFire: " + error);
                    } else {
                        Log.d(TAG, "Location removed on server successfully!");
                    }
                }
            });
        }
    }

    //TODO PT 3-7, userSignOut()

    private void userSignOut() {
        mGeoFire.removeLocation(mMyUserName, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Log.d(TAG, "There was an error removing the location to GeoFire: " + error);
                } else {
                    Log.d(TAG, "Location removed on server successfully!");
                }
            }
        });
        FirebaseAuth.getInstance().signOut();
        Intent authPageIntent = new Intent(MapsMainActivity.this, LoginActivity.class);
        startActivity(authPageIntent);
        MapsMainActivity.this.finish();
    }

    //TODO PT 3-8, recenterMyself()

    private void recenterMarker() {
        LatLng myLocation = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, ZOOM));
    }


    //TODO PT 4-5
    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("CHAT", "onMarkerClick:  MARKER CLICKED !" + marker.getTag());
        if (marker.getTag() != LoginActivity.user.name) {



            //TODO PT 4-11 Add logic for talking to another person
            if (!marker.getTag().toString().equals(nowTalkingTo)) {

                nowTalkingTo = marker.getTag().toString();
                messageList.clear();
                tempKey.clear();
                msgAdapter.notifyDataSetChanged();

                Log.d("CHAT", "nowTalkingTo " + nowTalkingTo);
            }

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


            talkingTo.setText(marker.getTag().toString());
        }
        return false;
    }

    @Override
    public void onCameraMove() {


    }
}

