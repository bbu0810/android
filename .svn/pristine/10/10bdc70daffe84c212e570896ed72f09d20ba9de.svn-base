package com.hitchtransporter.transporter.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.hitchtransporter.transporter.AA_Classes.MySupportMapFragment;
import com.squareup.picasso.Picasso;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.common.DirectionsJSONParser;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;

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

public class TransporterRequest extends MasterActivity implements OnMapReadyCallback {
    private NestedScrollView transporterRequestScroll;
    private GoogleMap mMap;
    private MySupportMapFragment trasporterRequestMap;
    private RoundedImageView transporterImageRequestIv;
    private SmartTextView transporterNameRequestTv;
    private SmartTextView transporterAddressRequestTv;
    private RatingBar transporterRatingRequestRb;
    private SmartTextView transporterRatingRequestTv;
    private SmartTextView transporterCommentTv;
    private SmartTextView requestDistanceTv;
    private SmartTextView requestTimeTv;
    private SmartTextView requestPriceTv;
    private SmartTextView requestPickupTv;
    private SmartTextView requestDropoffTv;
    private SmartTextView viewReviewsTv;


    private SmartTextView lengthRequestdetailTv;
    private SmartTextView heightRequestdetailTv;
    private SmartTextView widthRequestdetailTv;
    private SmartTextView weightRequestdetailTv;

    private SmartButton requestBtn;

    private double pickup_latitude;
    private double pickup_longitude;
    private double dropoff_latitude;
    private double dropoff_longitude;

    private Dialog infoDialog;


    private String pickupAddress;
    private String dropoffAddress;


    @Override
    public int getLayoutID() {
        return R.layout.activity_transporter_request;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        setHeaderToolbar(getString(R.string.request_hitcher));
        setSwitch(false);

        transporterRequestScroll = findViewById(R.id.tranporter_request_scroll);
        trasporterRequestMap = (MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trasporter_request_map);
        transporterImageRequestIv = findViewById(R.id.transporter_image_request_iv);
        transporterNameRequestTv = findViewById(R.id.transporter_name_request_tv);
        transporterAddressRequestTv = findViewById(R.id.transporter_address_request_tv);
        transporterRatingRequestRb = findViewById(R.id.transporter_rating_request_rb);
        transporterRatingRequestTv = findViewById(R.id.transporter_rating_request_tv);
        transporterCommentTv = findViewById(R.id.request_comment_tv);


        requestDistanceTv = findViewById(R.id.request_distance_tv);
        requestTimeTv = findViewById(R.id.request_time_tv);
        requestPriceTv = findViewById(R.id.request_price_tv);
        requestPickupTv = findViewById(R.id.request_pickup_tv);
        requestDropoffTv = findViewById(R.id.request_dropoff_tv);
        requestBtn = findViewById(R.id.request_btn);
        viewReviewsTv = findViewById(R.id.view_reviews_tv);


        lengthRequestdetailTv = findViewById(R.id.length_request_tv);
        heightRequestdetailTv = findViewById(R.id.height_request_tv);
        widthRequestdetailTv = findViewById(R.id.width_request_tv);
        weightRequestdetailTv = findViewById(R.id.weight_request_tv);

        if (trasporterRequestMap != null)

            trasporterRequestMap.setListener(new MySupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    transporterRequestScroll.requestDisallowInterceptTouchEvent(true);
                }
            });


        getOrderTransporterData();
    }


    @Override
    public void setActionListeners() {
        super.setActionListeners();
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requsetTransporter();
            }
        });

        viewReviewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransporterRequest.this, ReviewList.class);
                i.putExtra(TRANSPORTER_ID, getIntent().getStringExtra(TRANSPORTER_ID));
                startActivity(i);
            }
        });
    }

    private void getOrderTransporterData() {
        SmartUtils.showLoadingDialog(TransporterRequest.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, getIntent().getStringExtra(ORDER_ID));
        params.put(TRANSPORTER_ID, getIntent().getStringExtra(TRANSPORTER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "transporterProfile");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TransporterRequest.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.e("@@@REQUEST_TRANSPORTER", response.toString());
                    try {
                        JSONObject transporter_data = response.getJSONObject(DATA).getJSONObject("transporter_detail");
                        JSONObject order_data = response.getJSONObject(DATA).getJSONObject("order_data");

                        if (!TextUtils.isEmpty(transporter_data.getString(PROFILE_IMAGE))) {
                            Picasso.with(TransporterRequest.this).load(transporter_data.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(transporterImageRequestIv);
                        }
                        transporterNameRequestTv.setText(transporter_data.getString(FIRST_NAME) + " " + transporter_data.getString(LAST_NAME));
                        transporterAddressRequestTv.setText(transporter_data.getString(ADDRESS));
                        transporterRatingRequestRb.setRating(Float.parseFloat(transporter_data.getString(RATING)));
                        transporterRatingRequestTv.setText(transporter_data.getString(RATING));

                        transporterCommentTv.setText(transporter_data.getString(PROFILE_DETAIL));
                        requestDistanceTv.setText(decimalFormatter(order_data.getString(DISTANCE)) + " " + Km);
                        requestTimeTv.setText(order_data.getString(DURATION));
                        requestPriceTv.setText(€ + decimalFormatter(transporter_data.getString(AMOUNT)));

                        pickupAddress = order_data.getString(PICKUP_ADDRESS);
                        requestPickupTv.setText(pickupAddress);
                        dropoffAddress = order_data.getString(DROPOFF_ADDRESS);
                        requestDropoffTv.setText(dropoffAddress);

                        pickup_latitude = Double.parseDouble(order_data.getString(PICKUP_LATITUDE));
                        pickup_longitude = Double.parseDouble(order_data.getString(PICKUP_LONGITUDE));
                        dropoff_latitude = Double.parseDouble(order_data.getString(DROPOFF_LATITUDE));
                        dropoff_longitude = Double.parseDouble(order_data.getString(DROPOFF_LONGITUDE));

                        lengthRequestdetailTv.setText(transporter_data.getString(BOX_LENGTH) + " " + Cm);
                        heightRequestdetailTv.setText(transporter_data.getString(BOX_HEIGHT) + " " + Cm);
                        weightRequestdetailTv.setText(transporter_data.getString(BOX_WEIGHT) + " " + Kg);
                        widthRequestdetailTv.setText(transporter_data.getString(BOX_WIDTH) + " " + Cm);


                        trasporterRequestMap.getMapAsync(TransporterRequest.this);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(TransporterRequest.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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

    private void requsetTransporter() {
        SmartUtils.showLoadingDialog(TransporterRequest.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, getIntent().getStringExtra(ORDER_ID));
        params.put(TRANSPORTER_ID, getIntent().getStringExtra(TRANSPORTER_ID));
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderRequest");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, TransporterRequest.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@request_success", ": true");
                    showInfoDialog();
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(TransporterRequest.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng startPoint = new LatLng(pickup_latitude,
                pickup_longitude);
        LatLng endPoint = new LatLng(dropoff_latitude,
                dropoff_longitude);
        String url = getDirectionsUrl(startPoint, endPoint);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);


    }


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
                lineOptions.color(ContextCompat.getColor(TransporterRequest.this, R.color.accent));
                lineOptions.geodesic(true);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (points.size() > 0) {
                mMap.addPolyline(lineOptions);

                final View pickupMarkerView = TransporterRequest.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
                final SmartTextView titlepickUpMarkerTv = pickupMarkerView.findViewById(R.id.title_marker_tv);
                final SmartTextView addresspickUpMarkerTv = pickupMarkerView.findViewById(R.id.address_marker_tv);

                titlepickUpMarkerTv.setText(getString(R.string.pickup_location));
                addresspickUpMarkerTv.setText(pickupAddress);


                final View droppOffMarkerView = TransporterRequest.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
                final SmartTextView titledroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.title_marker_tv);
                final SmartTextView addressdroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.address_marker_tv);


                titledroppOffMarkerTv.setText(getString(R.string.dropoff_location));
                addressdroppOffMarkerTv.setText(dropoffAddress);

                mMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(TransporterRequest.this, pickupMarkerView))));
                mMap.addMarker(new MarkerOptions().position(new LatLng(dropoff_latitude, dropoff_longitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(TransporterRequest.this, droppOffMarkerView))));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 14));
            }
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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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

    private void showInfoDialog() {
        infoDialog = new Dialog(TransporterRequest.this);

        infoDialog.setCancelable(false);
        //    infoDialog.setCanceledOnTouchOutside(false);

        infoDialog.setContentView(R.layout.dialog_request_info);

        SmartButton requestInfoBtn = infoDialog.findViewById(R.id.request_info_btn);

        requestInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
                Intent i = new Intent(TransporterRequest.this, Home.class);
                i.putExtra(SET_PAGE, ORDER_HISTORY);
                startActivity(i);
                supportFinishAfterTransition();

            }
        });


        infoDialog.show();
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
}
