package salhi.boubrit.mayda;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RestaurantMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton retour_btn;
    private ImageView gps_view;

    private final static int PERMISSION_REQUEST_ACCES_FINE_LOCATION=1;
    private Boolean mLocationPermissionGranted;
    Location rLocation,cLocation,mDernierelocationconnu,mCurrentLocation,mCameraPosition;
    LocationRequest mlocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //GeoDataClient geoDataClient;
    //PlaceDetectionClient;
    private Location mLastknownLocation;
    private static final String TAG = "RestaurantMapActivity";

    private static final LatLng mDefaultLocation = new LatLng(45.526351, -73.587832);
    private final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);

        if (savedInstanceState!=null){
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        retour_btn = (ImageButton)findViewById(R.id.ret_btn);
        gps_view = (ImageView)findViewById(R.id.gps_btn);

       // getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setDisplayShowHomeEnabled(true);

        retour_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retour();
            }
        });

        gps_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                UpdateUI();
                getDeviceLocation();
               // goToDefaultLocation();
                Log.d(TAG, "onClick: ");
            }
        });
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.myCustomMap)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }else {
        getLocationPermission();
        UpdateUI();
        getDeviceLocation();}

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        if (mMap!=null){
            outState.putParcelable(KEY_CAMERA_POSITION,mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION,mLastknownLocation);
            super.onSaveInstanceState(outState, outPersistentState);
        }

    }

    //    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id== android.R.id.home){
//            this.finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }



    private void getLocationPermission(){

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
        }else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_ACCES_FINE_LOCATION);

    }



    private void UpdateUI(){
        if (mMap==null){
            return;
        }try {
                      if (mLocationPermissionGranted){
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }else {
                          mMap.setMyLocationEnabled(false);
                          mMap.getUiSettings().setMyLocationButtonEnabled(false);
                          mLastknownLocation=null;
                          getLocationPermission();
                      }
        }catch (SecurityException e){
            Log.e(TAG, "Exception: %s"+ e.getMessage(),e);
        }
    }


    private void getDeviceLocation(){

        try {
            if (mLocationPermissionGranted){
                Task LocationResualt = mFusedLocationProviderClient.getLastLocation();
                LocationResualt.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            mLastknownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude()),DEFAULT_ZOOM));
                        }else {
                            Log.d(TAG, "current location is null. Using defaults.");
                            Log.d(TAG, "Exception %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: Exception %s "+e.getMessage(),e );
        }
    }


    public void retour(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    private void goToDefaultLocation(){
        mMap.addMarker(new MarkerOptions().title("Montréal").position(mDefaultLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
    }


}
