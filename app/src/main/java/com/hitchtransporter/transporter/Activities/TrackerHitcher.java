package com.hitchtransporter.transporter.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.common.DirectionsJSONParser;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.hitchtransporter.transporter.AA_Classes.MySupportMapFragment;
import com.hitchtransporter.transporter.AA_Classes.SmartLocalBrodcast;
import com.hitchtransporter.transporter.Services.LocationService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.createDrawableFromView;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.getLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.getLongitude;

/**
 * Created by ebiztrait321 on 3/1/18.
 */

public class TrackerHitcher extends MasterActivity implements OnMapReadyCallback {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;
    final String TAG = "GPS";
    Double dropOffLatitude;
    Double dropOffLongitude;
    Double pickUpLatitude;
    Double pickUpLongitude;
    Double currentLatitude;
    Double currentLongitude;
    LocationManager locationManager;
    Location loc;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    private NestedScrollView trackerScroll;
    private RoundedImageView imageTrackerTv;
    private SmartTextView nameTrackerTv;
    private MySupportMapFragment trackerMap;
    private SmartTextView distanceTrackerTv;
    private SmartTextView pickupAddressTrackerTv;
    private SmartTextView dropoffAddressTrackerTv;
    private SmartButton startJobBtn;
    private SmartButton finishJobBtn;
    private String orderRequestId;
    private String orderId;
    private String pickupAddress;
    private String dropoffAddress;
    private JSONObject orderDetailJson;
    private GoogleMap mMap;
    private boolean isTrackingStarted = false;

    private int trackerNo = 0;

    private Handler mHandler = new Handler();

    private ArrayList<LatLng> trackingLatLngs = new ArrayList<>();

    private int SET_DATA_CODE = 0;
    private int CHECK_NEARBY_CODE = 1;
    private double distance;


//    private int SET_DATA_CODE = 0;
    private BroadcastReceiver liveTracking = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            trackingLatLngs.add(new LatLng(Double.parseDouble(intent.getStringExtra(LATITUDE)), Double.parseDouble(intent.getStringExtra(LONGITUDE))));
            Log.d("@@CHECK==", intent.getStringExtra(LATITUDE) + "---" + intent.getStringExtra(LONGITUDE));
            setCurrentLocation(intent.getStringExtra(LATITUDE), intent.getStringExtra(LONGITUDE));
            mMap.clear();
            setupMarkers(0);
            drawTrackingPath(trackingLatLngs, 0);
        }
    };
    Runnable refreshTracker = new Runnable() {
        @Override
        public void run() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    getOrderDetails(getIntent().getStringExtra(ORDER_REQUEST_ID), CHECK_NEARBY_CODE);
                }
            };
            mHandler.postDelayed(runnable, 10000);
        }
    };

    @Override
    public int getLayoutID() {
        return R.layout.activity_tracker;
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setSwitch(false);
        setHeaderToolbar(getString(R.string.tracker_screen));
    }

    @Override
    public void initComponents() {
        super.initComponents();

        trackerScroll = findViewById(R.id.tracker_scroll);

        imageTrackerTv = findViewById(R.id.image_tracker_iv);
        nameTrackerTv = findViewById(R.id.name_tracker_tv);
        distanceTrackerTv = findViewById(R.id.distance_tracker_tv);
        pickupAddressTrackerTv = findViewById(R.id.pickup_tracker_tv);
        dropoffAddressTrackerTv = findViewById(R.id.dropoff_tracker_tv);

        startJobBtn = findViewById(R.id.start_job_btn);
        finishJobBtn = findViewById(R.id.finish_job_btn);

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        trackerMap = (MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tracker_map);

        if (trackerMap != null)
            trackerMap.setListener(new MySupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    trackerScroll.requestDisallowInterceptTouchEvent(true); //to monitor the touch events
                }
            });

        setCurrentLocation(getLatitude(), getLongitude());


        // getLocation();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null) {
            getOrderDetails(String.valueOf(getIntent().getExtras().get(ORDER_REQUEST_ID)), SET_DATA_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orderDetailJson != null) {
            if (mHandler != null) {
                mHandler.removeCallbacks(refreshTracker);
                mHandler.removeCallbacksAndMessages(null);
            }
        }
        SmartLocalBrodcast.setUnregisterReceiver(TrackerHitcher.this, liveTracking);
    }

    private void getOrderDetails(String order_request_id, final int requestCode) {
        if (requestCode == SET_DATA_CODE) {
            SmartUtils.showLoadingDialog(TrackerHitcher.this);
        }
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_REQUEST_ID, order_request_id);
        params.put(CURRENT_LATITUDE, String.valueOf(currentLatitude));
        params.put(CURRENT_LONGITUDE, String.valueOf(currentLongitude));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "getOrderDetail");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TrackerHitcher.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.e("@@ORDER_DETAILS", response.toString());
                    try {
                        orderDetailJson = response.getJSONObject(DATA).getJSONObject("order_detail");
                        if (requestCode == SET_DATA_CODE) {
                            setOrderDetails(orderDetailJson);
                            receiveTransporterLocation();
                        } else if (requestCode == CHECK_NEARBY_CODE) {
                            setButton(orderDetailJson);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);
    }


    //----RECEIVING TRANSPORTER DATA-----------------------------------------------------------------------------------------------------------------------------------------------------

    private void setOrderDetails(final JSONObject orderData) throws JSONException {
        orderId = orderData.getString(ORDER_ID);
        orderRequestId = orderData.getString(ORDER_REQUEST_ID);

        nameTrackerTv.setText(orderData.getString(FIRST_NAME) + " " + orderData.getString(LAST_NAME));

        if (!TextUtils.isEmpty(orderData.getString(PROFILE_IMAGE))) {
            Picasso.with(TrackerHitcher.this).load(orderData.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(imageTrackerTv);
        }

        distanceTrackerTv.setText(decimalFormatter(orderData.getString(DISTANCE)) + " " + Km);
        pickupAddress = orderData.getString(PICKUP_ADDRESS);
        pickupAddressTrackerTv.setText(pickupAddress);

        dropoffAddress = orderData.getString(DROPOFF_ADDRESS);
        dropoffAddressTrackerTv.setText(dropoffAddress);

        distance = Double.parseDouble(decimalFormatter(orderData.getString(DISTANCE)));
        pickUpLatitude = Double.parseDouble(orderData.getString(PICKUP_LATITUDE));
        pickUpLongitude = Double.parseDouble(orderData.getString(PICKUP_LONGITUDE));
        dropOffLatitude = Double.parseDouble(orderData.getString(DROPOFF_LATITUDE));
        dropOffLongitude = Double.parseDouble(orderData.getString(DROPOFF_LONGITUDE));

        trackerMap.getMapAsync(TrackerHitcher.this);

        setButton(orderData);


    }


    //SETTING THE BUTTON--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void setButton(JSONObject buttonData) throws JSONException {
        switch (buttonData.getString(ORDER_STATUS)) {

            case STATUS_APPROVED:
                startRefreshingTask();

                if (buttonData.getString(IS_PAY).equalsIgnoreCase(STATUS_PAID) && buttonData.getString(FOR_TODAY).equalsIgnoreCase("1")) {
                    startJobBtn.setVisibility(View.VISIBLE);
                    if (buttonData.getString(IS_NEARBY).equalsIgnoreCase("1")) {
                        setStartJobButton(startJobBtn);
                    } else {
                        setGoToLocationButton(startJobBtn, buttonData.getString(PICKUP_LATITUDE), buttonData.getString(PICKUP_LONGITUDE));
                    }
                }
                break;


            case STATUS_STARTED:
                startRefreshingTask();
                if (buttonData.getString(IS_NEARBY_FINISH).equalsIgnoreCase("1")) {
                    startJobBtn.setVisibility(View.VISIBLE);
                    setFinishJobButton(startJobBtn);
                } else {
                    startJobBtn.setVisibility(View.GONE);
                }
                break;

            default:
                startJobBtn.setVisibility(View.GONE);
                break;

        }
    }

    private void receiveTransporterLocation() {
        Map<String, String> params = new HashMap<>();

        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, orderId);
        params.put(IS_INITIAL, "1");

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "trackingOut");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TrackerHitcher.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    try {
                        JSONArray trackingData = response.getJSONArray(DATA);
                        if (trackingData.length() > 0) {
                            isTrackingStarted = true;
                            JSONArray lastTrackingData = response.getJSONArray(DATA);

                            mMap.clear();

                            for (int i = 0; i < lastTrackingData.length(); i++) {
                                LatLng position = new LatLng(Double.parseDouble(lastTrackingData.getJSONObject(i).getString(LATITUDE)),
                                        Double.parseDouble(lastTrackingData.getJSONObject(i).getString(LONGITUDE)));
                                trackingLatLngs.add(position);
                            }
                            currentLatitude = trackingLatLngs.get(trackingLatLngs.size() - 1).latitude;
                            currentLongitude = trackingLatLngs.get(trackingLatLngs.size() - 1).longitude;
//Amaltas Flats, Ramdev Nagar, Ahmedabad, Gujarat 380015, India 23.025664499999998 72.5086098
                            setupMarkers(1);
                            drawTrackingPath(trackingLatLngs, 1);

                            SmartLocalBrodcast.setRegisetBrodCast(TrackerHitcher.this, liveTracking, BROADCAST_MESSAGE);
                            if (!isMyServiceRunning(LocationService.class)) {
                                startLocationService();
                            }


                            // Drawing polyline in the Google Map for the i-th route
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {

            }
        });
        SmartWebManager.getInstance(TrackerHitcher.this).addToRequestQueueMultipart(requestParams, "", true);
    }

    private void setStartJobButton(SmartButton button) {
        button.setText(getString(R.string.start_job));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartUtils.getConfirmDialog(TrackerHitcher.this, getString(R.string.start_job), getString(R.string.sure_start_job),
                        getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                Log.d("@@@currentLatitude", String.valueOf(currentLatitude) + "--------------" + String.valueOf(currentLongitude));
//                                goToNavigation(String.valueOf(dropOffLatitude), String.valueOf(dropOffLongitude));
                                startJob(orderId);
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {
                            }
                        });
            }
        });
    }


    //START AND FINISH JOB--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void setGoToLocationButton(SmartButton button, final String pickUpLattitude, final String pickUpLongitude) {
        button.setText(getString(R.string.go_to_pick_up_location));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNavigation(pickUpLattitude, pickUpLongitude);
            }
        });
    }

    private void setFinishJobButton(SmartButton button) {
        button.setText(getString(R.string.finish_job));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartUtils.getConfirmDialog(TrackerHitcher.this, getString(R.string.finish_job), getString(R.string.sure_finish_job),
                        getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {

                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                finishJob(orderId);
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {

                            }
                        });
            }
        });
    }

    //DOING THE MAP THING--------------------------------------------------------------------------------------------------------------------------

    private void startJob(final String orderId) {

        SmartUtils.showLoadingDialog(TrackerHitcher.this);
        Map<String, String> params = new HashMap<>();

        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(GERMAN_STR)) {
            params.put(LANGUAGE, GERMAN_CODE);
        } else {
            params.put(LANGUAGE, ENGLISH_CODE);
        }
        params.put(ORDER_ID, orderId);
        params.put(LATITUDE, String.valueOf(currentLatitude));
        params.put(LONGITUDE, String.valueOf(currentLongitude));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderDeliveryStart");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TrackerHitcher.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();

                if (responseCode == 200) {
                    Log.d("@@@orderhistory_success", response.toString());

                    SmartUtils.getConfirmDialog(TrackerHitcher.this, "Job Started", getString(R.string.mdguser),
                            getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {

                                    try {
                                        Toast.makeText(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
                                        isTrackingStarted = true;
                                        setCurrentLocation(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                                        getOrderDetails(orderRequestId, SET_DATA_CODE);
                                        goToNavigation(String.valueOf(dropOffLatitude), String.valueOf(dropOffLongitude));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void NegativeMethod(DialogInterface dialog, int id) {
                                }

                            });

                } else if (responseCode == 400) {
                    try {
                        Toast.makeText(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("@@@orderhistory_success", response.toString());
                try {
                    Toast.makeText(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(TrackerHitcher.this).addToRequestQueueMultipart(requestParams, "", true);
    }

    private void finishJob(final String orderId) {
        SmartUtils.showLoadingDialog(TrackerHitcher.this);
        Map<String, String> params = new HashMap<>();
        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(GERMAN_STR)) {
            params.put(LANGUAGE, GERMAN_CODE);
        } else {
            params.put(LANGUAGE, ENGLISH_CODE);
        }

        params.put(ORDER_ID, orderId);
        params.put(LATITUDE, String.valueOf(currentLatitude));
        params.put(LONGITUDE, String.valueOf(currentLongitude));


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderDeliveryComplete");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TrackerHitcher.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                        Toast.makeText(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_LONG).show();

                        isTrackingStarted = false;
                        Intent i = new Intent(TrackerHitcher.this, Home.class);
                        i.putExtra(SET_PAGE, ORDER_HISTORY);
                        startActivity(i);
                        supportFinishAfterTransition();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        Toast.makeText(TrackerHitcher.this, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(TrackerHitcher.this).addToRequestQueueMultipart(requestParams, "", true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (pickUpLatitude != null) {
            LatLng startPoint = new LatLng(pickUpLatitude, pickUpLongitude);
            LatLng endPoint = new LatLng(dropOffLatitude, dropOffLongitude);

            String url = getDirectionsUrl(startPoint, endPoint);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private void goToNavigation(String lattitude, String longitude) {
        try {
            String UrlToGive = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + Uri.encode("(My address 1)") +
                    "&daddr=" + lattitude + "," + longitude + Uri.encode("(My address 2)");

            Log.e("@@url", UrlToGive);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(UrlToGive));
            startActivity(intent);

        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en")));
        }
    }


    //SERVICE & BROADCAST--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void startLocationService() {
        Intent serviceIntent = new Intent(TrackerHitcher.this, LocationService.class);
        serviceIntent.putExtra(ORDER_ID, orderId);
        serviceIntent.putExtra(ORDER_REQUEST_ID, orderRequestId);
        startService(serviceIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void startRefreshingTask() {
        refreshTracker.run();
    }

    //THREADS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void setCurrentLocation(String currentLatitude, String currentLongitude) {
        this.currentLatitude = Double.parseDouble(currentLatitude);
        this.currentLongitude = Double.parseDouble(currentLongitude);
    }

    private void setupMarkers(int from)  //setting up pick up location, drop off location and truck marker on the map
    {
        final View pickupMarkerView = TrackerHitcher.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
        final SmartTextView titlepickUpMarkerTv = pickupMarkerView.findViewById(R.id.title_marker_tv);
        final SmartTextView addresspickUpMarkerTv = pickupMarkerView.findViewById(R.id.address_marker_tv);

        titlepickUpMarkerTv.setText(getString(R.string.pickup_location));
        addresspickUpMarkerTv.setText(pickupAddress);


        final View droppOffMarkerView = TrackerHitcher.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
        final SmartTextView titledroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.title_marker_tv);
        final SmartTextView addressdroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.address_marker_tv);
        final SmartTextView clickDropoffTv = droppOffMarkerView.findViewById(R.id.click_dropoff_tv);

        try {
            if (orderDetailJson.getString(ORDER_STATUS).equalsIgnoreCase(STATUS_STARTED)) {
                clickDropoffTv.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        titledroppOffMarkerTv.setText(getString(R.string.dropoff_location));
        addressdroppOffMarkerTv.setText(dropoffAddress);

        mMap.addMarker(new MarkerOptions().position(new LatLng(pickUpLatitude, pickUpLongitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(TrackerHitcher.this, pickupMarkerView))));

        mMap.addMarker(new MarkerOptions().position(new LatLng(dropOffLatitude, dropOffLongitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(TrackerHitcher.this, droppOffMarkerView))));

        if (from != 0) {
            Log.d("@@FROM==", "" + from);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickUpLatitude, pickUpLongitude), 14));
        }
    }

    //SETTING A SETTER--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void drawTrackingPath(ArrayList<LatLng> trackingLatLongInner, int from) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(TrackerHitcher.this, R.color.accent));
        polyOptions.width(10);
        polyOptions.addAll(trackingLatLongInner);
        mMap.addPolyline(polyOptions);
        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.track)));
        if (from != 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14));
        }
    }

    //DRAW ON MAP--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mHandler != null) {
            mHandler.removeCallbacks(refreshTracker);
            mHandler.removeCallbacksAndMessages(null);
        }
        if (getIntent().getBooleanExtra("from_notification", false)) {
            startActivity(new Intent(TrackerHitcher.this, Home.class));
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("@@Map_Data", data);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            List<List<HashMap<String, String>>> routes = null;
            try {
                JSONObject jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            ArrayList<LatLng> points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(ContextCompat.getColor(TrackerHitcher.this, R.color.accent));
                lineOptions.geodesic(true);
            }
            // Drawing polyline in the Google Map for the i-th route

            if (points.size() > 0) {
                mMap.clear();
                mMap.addPolyline(lineOptions);
                setupMarkers(2);
            }
        }

    }
}
