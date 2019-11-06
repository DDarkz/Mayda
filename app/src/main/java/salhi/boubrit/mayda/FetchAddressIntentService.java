package salhi.boubrit.mayda;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static java.util.Locale.CANADA;

public class FetchAddressIntentService extends IntentService {

    Geocoder geocoder;
    String errorMessage = "";

   // protected ResultReceiver receiver;

    public FetchAddressIntentService() {
        super("FetchAdressIntentService");
    }


    // ...
//    private void deliverResultToReceiver(int resultCode, String message) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.RESULT_DATA_KEY, message);
//        receiver.send(resultCode, bundle);
//    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent: intent est"+intent);
                geocoder = new Geocoder(this, Locale.getDefault());

        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        //Bundle parameters = intent.getExtras(Constants.RECEIVER);
           ResultReceiver  receiver = intent.getParcelableExtra(Constants.RECEIVER);

        //geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),10);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);


        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, "onHandleIntent: " + e.getMessage(), e);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = illegalArgumentException.getMessage();
            Log.e(TAG, "onHandleIntent: " + illegalArgumentException.getMessage(), illegalArgumentException);
        }

        //sil n'y a pas d'adresse
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "aucune adresse trouvé";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(receiver,Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> adressFragment = new ArrayList<String>();
            Log.d(TAG, "onHandleIntent: "+address);

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                adressFragment.add(addresses.get(0).getAddressLine(i));

            }
            Log.e(TAG, getString(R.string.adress_found));
            Log.d(TAG, "onHandleIntent: sa c'est l' adressFragment "+adressFragment);
            deliverResultToReceiver(receiver,Constants.SUCCESS_RESULT, ""+adressFragment);


        }


    }
    private void deliverResultToReceiver(ResultReceiver receiver, int resultCode, String message) {
       // receiver = new ResultReceiver(new Handler());
       // ResultReceiver  receiver = intent.getParcelableExtra(Constants.RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        Log.d(TAG, "deliverResultToReceiver: sa c'est le message"+message);
        Log.d(TAG, "deliverResultToReceiver: sa c'est le resultCode"+resultCode);
        Log.d(TAG, "deliverResultToReceiver: sa c'est le bundle"+bundle);
        Log.d(TAG, "deliverResultToReceiver: sa cest lereceiver"+receiver);
        receiver.send(resultCode, bundle);
        Log.d(TAG, "deliverResultToReceiver: sa cest lereceiver"+receiver);

    }

}

//    public final class Constants {
//        public static final int SUCCESS_RESULT = 0;
//        public static final int FAILURE_RESULT = 1;
//        public static final String PACKAGE_NAME =
//                "salhi.boubrit.mayda";
//        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
//        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
//                ".RESULT_DATA_KEY";
//        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
//                ".LOCATION_DATA_EXTRA";
//    }


//}
