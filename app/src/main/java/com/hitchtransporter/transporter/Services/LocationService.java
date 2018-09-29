package com.hitchtransporter.transporter.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Activities.TrackerHitcher;
import com.hitchtransporter.transporter.AA_Classes.SmartLocalBrodcast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.Constants.BROADCAST_MESSAGE;
import static com.hitchtransporter.smart.framework.Constants.LANGUAGE;
import static com.hitchtransporter.smart.framework.Constants.LATITUDE;
import static com.hitchtransporter.smart.framework.Constants.LONGITUDE;
import static com.hitchtransporter.smart.framework.Constants.ORDER_ID;
import static com.hitchtransporter.smart.framework.Constants.ORDER_REQUEST_ID;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;

public class LocationService extends Service implements LocationListener {

    final String TAG = "GPS";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;


    LocationManager locationManager;
    Location loc;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    String orderId, orderRequestId;


    int i = 0;
    private static int count = 0;


    NotificationManagerCompat notificationManager;

    Double currentLatitude;
    Double currentLongitude;

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Toast.makeText(getApplicationContext(), "toasting", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (intent != null && intent.getStringExtra(ORDER_ID) != null) {
            orderId = intent.getStringExtra(ORDER_ID);
            orderRequestId = intent.getStringExtra(ORDER_REQUEST_ID);
        } else {
            Log.d("@@CHECK==", "check5");
        }

        getLocation();
       /*
        if()
        sendTransporterLocation(String.valueOf(currentLatitude), String.valueOf(currentLatitude), orderId);
*/
        //   setTrackingNotification();
        // startRepeatingTask();

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("@@CHECK==", "check2");
        sendLatLong(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        setCurrentLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        if(orderId != null){
            if(!orderId.equals("")){
                sendTransporterLocation(String.valueOf(currentLatitude), String.valueOf(currentLongitude), orderId);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateUI(Location loc) {
        // Log.d("@@CHECK==","check3");

        Log.d(TAG, "updateUI");
        Log.d(TAG, "latitude==" + loc.getLatitude());
        Log.d(TAG, "longitude==" + loc.getLongitude());

    }

    private void sendTransporterLocation(final String lattitude, final String longitude, String orderId) {
        Log.d("@@CHECK==", "lattitude==" + lattitude);
        Log.d("@@CHECK==", "longitude==" + longitude);

        // trackerMap.getMapAsync(TrackerTest.this);
        Map<String, String> params = new HashMap<>();
        params.put(ORDER_ID, orderId);
        params.put(LONGITUDE, longitude);
        params.put(LATITUDE, lattitude);
        params.put(LANGUAGE, getLanguageCode());
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "trackingIn");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {

                Log.d("@@TRACKING_IN", response.toString());    /*trackerNo++;

                if (responseCode == 200) {
                    Log.d("@@@Tracker_success==", "" + trackerNo);
                    mMap.clear();

                    trackingLatLngs.add(new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude)));
                    setupMarkers(0);
                    drawTrackingPath(trackingLatLngs,0);
                } else if (responseCode == 400) {
                    try {
                        Toast.makeText(getApplicationContext(), response.getJSONObject(DATA).getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }

            @Override
            public void onResponseError() {
                // Log.e("@@@Tracker_failed==", "" + trackerNo);
                //  pendingLatLong.add(new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude)));

            }
        });
        SmartWebManager.getInstance(this).addToRequestQueue(requestParams, false, false);
    }

    private void setTrackingNotification() {
        if (notificationManager == null) {
            notificationManager = NotificationManagerCompat.from(this);
        }
        Intent intent = new Intent(this, TrackerHitcher.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(ORDER_REQUEST_ID, orderRequestId);
        intent.putExtra("from_notification", true);

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100),
                    intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentTitle("Order Tracking Status")
                    .setContentText("In Progress")
                    .setTicker(getString(R.string.app_name))
                    .setAutoCancel(false)
                    .setDefaults(-1)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);

            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager.notify(count, notificationBuilder.build());
            count++;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        notificationManager.cancelAll();
        startService();
    }

    private void sendLatLong(String lat, String longi) {
        Intent broadcastIntent = new Intent(BROADCAST_MESSAGE);
        broadcastIntent.putExtra(LATITUDE, lat);
        broadcastIntent.putExtra(LONGITUDE, longi);
        SmartLocalBrodcast.setSendBroadcast(this, broadcastIntent);
    }

    private void startService() {
        Intent serviceIntent = new Intent(LocationService.this, LocationService.class);
        serviceIntent.putExtra(ORDER_ID, orderId);
        serviceIntent.putExtra(ORDER_REQUEST_ID, orderRequestId);
        startService(serviceIntent);
    }

    private void setCurrentLocation(String currentLatitude, String currentLongitude) {
        this.currentLatitude = Double.parseDouble(currentLatitude);
        this.currentLongitude = Double.parseDouble(currentLongitude);
    }
}
