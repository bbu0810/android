package com.hitchtransporter.transporter.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

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

import static android.widget.Toast.LENGTH_LONG;
import static com.hitchtransporter.smart.framework.SmartUtils.createDrawableFromView;
import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.extractDate;
import static com.hitchtransporter.smart.framework.SmartUtils.extractTime;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.getLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.getLongitude;
import static com.hitchtransporter.smart.framework.SmartUtils.timeFormatChanger3;

public class OrderDetails extends MasterActivity implements OnMapReadyCallback {

    private NestedScrollView orderDetailsScroll;
    private GoogleMap mMap;
    private MySupportMapFragment orderdetailMap;
    private RoundedImageView imageOrderdetailIv;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private SmartTextView nameOrderdetailTv;
    private SmartTextView addressOrderdetailTv;
    private SmartTextView timeOrderdetailTv;
    private SmartTextView dateOrderdetailTv;
    private SmartTextView amountOrderdetailTv;
    private SmartTextView pickupOrderdetailTv;
    private SmartTextView dropoffOrderdetailTv;
    private SmartTextView lengthOrderdetailTv;
    private SmartTextView heightOrderdetailTv;
    private SmartTextView widthOrderdetailTv;
    private SmartTextView weightOrderdetailTv;
    private SmartTextView distanceOrderdetailTv;
    private SmartTextView durationOrderdetailTv;
    private SmartTextView awaitingPaymentTv;

    private LinearLayout approveRejectLl;

    private SmartButton approveOrderdetailBtn;
    private SmartButton rejectOrderdetailBtn;

    private JSONObject orderDetailJson;

    private double pickup_latitude;
    private double pickup_longitude;
    private double dropoff_latitude;
    private double dropoff_longitude;

    private String pickupAddress;
    private String dropoffAddress;


    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_order_detail_2;
    }

    @Override
    public void initComponents() {
        super.initComponents();


        imageOrderdetailIv = findViewById(R.id.image_orderdetail_iv);
        orderdetailMap = (MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.orderdetail_map);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        orderDetailsScroll = findViewById(R.id.order_details_scroll);
        nameOrderdetailTv = findViewById(R.id.name_orderdetail_tv);
        addressOrderdetailTv = findViewById(R.id.address_orderdetail_tv);
        timeOrderdetailTv = findViewById(R.id.time_orderdetail_tv);
        dateOrderdetailTv = findViewById(R.id.date_orderdetail_tv);
        amountOrderdetailTv = findViewById(R.id.amount_orderdetail_tv);
        pickupOrderdetailTv = findViewById(R.id.pickup_orderdetail_tv);
        dropoffOrderdetailTv = findViewById(R.id.dropoff_orderdetail_tv);
        lengthOrderdetailTv = findViewById(R.id.length_orderdetail_tv);
        heightOrderdetailTv = findViewById(R.id.height_orderdetail_tv);
        widthOrderdetailTv = findViewById(R.id.width_orderdetail_tv);
        weightOrderdetailTv = findViewById(R.id.weight_orderdetail_tv);
        distanceOrderdetailTv = findViewById(R.id.distance_orderdetail_tv);
        durationOrderdetailTv = findViewById(R.id.duration_orderdetail_tv);
        awaitingPaymentTv = findViewById(R.id.awaiting_payment_tv);

        approveOrderdetailBtn = findViewById(R.id.approve_orderdetail_btn);
        rejectOrderdetailBtn = findViewById(R.id.reject_orderdetail_btn);

        approveRejectLl = findViewById(R.id.approve_reject_ll);

        if (orderdetailMap != null)
            orderdetailMap.setListener(new MySupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    orderDetailsScroll.requestDisallowInterceptTouchEvent(true);
                    collapsingToolbarLayout.requestDisallowInterceptTouchEvent(true);
                }
            });

        getOrderDetails();
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setHeaderToolbar(getString(R.string.order_details));
        setSwitch(false);
    }

    private void getOrderDetails() {
        SmartUtils.showLoadingDialog(OrderDetails.this);
        Map<String, String> params = new HashMap<>();
        params.put(ORDER_REQUEST_ID, getIntent().getStringExtra(ORDER_REQUEST_ID));
        params.put(LANGUAGE, getLanguageCode());
        params.put(CURRENT_LATITUDE, getLatitude());
        params.put(CURRENT_LONGITUDE, getLongitude());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "getOrderDetail");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, OrderDetails.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                        orderDetailJson = response.getJSONObject(DATA).getJSONObject("order_detail");
                        if (!TextUtils.isEmpty(orderDetailJson.getString(PROFILE_IMAGE))) {
                            Picasso.with(OrderDetails.this).load(orderDetailJson.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(imageOrderdetailIv);
                        }
                        nameOrderdetailTv.setText(orderDetailJson.getString(FIRST_NAME) + " " + orderDetailJson.getString(LAST_NAME));
                        addressOrderdetailTv.setText(orderDetailJson.getString(ADDRESS));
                        timeOrderdetailTv.setText(timeFormatChanger3(extractTime(orderDetailJson.getString(ORDER_DATETIME))));
                        dateOrderdetailTv.setText(dateFormatChanger(extractDate(orderDetailJson.getString(ORDER_DATETIME))));
                        amountOrderdetailTv.setText(€ + decimalFormatter(orderDetailJson.getString(AMOUNT)));
                        amountOrderdetailTv.setText(€ + decimalFormatter(orderDetailJson.getString(AMOUNT)));
                        distanceOrderdetailTv.setText(decimalFormatter(orderDetailJson.getString(DISTANCE)) + " " + Km);
                        durationOrderdetailTv.setText(orderDetailJson.getString(DURATION));
                        pickupAddress = orderDetailJson.getString(PICKUP_ADDRESS);
                        pickupOrderdetailTv.setText(pickupAddress);
                        dropoffAddress = orderDetailJson.getString(DROPOFF_ADDRESS);
                        dropoffOrderdetailTv.setText(dropoffAddress);
                        lengthOrderdetailTv.setText(orderDetailJson.getString(LENGTH) + " " + Cm);
                        heightOrderdetailTv.setText(orderDetailJson.getString(HEIGHT) + " " + Cm);
                        widthOrderdetailTv.setText(orderDetailJson.getString(WIDTH) + " " + Cm);
                        weightOrderdetailTv.setText(orderDetailJson.getString(WEIGHT) + " " + Kg);
                        pickup_latitude = Double.parseDouble(orderDetailJson.getString(PICKUP_LATITUDE));
                        pickup_longitude = Double.parseDouble(orderDetailJson.getString(PICKUP_LONGITUDE));
                        dropoff_latitude = Double.parseDouble(orderDetailJson.getString(DROPOFF_LATITUDE));
                        dropoff_longitude = Double.parseDouble(orderDetailJson.getString(DROPOFF_LONGITUDE));

                        if (orderDetailJson.getString(ORDER_STATUS).equalsIgnoreCase(STATUS_PENDING)) {
                            approveRejectLl.setVisibility(View.VISIBLE);
                            awaitingPaymentTv.setVisibility(View.GONE);

                            approveOrderdetailBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkBankDetailsAndConfirm();
                                }
                            });


                            rejectOrderdetailBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SmartUtils.getConfirmDialog(OrderDetails.this, getString(R.string.reject_request), getString(R.string.reject_message),
                                            getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {

                                                @Override
                                                public void PositiveMethod(DialogInterface dialog, int id) {
                                                    try {
                                                        orderApproveReject(orderDetailJson.getString(ORDER_ID), REJECT_CODE);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void NegativeMethod(DialogInterface dialog, int id) {
                                                }
                                            });
                                }
                            });
                        } else if (orderDetailJson.getString(ORDER_STATUS).equalsIgnoreCase(STATUS_APPROVED)) {
                            if (orderDetailJson.getString(IS_PAY).equalsIgnoreCase(STATUS_UNPAID)) {
                                approveRejectLl.setVisibility(View.GONE);
                                awaitingPaymentTv.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    orderdetailMap.getMapAsync(OrderDetails.this);


                } else if (responseCode == 400)

                {
                    try {
                        SmartUtils.showSnackBar(OrderDetails.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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
        SmartWebManager.getInstance(

                getApplicationContext()).

                addToRequestQueueMultipart(requestParams, "", true);
    }

    private void orderApproveReject(String orderId, String approve_reject) {
        SmartUtils.showLoadingDialog(OrderDetails.this);
        Map<String, String> params = new HashMap<>();

        params.put(LANGUAGE, getLanguageCode());
        params.put(TRANSPORTER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_DATA));
        params.put(ORDER_ID, orderId);
        params.put(APPROVE_REJECT, approve_reject);


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderApproveReject");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, OrderDetails.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    try {
                        Toast.makeText(OrderDetails.this, response.getJSONObject(DATA).getString(MESSAGE), LENGTH_LONG).show();
                        Intent i = new Intent(OrderDetails.this, Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        supportFinishAfterTransition();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(OrderDetails.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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

    private void checkBankDetailsAndConfirm() {
        SmartUtils.showLoadingDialog(OrderDetails.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "getBankDetail");

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, OrderDetails.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@account_success", response.toString());

                    SmartUtils.getConfirmDialog(OrderDetails.this, getString(R.string.approve_request), getString(R.string.approve_message),
                            getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {
                                    try {
                                        orderApproveReject(orderDetailJson.getString(ORDER_ID), APPROVE_CODE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void NegativeMethod(DialogInterface dialog, int id) {
                                }
                            });

                } else if (responseCode == 400) {
                    //editDetails();
                    SmartUtils.getConfirmDialog(OrderDetails.this, getString(R.string.no_bank), getString(R.string.bank_setup),
                            getString(R.string.yes), getString(R.string.cancel), true, new AlertMagnatic() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {
                                    startActivity(new Intent(OrderDetails.this, BankAccount.class));
                                    supportFinishAfterTransition();
                                }

                                @Override
                                public void NegativeMethod(DialogInterface dialog, int id) {
                                }
                            });
                  /*  try {
                        SmartUtils.showSnackBar(OrderDetails.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
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

    // Convert a view to bitmap

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
            final ArrayList<LatLng> points = new ArrayList<>();
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
                lineOptions.color(ContextCompat.getColor(OrderDetails.this, R.color.accent));
                lineOptions.geodesic(true);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (points.size() > 0) {
                mMap.addPolyline(lineOptions);

                // LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View pickupMarkerView = OrderDetails.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
                final SmartTextView titlepickUpMarkerTv = pickupMarkerView.findViewById(R.id.title_marker_tv);
                final SmartTextView addresspickUpMarkerTv = pickupMarkerView.findViewById(R.id.address_marker_tv);

                titlepickUpMarkerTv.setText(getString(R.string.pickup_location));
                addresspickUpMarkerTv.setText(pickupAddress);


                final View droppOffMarkerView = OrderDetails.this.getLayoutInflater().inflate(R.layout.tracker_marker_info, null);
                final SmartTextView titledroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.title_marker_tv);
                final SmartTextView addressdroppOffMarkerTv = droppOffMarkerView.findViewById(R.id.address_marker_tv);


                titledroppOffMarkerTv.setText(getString(R.string.dropoff_location));
                addressdroppOffMarkerTv.setText(dropoffAddress);

                mMap.addMarker(new MarkerOptions().position(new LatLng(pickup_latitude, pickup_longitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(OrderDetails.this, pickupMarkerView))));
                mMap.addMarker(new MarkerOptions().position(new LatLng(dropoff_latitude, dropoff_longitude)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(OrderDetails.this, droppOffMarkerView))));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 14));


            }
        }

    }
}
