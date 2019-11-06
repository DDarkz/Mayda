package salhi.boubrit.mayda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static java.util.Locale.CANADA;

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
    private final int DEFAULT_ZOOM = 13;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private EditText search_location_et;
    private ImageButton search_restaurant_btn;
    protected Location lastLocation;
    private AdressResultReceiver resultReceiver;
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

        search_location_et = (EditText)findViewById(R.id.location_search);

        retour_btn = (ImageButton)findViewById(R.id.ret_btn);
        //gps_view = (ImageView)findViewById(R.id.gps_btn);

       // getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setDisplayShowHomeEnabled(true);

        String restaurant_recherche = search_location_et.getText().toString();

        search_restaurant_btn = (ImageButton)findViewById(R.id.search_restaurant_button);

        search_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAddressButtonHander();
               // testFetchAdress();
               // testingGeocoder();
            }
        });

        retour_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retour();
            }
        });

//        gps_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getLocationPermission();
//                UpdateUI();
//                getDeviceLocation();
//               // goToDefaultLocation();
//                Log.d(TAG, "onClick: ");
//            }
//        });
    }
//    public void testingGeocoder(){
//        search_location_et.setText(""+mLastknownLocation);
//    }

    public void testFetchAdress(){
        Geocoder geocoder1 = new Geocoder(this, Locale.getDefault());
        String outputAdress = "";

            List<Address> addresses1 = null;
            try {   addresses1 = geocoder1.getFromLocation(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude(),1);
//
                outputAdress = addresses1.get(0).getAddressLine(0);
              Toast.makeText(this, ""+outputAdress, Toast.LENGTH_LONG).show();
              search_location_et.setText(outputAdress);


    } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                            LatLng latLng = new LatLng(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here").icon(bitmapDescriptorFromVector(RestaurantMapActivity.this,R.drawable.ic_place2)));
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

  // methode pour convertir l'image de l'icone du  marker en bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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


    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
       // if (search_location_et.i) a completer
        resultReceiver = new AdressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastknownLocation);
        startService(intent);
    }

    private void fetchAddressButtonHander() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastknownLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastknownLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(RestaurantMapActivity.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                        UpdateUI();
                        showRestaurants();

                    }
                });
    }

    private void showRestaurants() {
        Restaurants restaurants1 = new Restaurants("le bon","samir", "11111119999", "1345 rue chambly Longueuil QC J4J 5C6 canada");
       // Address adressOnProcess =
      //  LatLng latLngRes = new LatLng(restaurants1.adressRes.)

    }

    private class AdressResultReceiver extends ResultReceiver {

        public AdressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            Log.d(TAG, "onReceiveResult: ca c'Est le resultData"+resultData);
            if (resultData == null) {
                return;
            }

            String output = resultData.getString(Constants.RESULT_DATA_KEY);
            search_location_et.setText(output);
            //   mMap.addMarker(new MarkerOptions().title("my location").position())

            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(RestaurantMapActivity.this, " adresse trouvé" + R.string.adress_found, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(RestaurantMapActivity.this, "on a tout foiré", Toast.LENGTH_SHORT).show();

            }
        }

    }

//    public void showPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.actions, popup.getMenu());
//        popup.show();
//    }

        // essai de la classe Fetch


}
