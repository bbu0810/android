package com.hitchtransporter.transporter.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectAddress extends MasterActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private EditText edtAddress;
    private PlaceAutocompleteFragment autocompleteFragment;

    private SmartButton btnConfirm;

    private String IN_SELECTED_ADDRESS;
    private LatLng IN_SELECTED_ADDRESS_LATLONG;

    private ArrayList<HashMap<String, String>> result = new ArrayList<>();
    private HashMap<String, String> hashMap_address = new HashMap<>();

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public int getLayoutID() {
        return R.layout.select_address_screen;
    }

    @Override
    public void initComponents() {
        super.initComponents();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete);

        edtAddress = autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        edtAddress.setPadding(0, 0, 16, 0);
        edtAddress.setTextSize(15f);
        edtAddress.setHint(getString(R.string.search_here));
        edtAddress.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.font_normal));
        edtAddress.setTypeface(typeface);

        btnConfirm = findViewById(R.id.btnConfirm);
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                IN_SELECTED_ADDRESS_LATLONG = place.getLatLng();

                Address address = SmartUtils.getAddressFromLatLong(SelectAddress.this,
                        IN_SELECTED_ADDRESS_LATLONG.latitude,
                        IN_SELECTED_ADDRESS_LATLONG.longitude);

                Log.d("k_inSelectedAddress", "returned_address@@==" + address);

                if (address != null) {
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    for (int i = 0; i < address.getMaxAddressLineIndex() + 1; i++) {
                        strReturnedAddress.append(address.getAddressLine(i)).append(",");
                    }
                    Log.d("k_inSelectedAddress", "street==" + address.getThoroughfare());
                    Log.d("k_inSelectedAddress", "state==" + address.getAdminArea());
                    Log.d("k_inSelectedAddress", "city==" + address.getLocality());
                    Log.d("k_inSelectedAddress", "country==" + address.getCountryName());
                    Log.d("k_inSelectedAddress", "latitude==" + address.getLatitude());
                    Log.d("k_inSelectedAddress", "longitude==" + address.getLongitude());

                    IN_SELECTED_ADDRESS = strReturnedAddress.deleteCharAt(strReturnedAddress.length() - 1).toString();

                    result = new ArrayList<>();
                    hashMap_address = new HashMap<>();
                    hashMap_address.put(LATITUDE, String.valueOf(address.getLatitude()));
                    hashMap_address.put(LONGITUDE, String.valueOf(address.getLongitude()));
                    hashMap_address.put(STREET, address.getAddressLine(0));
                    hashMap_address.put(CITY, address.getLocality());
                    hashMap_address.put(STATE, address.getAdminArea());
                    hashMap_address.put(COUNTRY, address.getCountryName());
                    hashMap_address.put(ADDRESS, IN_SELECTED_ADDRESS);
                    result.add(hashMap_address);

                    googleMap.clear();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(IN_SELECTED_ADDRESS_LATLONG).tilt(50).zoom(17).build();
                    googleMap.addMarker(new MarkerOptions().position(IN_SELECTED_ADDRESS_LATLONG).draggable(true));
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(IN_SELECTED_ADDRESS) && !TextUtils.isEmpty(edtAddress.getText().toString())) {

                    Intent intent = new Intent();
                    intent.putExtra(ADDRESS, result);
                    setResult(RESULT_OK, intent);
                    supportFinishAfterTransition();
                } else {

                    SmartUtils.showSnackBar(SelectAddress.this, getString(R.string.enter_address), Snackbar.LENGTH_LONG);
                /*    setResult(RESULT_CANCELED);
                    supportFinishAfterTransition();
                */
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMarkerDragListener(this);

        result = (ArrayList<HashMap<String, String>>)
                getIntent().getSerializableExtra("IN_SELECTED_ADDRESS");
        if (result != null && result.size() > 0) {

            IN_SELECTED_ADDRESS = result.get(0).get(ADDRESS);

            edtAddress.setText(IN_SELECTED_ADDRESS);

            googleMap.clear();

            IN_SELECTED_ADDRESS_LATLONG = new LatLng(Double.parseDouble(result.get(0).get(LATITUDE)),
                    Double.parseDouble(result.get(0).get(LONGITUDE)));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(IN_SELECTED_ADDRESS_LATLONG).tilt(50).zoom(17).build();
            googleMap.addMarker(new MarkerOptions().position(IN_SELECTED_ADDRESS_LATLONG).draggable(true));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {

            double givenLatitude, givenLongitude;
            if (getIntent().getStringExtra(LATITUDE) != null && getIntent().getStringExtra(LONGITUDE) != null) {
                givenLatitude = Double.parseDouble(getIntent().getStringExtra(LATITUDE));
                givenLongitude = Double.parseDouble(getIntent().getStringExtra(LONGITUDE));
            } else {

                givenLatitude = Double.parseDouble(SmartUtils.getLatitude());
                givenLongitude = Double.parseDouble(SmartUtils.getLongitude());
            }
            Address address = SmartUtils.getAddressFromLatLong(SelectAddress.this, givenLatitude, givenLongitude);


            Log.d("k_inSelectedAddress", "returned_address@@==" + address);

            if (address != null) {


                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < address.getMaxAddressLineIndex() + 1; i++) {
                    strReturnedAddress.append(address.getAddressLine(i)).append(",");
                }
                Log.d("k_inSelectedAddress", "street==" + address.getThoroughfare());
                Log.d("k_inSelectedAddress", "state==" + address.getAdminArea());
                Log.d("k_inSelectedAddress", "city==" + address.getLocality());
                Log.d("k_inSelectedAddress", "country==" + address.getCountryName());

                IN_SELECTED_ADDRESS = strReturnedAddress.deleteCharAt(strReturnedAddress.length() - 1).toString();

                result = new ArrayList<>();

                hashMap_address.put(ADDRESS_LINE_1, address.getAddressLine(0));
                hashMap_address.put(LATITUDE, String.valueOf(address.getLatitude()));
                hashMap_address.put(LONGITUDE, String.valueOf(address.getLongitude()));
                hashMap_address.put(STREET, address.getThoroughfare());
                hashMap_address.put(CITY, address.getLocality());
                hashMap_address.put(STATE, address.getAdminArea());
                hashMap_address.put(COUNTRY, address.getCountryName());
                hashMap_address.put(ADDRESS, IN_SELECTED_ADDRESS);

                result.add(hashMap_address);

                if (getIntent().getBooleanExtra(IS_DROPOFF, false)) {
                    if (getIntent().getStringExtra(LONGITUDE) != null) {
                        edtAddress.setText(IN_SELECTED_ADDRESS);
                    }
                } else {
                    edtAddress.setText(IN_SELECTED_ADDRESS);
                }

                googleMap.clear();

                IN_SELECTED_ADDRESS_LATLONG = new LatLng(address.getLatitude(), address.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(IN_SELECTED_ADDRESS_LATLONG).tilt(50).zoom(17).build();
                googleMap.addMarker(new MarkerOptions().position(IN_SELECTED_ADDRESS_LATLONG).draggable(true));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        /*googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                Address address = SmartUtils.getAddressFromLatLong(SelectAddress.this,
                        arg0.getPosition().latitude,
                        arg0.getPosition().longitude);

                Log.d("k_inSelectedAddress", "returned_address@@==" + address);

                if (address != null) {
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    for (int i = 0; i < address.getMaxAddressLineIndex() + 1; i++) {
                        strReturnedAddress.append(address.getAddressLine(i)).append(",");
                    }
                    Log.d("k_inSelectedAddress", "street==" + address.getThoroughfare());
                    Log.d("k_inSelectedAddress", "state==" + address.getAdminArea());
                    Log.d("k_inSelectedAddress", "city==" + address.getLocality());
                    Log.d("k_inSelectedAddress", "country==" + address.getCountryName());
                    Log.d("k_inSelectedAddress", "latitude==" + address.getLatitude());
                    Log.d("k_inSelectedAddress", "longitude==" + address.getLongitude());

                    IN_SELECTED_ADDRESS = strReturnedAddress.deleteCharAt(strReturnedAddress.length() - 1).toString();

                    result = new ArrayList<>();
                    hashMap_address = new HashMap<>();
                    hashMap_address.put(LATITUDE, String.valueOf(address.getLatitude()));
                    hashMap_address.put(LONGITUDE, String.valueOf(address.getLongitude()));
                    hashMap_address.put(STREET, address.getAddressLine(0));
                    hashMap_address.put(CITY, address.getLocality());
                    hashMap_address.put(STATE, address.getAdminArea());
                    hashMap_address.put(COUNTRY, address.getCountryName());
                    hashMap_address.put(ADDRESS, IN_SELECTED_ADDRESS);
                    result.add(hashMap_address);

                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                }

            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });*/
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        Address address = SmartUtils.getAddressFromLatLong(SelectAddress.this, dragPosition.latitude,
                dragPosition.longitude);
        //if (address != null) {

            /*StringBuilder strReturnedAddress = new StringBuilder("");
            for (int i = 0; i < address.getMaxAddressLineIndex() + 1; i++) {
                strReturnedAddress.append(address.getAddressLine(i)).append(",");
            }
            IN_SELECTED_ADDRESS = strReturnedAddress.deleteCharAt(strReturnedAddress.length() - 1).toString();
            edtAddress.setText(IN_SELECTED_ADDRESS);*/

        if (address != null) {


            StringBuilder strReturnedAddress = new StringBuilder("");
            for (int i = 0; i < address.getMaxAddressLineIndex() + 1; i++) {
                strReturnedAddress.append(address.getAddressLine(i)).append(",");
            }
            Log.d("k_inSelectedAddress", "street==" + address.getThoroughfare());
            Log.d("k_inSelectedAddress", "state==" + address.getAdminArea());
            Log.d("k_inSelectedAddress", "city==" + address.getLocality());
            Log.d("k_inSelectedAddress", "country==" + address.getCountryName());

            IN_SELECTED_ADDRESS = strReturnedAddress.deleteCharAt(strReturnedAddress.length() - 1).toString();

            result = new ArrayList<>();

            hashMap_address.put(ADDRESS_LINE_1, address.getAddressLine(0));
            hashMap_address.put(LATITUDE, String.valueOf(address.getLatitude()));
            hashMap_address.put(LONGITUDE, String.valueOf(address.getLongitude()));
            hashMap_address.put(STREET, address.getThoroughfare());
            hashMap_address.put(CITY, address.getLocality());
            hashMap_address.put(STATE, address.getAdminArea());
            hashMap_address.put(COUNTRY, address.getCountryName());
            hashMap_address.put(ADDRESS, IN_SELECTED_ADDRESS);

            result.add(hashMap_address);
            edtAddress.setText(IN_SELECTED_ADDRESS);

            googleMap.clear();

            IN_SELECTED_ADDRESS_LATLONG = new LatLng(address.getLatitude(), address.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(IN_SELECTED_ADDRESS_LATLONG).tilt(50).zoom(17).build();
            googleMap.addMarker(new MarkerOptions().position(IN_SELECTED_ADDRESS_LATLONG).draggable(true));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //}

           /* googleMap.clear();

            IN_SELECTED_ADDRESS_LATLONG = new LatLng(address.getLatitude(), address.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(IN_SELECTED_ADDRESS_LATLONG).tilt(50).zoom(17).build();
            googleMap.addMarker(new MarkerOptions().position(IN_SELECTED_ADDRESS_LATLONG).draggable(true));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }
}
