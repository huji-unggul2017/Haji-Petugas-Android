package com.huji.apps.haji.petugas.Services;

/**
 * Created by Dell_Cleva on 20/02/2017.
 */

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.huji.apps.haji.petugas.R;
import com.huji.apps.haji.petugas.Utils.Constant;
import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link ResultReceiver} in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constant.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(Constant.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constant.FAILURE_RESULT, errorMessage);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        // Address found using the Geocoder.
//        List<Address> addresses = null;
//
//        try {
//            // Using getFromLocation() returns an array of Addresses for the area immediately
//            // surrounding the given latitude and longitude. The results are a best guess and are
//            // not guaranteed to be accurate.
//            addresses = geocoder.getFromLocation(
//                    location.getLatitude(),
//                    location.getLongitude(),
//                    // In this sample, we get just a single address.
//                    1);
//        } catch (IOException ioException) {
//            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available);
//            Log.e(TAG, errorMessage, ioException);
//        } catch (IllegalArgumentException illegalArgumentException) {
//            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(TAG, errorMessage + ". " +
//                    "Latitude = " + location.getLatitude() +
//                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
//        }
//
//        // Handle case where no address was found.
//        if (addresses == null || addresses.size()  == 0) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found);
//                Log.e(TAG, errorMessage);
//            }
//            deliverResultToReceiver(Constant.FAILURE_RESULT, errorMessage);
//        } else {
//            Address address = addresses.get(0);
//            ArrayList<String> addressFragments = new ArrayList<String>();
//
//            // Fetch the address lines using {@code getAddressLine},
//            // join them, and send them to the thread. The {@link android.location.address}
//            // class provides other options for fetching address details that you may prefer
//            // to use. Here are some examples:
//            // getLocality() ("Mountain View", for example)
//            // getAdminArea() ("CA", for example)
//            // getPostalCode() ("94043", for example)
//            // getCountryCode() ("US", for example)
//            addressFragments.add(address.getCountryName()); //("United States", for example)
//            // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//            //    addressFragments.add(address.getAddressLine(i));
//            // }
//            Log.i(TAG, getString(R.string.address_found));
//            deliverResultToReceiver(Constant.SUCCESS_RESULT,
//                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
//        }

        String currentLocation = getCurrentLocationViaJSON(location.getLatitude(), location.getLongitude());
        deliverResultToReceiver(Constant.SUCCESS_RESULT, currentLocation);
    }

    public static JSONObject getLocationInfo(double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat+","+lng +"&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String getCurrentLocationViaJSON(double lat, double lng) {

        JSONObject jsonObj = getLocationInfo(lat, lng);
        Log.i("JSON string =>", jsonObj.toString());

        String currentLocation = "testing";
        String street_address = null;
        String postal_code = null;

        try {
            String status = jsonObj.getString("status").toString();
            Log.i("status", status);

            if(status.equalsIgnoreCase("OK")){

                JSONArray results = jsonObj.getJSONArray("results");
                JSONObject jsonObjects = results.getJSONObject(0);
                if (jsonObjects.has("formatted_address")){
                    Log.w(TAG, "getCurrentLocationViaJSON: "+ jsonObjects.getString("formatted_address"));
                    String[] splits = jsonObjects.getString("formatted_address").split(",");
                    System.out.println("splits.size: " + splits.length);
                    for(String asset: splits){
                        System.out.println(asset);
                    }
                    currentLocation = splits[splits.length-1].trim();
                }


                Log.i("JSON Geo Locatoin =>|", currentLocation);
                return currentLocation;
            }

        } catch (JSONException e) {
            Log.e("testing","Failed to load JSON");
            e.printStackTrace();
        }
        return "lol";
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
