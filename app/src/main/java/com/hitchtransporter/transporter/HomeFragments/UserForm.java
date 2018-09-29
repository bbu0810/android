package com.hitchtransporter.transporter.HomeFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.CustomClickListener;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartFragment;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MySupportMapFragment;
import com.hitchtransporter.transporter.Activities.SelectAddress;
import com.hitchtransporter.transporter.Activities.TransporterRequest;

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
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed3;
import static com.hitchtransporter.smart.framework.SmartUtils.getCurrentDate;
import static com.hitchtransporter.smart.framework.SmartUtils.getDateTimeDialog;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.getTimeStampFromString;

public class UserForm extends SmartFragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private LinearLayout filterLl;
    private RelativeLayout darkBg;
    private RelativeLayout filter2;
    private RelativeLayout filter3;
    private ImageView filter2Iv;
    private ImageView filter3Iv;
    private ImageView filterIv;

    public Boolean isFilterOpen = false;


    private RecyclerView availableTransporterRv;
    AvailableTransporterAdapter availableTransporterAdapter;
    private MySupportMapFragment userHomeMap;
    private View availableTransporter;
    private View noDataFound;
    public boolean isSetForm = true;
    private String[] sizes = new String[]{EXTRA_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE};

    private static int ADDRESS_PICKUP_CODE = 901;
    private static int ADDRESS_DROPOFF_CODE = 902;

    private NestedScrollView formScrollView;
    private LinearLayout trasporterFormLl;
    private SmartTextView noDataFoundTv;
    private SmartEditText formPickupEt;
    private SmartEditText formDropoffEt;
    private SmartEditText formTimeEt;
    //private SmartEditText formPriceRangeEt;
    private SmartEditText formWeightEt;
    private SmartEditText formHeightEt;
    private SmartEditText formWidthEt;
    //private SmartEditText formDistanceRangeEt;
    private SmartEditText formLengthEt;
    private SmartEditText focusEt;
    private SmartButton formSendBtn;
    private String order_id;

    private Double pickup_latitude;
    private Double pickup_longitude;
    private Double dropoff_latitude;
    private Double dropoff_longitude;

    private String distance, duration;

    private Dialog setRangeDialog;


    private JSONArray transporterDetailsJson = new JSONArray();
    private ArrayList<JSONObject> transporterDetailArray = new ArrayList<>();
    private HashMap<Marker, JSONObject> markerHashMap = new HashMap<>();

    public UserForm() {
        // Required empty public constructor
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_transporter_form;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {


        formScrollView = currentView.findViewById(R.id.form_scroll_view);

        filterLl = currentView.findViewById(R.id.filter_ll);

        filter2 = currentView.findViewById(R.id.filter_2);
        filter3 = currentView.findViewById(R.id.filter_3);

        darkBg = currentView.findViewById(R.id.darkBg);


        filter2Iv = currentView.findViewById(R.id.filter_2_iv);
        filter3Iv = currentView.findViewById(R.id.filter_3_iv);

        filterIv = currentView.findViewById(R.id.filter_iv);

        noDataFoundTv = currentView.findViewById(R.id.no_data_found_tv);
        formPickupEt = currentView.findViewById(R.id.form_pickup_et);
        formDropoffEt = currentView.findViewById(R.id.form_dropoff_et);
        formTimeEt = currentView.findViewById(R.id.form_time_et);
        formHeightEt = currentView.findViewById(R.id.form_height_et);
        formWeightEt = currentView.findViewById(R.id.form_weight_et);
        formWidthEt = currentView.findViewById(R.id.form_width_et);
        formLengthEt = currentView.findViewById(R.id.form_length_et);
        focusEt = currentView.findViewById(R.id.focus_et);
        formSendBtn = currentView.findViewById(R.id.form_send_btn);

        // formPriceRangeEt = currentView.findViewById(R.id.form_price_range_et);
        //formDistanceRangeEt = currentView.findViewById(R.id.form_distance_range_et);

        userHomeMap = (MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_home_map);

        availableTransporter = currentView.findViewById(R.id.available_transporter);
        noDataFound = currentView.findViewById(R.id.layout_no_data);

        trasporterFormLl = currentView.findViewById(R.id.trasporter_form_ll);

        availableTransporterRv = currentView.findViewById(R.id.available_transporter_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        availableTransporterRv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(availableTransporterRv.getContext(),
                linearLayoutManager.getOrientation());
        mDividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.list_devider));
        availableTransporterRv.addItemDecoration(mDividerItemDecoration);

        availableTransporterAdapter = new AvailableTransporterAdapter();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sizes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (userHomeMap != null)

            userHomeMap.setListener(new MySupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    formScrollView.requestDisallowInterceptTouchEvent(true);
                }
            });


    }

    @Override
    public void prepareViews(View currentView) {

    }


    @Override
    public void setActionListeners(View currentView) {
        formPickupEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectAddress.class);
                if (pickup_latitude != null && pickup_longitude != null) {
                    i.putExtra(LATITUDE, String.valueOf(pickup_latitude));
                    i.putExtra(LONGITUDE, String.valueOf(pickup_longitude));
                }
                getActivity().startActivityForResult(i, ADDRESS_PICKUP_CODE);
            }
        });
        formDropoffEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectAddress.class);
                i.putExtra(IS_DROPOFF, true);
                if (dropoff_latitude != null && dropoff_longitude != null) {
                    i.putExtra(LATITUDE, String.valueOf(dropoff_latitude));
                    i.putExtra(LONGITUDE, String.valueOf(dropoff_longitude));
                }
                getActivity().startActivityForResult(i, ADDRESS_DROPOFF_CODE);
            }
        });
        formTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateTimeDialog(getActivity(), getCurrentDate(), true, new CustomClickListener() {
                    @Override
                    public void onClick(String value) {
                        formTimeEt.setText(value);
                    }
                }, getString(R.string.date_time_format));
            }
        });
        formSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    getOrderDistance();
                }
            }
        });

        filterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilterOpen) {
                    closeFilter();
                } else {
                    openFilter();
                }
            }
        });


        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(PRICE_CODE, "");
                filter3Iv.setImageResource(R.drawable.ic_round_unselected);
                filter2Iv.setImageResource(R.drawable.ic_round_selected);
                closeFilter();
            }
        });

        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRange();
            }
        });
    }

    private void setRange() {
        setRangeDialog = new Dialog(getActivity());
        setRangeDialog.setContentView(R.layout.dialog_set_range);

        final SmartEditText selectRangeEt = setRangeDialog.findViewById(R.id.select_range_et);
        SmartButton setRangeBtn = setRangeDialog.findViewById(R.id.set_range_btn);
        SmartButton cancelRangeBtn = setRangeDialog.findViewById(R.id.cancel_range_btn);
        setRangeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        setRangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectRangeEt.getText().toString())) {
                    filter("2", selectRangeEt.getText().toString());
                    filter2Iv.setImageResource(R.drawable.ic_round_unselected);
                    filter3Iv.setImageResource(R.drawable.ic_round_selected);
                    closeFilter();
                    setRangeDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.range_empty_toast), Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelRangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRangeDialog.dismiss();
            }
        });

        setRangeDialog.show();
    }


    public void closeFilter() {
        Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slidein_animation);
        slideIn.setAnimationListener(animationListener);

        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout_anmation);
        filterLl.startAnimation(slideIn);

        isFilterOpen = false;

        darkBg.startAnimation(fadeOut);
        darkBg.setVisibility(View.GONE);
    }

    private void openFilter() {
        filterLl.setVisibility(View.VISIBLE);

        Animation slideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slideout_animation);

        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fadein_anmation);
        filterLl.startAnimation(slideOut);

        isFilterOpen = true;

        darkBg.setVisibility(View.VISIBLE);

        darkBg.startAnimation(fadeIn);
        darkBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter();
            }
        });
    }


    public void setForm() {
        trasporterFormLl.setVisibility(View.VISIBLE);
        availableTransporter.setVisibility(View.GONE);
        formScrollView.smoothScrollTo(0, 0);
        isSetForm = true;
        setHasOptionsMenu(false);
    }

    public void setAvailableTransporter() {
        if (transporterDetailsJson.length() > 0) {
            setHasOptionsMenu(true);
        }
        trasporterFormLl.setVisibility(View.GONE);
        availableTransporter.setVisibility(View.VISIBLE);
        focusEt.requestFocus();
        isSetForm = false;
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (filterLl.getVisibility() == View.VISIBLE) {
                filterLl.clearAnimation();
                filterLl.setVisibility(View.GONE);
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    private boolean check() {
        boolean isValid = true;
        /*if (TextUtils.isEmpty(formDistanceRangeEt.getText().toString())) {
            focusEditTextRed(getActivity(), formDistanceRangeEt, true, null, 0);
            isValid = false;
        }*/
        if (TextUtils.isEmpty(formLengthEt.getText().toString())) {
            focusEditTextRed(getActivity(), formLengthEt, true, null, 0);
            isValid = false;
        }
        if (TextUtils.isEmpty(formWidthEt.getText().toString())) {
            focusEditTextRed(getActivity(), formWidthEt, true, null, 0);
            isValid = false;
        }

        if (TextUtils.isEmpty(formHeightEt.getText().toString())) {
            focusEditTextRed(getActivity(), formHeightEt, true, null, 0);
            isValid = false;
        }
        if (TextUtils.isEmpty(formWeightEt.getText().toString())) {
            focusEditTextRed(getActivity(), formWeightEt, true, null, 0);
            isValid = false;
        }
        /*if (TextUtils.isEmpty(formPriceRangeEt.getText().toString())) {
            focusEditTextRed(getActivity(), formPriceRangeEt, true, null, 0);
            isValid = false;
        }*/
        if (TextUtils.isEmpty(formTimeEt.getText().toString())) {
            focusEditTextRed(getActivity(), formTimeEt, true, null, 0);
            isValid = false;
        } else if (getTimeStampFromString(formTimeEt.getText().toString(), getString(R.string.date_time_format)) < System.currentTimeMillis()) {
            Log.d("@@TIME_CHECK===", getTimeStampFromString(formTimeEt.getText().toString(), getString(R.string.date_time_format)) + "-----" + System.currentTimeMillis());
            focusEditTextRed(getActivity(), formTimeEt, true, getString(R.string.validation_order_current_time), 0);
            isValid = false;
        }
        if (TextUtils.isEmpty(formPickupEt.getText().toString())) {
            focusEditTextRed(getActivity(), formPickupEt, true, null, 0);
            isValid = false;
        }
        if (TextUtils.isEmpty(formDropoffEt.getText().toString())) {
            focusEditTextRed3(getActivity(), formDropoffEt, true, null);
            isValid = false;
        }
        return isValid;
    }


    private void filter(String filter_type, String range) {
        SmartUtils.showLoadingDialog(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, order_id);
        params.put(FILTER_TYPE, filter_type);
        params.put("range", range);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "filterNearByTransporter");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    transporterDetailArray.clear();
                    try {
                        transporterDetailsJson = response.getJSONObject(DATA).getJSONArray("transporter_detail");
                        insertData("");
                        setAvailableTransporter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (responseCode == 400) {

                    transporterDetailArray.clear();
                    userHomeMap.getMapAsync(UserForm.this);
                    availableTransporterAdapter.notifyDataSetChanged();
                    noDataFound.setVisibility(View.VISIBLE);
                    noDataFoundTv.setText(getString(R.string.no_transporter_found));

                   /* try {
                        SmartUtils.showSnackBar(getActivity(), response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);

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
        SmartWebManager.getInstance(getActivity()).addToRequestQueueMultipart(requestParams, "", true);
    }

    private void requestTransporter() {
        SmartUtils.showLoadingDialog(getActivity());


        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, "no_user_id"));
        params.put(PICKUP_ADDRESS, formPickupEt.getText().toString());
        if (pickup_longitude != null || pickup_latitude != null) {
            params.put(PICKUP_LONGITUDE, String.valueOf(pickup_longitude));
            params.put(PICKUP_LATITUDE, String.valueOf(pickup_latitude));
        }
        params.put(DROPOFF_ADDRESS, formDropoffEt.getText().toString());
        if (pickup_longitude != null || pickup_latitude != null) {
            params.put(DROPOFF_LONGITUDE, String.valueOf(dropoff_longitude));
            params.put(DROPOFF_LATITUDE, String.valueOf(dropoff_latitude));
        }
        params.put(DATETIME, formTimeEt.getText().toString());
        params.put(HEIGHT, formHeightEt.getText().toString());
        params.put(WIDTH, formWidthEt.getText().toString());
        params.put(WEIGHT, formWeightEt.getText().toString());
        params.put(LENGTH, formLengthEt.getText().toString());
        params.put(SIZE, NO_DATA);
        params.put(DISTANCE, distance);
        params.put(DURATION, duration);
        params.put(DISTANCE_RANGE, "");
        params.put(PRICE, "");


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "userOrder");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    try {
                        order_id = response.getJSONObject(DATA).getString(ORDER_ID);
                        transporterDetailsJson = response.getJSONObject(DATA).getJSONArray("transporter_detail");
                        insertData("");
                        setAvailableTransporter();

                        filter2Iv.setImageResource(R.drawable.ic_round_unselected);
                        filter3Iv.setImageResource(R.drawable.ic_round_unselected);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(getActivity(), response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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
        SmartWebManager.getInstance(getActivity()).addToRequestQueueMultipart(requestParams, "", true);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        GoogleMap googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);

                JSONObject row = markerHashMap.get(marker);

                SmartTextView venueNameTxt = infoWindow.findViewById(R.id.venueName_txt);
                SmartTextView venueAddressTxt = infoWindow.findViewById(R.id.venueAddress_txt);
                SmartTextView venueDistanceTxt = infoWindow.findViewById(R.id.venueDistance_txt);
                ImageView imgVenue = infoWindow.findViewById(R.id.imgVenue);

                try {
                    venueNameTxt.setText(row.getString(FIRST_NAME));
                    if (!TextUtils.isEmpty(row.getString(PROFILE_IMAGE))) {
                        Picasso.with(getActivity()).load(row.getString(PROFILE_IMAGE)).into(imgVenue);
                    }
                    venueAddressTxt.setText(row.getString(ADDRESS));
                    venueDistanceTxt.setText(row.getString(TRANSPORTER_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return infoWindow;
            }
        });

        googleMap.clear();
        for (JSONObject transporterDetails : transporterDetailArray) {
            LatLng diwaniyaLatLong;
            try {
                diwaniyaLatLong = new LatLng(Double.parseDouble(transporterDetails.getString(LATITUDE)),
                        Double.parseDouble(transporterDetails.getString(LONGITUDE)));
                MarkerOptions markerOptions = new MarkerOptions().position(diwaniyaLatLong)
                        .title(transporterDetails.getString(FIRST_NAME))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markerHashMap.put(googleMap.addMarker(markerOptions), transporterDetails);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LatLng currentLatLong = new LatLng(Double.parseDouble(SmartUtils.getLatitude()),
                Double.parseDouble(SmartUtils.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12));

    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    private class AvailableTransporterAdapter extends RecyclerView.Adapter<AvailableTransporterAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_user_home, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), TransporterRequest.class);
                    i.putExtra(ORDER_ID, order_id);
                    try {
                        i.putExtra(TRANSPORTER_ID, transporterDetailArray.get(position).getString(USER_ID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(i);
                }
            });
            try {
                if (!TextUtils.isEmpty(transporterDetailArray.get(position).getString(PROFILE_IMAGE))) {
                    Picasso.with(getActivity()).load(transporterDetailArray.get(position).getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(holder.transporterImageIv);
                }
                holder.transporterNameTv.setText(transporterDetailArray.get(position).getString(FIRST_NAME) + " " + transporterDetailArray.get(position).getString(LAST_NAME));
                holder.transporterAddressTv.setText(transporterDetailArray.get(position).getString(ADDRESS));
                holder.transporterRatingRb.setRating(Float.parseFloat(transporterDetailArray.get(position).getString(RATING)));
                holder.transporterRatingTv.setText(transporterDetailArray.get(position).getString(RATING));
                holder.transporterDistanceIv.setText(decimalFormatter(transporterDetailArray.get(position).getString(DISTANCE)) + " " + Km);
                holder.transporterPriceTv.setText(€ + " " + transporterDetailArray.get(position).getString(RATE_PER_KM) + "/" + Km);
                holder.transporterTimeIv.setText(transporterDetailArray.get(position).getString(TIME) + " " + getString(R.string.min_away));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return transporterDetailArray.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private RoundedImageView transporterImageIv;
            private SmartTextView transporterNameTv;
            private SmartTextView transporterAddressTv;
            private RatingBar transporterRatingRb;
            private SmartTextView transporterRatingTv;
            private SmartTextView transporterDistanceIv;
            private SmartTextView transporterTimeIv;
            private SmartTextView transporterPriceTv;

            public ViewHolder(View itemView) {
                super(itemView);
                transporterImageIv = itemView.findViewById(R.id.transporter_image_iv);
                transporterNameTv = itemView.findViewById(R.id.transporter_name_tv);
                transporterAddressTv = itemView.findViewById(R.id.transporter_address_tv);
                transporterRatingRb = itemView.findViewById(R.id.transporter_rating_rb);
                transporterRatingTv = itemView.findViewById(R.id.transporter_rating_tv);
                transporterDistanceIv = itemView.findViewById(R.id.transporter_distance_iv);
                transporterTimeIv = itemView.findViewById(R.id.transporter_time_iv);
                transporterPriceTv = itemView.findViewById(R.id.transporter_price_km_tv);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<HashMap<String, String>> selected_Address;
            if (requestCode == ADDRESS_PICKUP_CODE) {
                selected_Address = (ArrayList<HashMap<String, String>>) data.getSerializableExtra(ADDRESS);
                HashMap<String, String> hashMapAddress = selected_Address.get(0);
                formPickupEt.setText(hashMapAddress.get(ADDRESS));
                pickup_latitude = Double.parseDouble(hashMapAddress.get(LATITUDE));
                pickup_longitude = Double.parseDouble(hashMapAddress.get(LONGITUDE));
            } else if (requestCode == ADDRESS_DROPOFF_CODE) {
                selected_Address = (ArrayList<HashMap<String, String>>) data.getSerializableExtra(ADDRESS);
                HashMap<String, String> hashMapAddress = selected_Address.get(0);
                formDropoffEt.setText(hashMapAddress.get(ADDRESS));
                dropoff_latitude = Double.parseDouble(hashMapAddress.get(LATITUDE));
                dropoff_longitude = Double.parseDouble(hashMapAddress.get(LONGITUDE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertData(String filter) {

        transporterDetailArray.clear();

        for (int i = 0; i < transporterDetailsJson.length(); i++) {
            if (TextUtils.isEmpty(filter)) {
                try {
                    transporterDetailArray.add(transporterDetailsJson.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String nameFromJsonArray = transporterDetailsJson.getJSONObject(i).getString(FIRST_NAME) + " " + transporterDetailsJson.getJSONObject(i).getString(LAST_NAME);
                    filter = filter.toLowerCase();
                    if (nameFromJsonArray.toLowerCase().contains(filter)) {
                        transporterDetailArray.add(transporterDetailsJson.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        availableTransporterRv.setAdapter(availableTransporterAdapter);
        userHomeMap.getMapAsync(UserForm.this);
        if (transporterDetailArray.size() > 0) {
            noDataFound.setVisibility(View.GONE);

        } else {
            noDataFound.setVisibility(View.VISIBLE);
            noDataFoundTv.setText(getString(R.string.no_transporter_found));
        }
    }

    private void getOrderDistance() {
        LatLng startPoint = new LatLng(pickup_latitude,
                pickup_longitude);
        LatLng endPoint = new LatLng(dropoff_latitude,
                dropoff_longitude);
        String url = getDirectionsUrl(startPoint, endPoint);

        try {
            new downloadUrl().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_server_key);
        Log.d("@@URL====", url);

        return url;
    }


    private class downloadUrl extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]);

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
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            Log.d("@@Map_Data", data);
            try {
                JSONObject mapData = new JSONObject(data);
                Log.d("@@map_distance", mapData.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text"));
                Log.d("@@map_distance", mapData.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text"));


                String data_distance = mapData.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                data_distance = data_distance.split(" ")[0];
                distance = decimalFormatter(data_distance);
                Log.d("@@DECIMAL====", distance);
                duration = mapData.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
                requestTransporter();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            requestTransporter();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.menu_search_refresh_userlist, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (transporterDetailsJson != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    callSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    insertData(newText);
                    return true;
                }

                public void callSearch(String query) {
                }
            });
        }
    }
}
