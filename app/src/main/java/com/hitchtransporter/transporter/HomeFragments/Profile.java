package com.hitchtransporter.transporter.HomeFragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.Constants;
import com.hitchtransporter.smart.framework.SmartActivity;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartFragment;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Activities.Home;
import com.hitchtransporter.transporter.Activities.SelectAddress;
import com.hitchtransporter.transporter.Adapters.CarImagesAdapter;
import com.hitchtransporter.transporter.Interfaces.ProfileView;
import com.hitchtransporter.transporter.POJO.ObjectEditImage;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfTransporter;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.getUserType;


public class Profile extends SmartFragment {

    private static int ADDRESS_CODE = 1001;
    public boolean inUpdateState = false;
    String[] array;
    String[] buisnessTypearray;
    private Uri currentImageUri;
    private String cameraImageName;
    private String PROFILE_PIC = "profile_pic";
    private String CAR_PIC = "car_pic";
    private ArrayList<ObjectEditImage> carImgs = new ArrayList<>();
    private CarImagesAdapter carImagesAdapter;
    private RoundedImageView profileImageIv;
    private ImageView editProfileImageIv;
    private SmartTextView btnGoOnline;
    private SmartEditText firstNameEt;
    private SmartEditText lastNameEt;
    private SmartEditText mobileEt;
    private SmartEditText emailEt;
    private SmartEditText addressEt;
    private SmartEditText profileDetailsEt;
    private SmartEditText carNoEt;
    private SmartEditText priceEt;
    private SmartEditText weightEt;
    private SmartEditText lengthEt;
    private SmartEditText heightEt;
    private SmartEditText widthEt;
    private SmartEditText distanceRangeEt;
    private SmartEditText countryEt;
    private SmartTextView txtVat;
    private RelativeLayout rlBtype;
    private SmartEditText buisnessTypeEt;
    private LinearLayout priceLl;
    private LinearLayout weightLl;
    private LinearLayout lengthLl;
    private LinearLayout heightLl;
    private LinearLayout widthLl;
    private LinearLayout distanceRangeLl;
    private int selectedCountry = 0;
    private int selectedTranspotrType = 0;
    private RelativeLayout rlCountry;
    private String profilePicImagePath = "";
    private HashMap<String, String> imageFilePaths = new HashMap<>();
    private ArrayList<HashMap<String, String>> selected_Address = new ArrayList<>();
    private String longitude;
    private String latitude;
    private ArrayList<String> carImgsDeleted = new ArrayList<>();
    private RecyclerView carImagesProfileRv;
    private ImageView editIv;
    private ImageView doneIv;
    private JSONObject profileDetailJson;
    private String pic_type;
    private SmartActivity activity;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SmartActivity) context;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_profile_2;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {
        btnGoOnline = currentView.findViewById(R.id.btnGoOnline);
        profileImageIv = currentView.findViewById(R.id.image_profile_iv);
        editProfileImageIv = currentView.findViewById(R.id.edit_profile_image_iv);

        firstNameEt = currentView.findViewById(R.id.first_name_et);
        lastNameEt = currentView.findViewById(R.id.last_name_et);
        profileDetailsEt = currentView.findViewById(R.id.profile_details_et);
        mobileEt = currentView.findViewById(R.id.mobile_et);
        countryEt = currentView.findViewById(R.id.country_et);
        rlCountry = currentView.findViewById(R.id.rlCountry);
        buisnessTypeEt = currentView.findViewById(R.id.buisnesType_et);
        txtVat = currentView.findViewById(R.id.txtVat);
        rlBtype = currentView.findViewById(R.id.rlBType);
        emailEt = currentView.findViewById(R.id.email_et);
        addressEt = currentView.findViewById(R.id.address_et);
        carNoEt = currentView.findViewById(R.id.car_no_et);
        priceEt = currentView.findViewById(R.id.price_et);
        carImagesProfileRv = currentView.findViewById(R.id.car_images_profile_rv);
        editIv = currentView.findViewById(R.id.edit_iv);
        doneIv = currentView.findViewById(R.id.done_iv);

        weightEt = currentView.findViewById(R.id.weight_et);
        lengthEt = currentView.findViewById(R.id.length_et);
        heightEt = currentView.findViewById(R.id.height_et);
        widthEt = currentView.findViewById(R.id.width_et);
        distanceRangeEt = currentView.findViewById(R.id.distance_range_et);

        priceLl = currentView.findViewById(R.id.price_ll);
        lengthLl = currentView.findViewById(R.id.length_ll);
        weightLl = currentView.findViewById(R.id.weight_ll);
        heightLl = currentView.findViewById(R.id.height_ll);
        widthLl = currentView.findViewById(R.id.width_ll);
        distanceRangeLl = currentView.findViewById(R.id.distance_range_ll);

        carImagesAdapter = new CarImagesAdapter(getActivity(), carImgs, inUpdateState);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        carImagesProfileRv.setLayoutManager(linearLayoutManager);
        carImagesProfileRv.setAdapter(carImagesAdapter);

        buisnessTypearray = getContext().getResources().getStringArray(R.array.buisness_type_array);
        array = getContext().getResources().getStringArray(R.array.lan_type_array);
        if (checkIfTransporter()) {
            setEditTextVisibility(View.VISIBLE);
        } else {
            setEditTextVisibility(View.GONE);
        }

        getProfileData();
        enableDisableEditText(false);
    }

    @Override
    public void prepareViews(View currentView) {
        //Mandatory method cant remove
    }


    @Override
    public void setActionListeners(View currentView) {

        carImagesAdapter.setCarSelectListener(new ProfileView.CarImageImplementation() {
            @Override
            public void onCarImageSelector() {
                openCropActivity();
            }

            @Override
            public void onCarRemoveListener(int position) {
                removeCarImg(position);
            }
        });

        addressEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SelectAddress.class);
                if (latitude != null && longitude != null) {
                    i.putExtra(LATITUDE, latitude);
                    i.putExtra(LONGITUDE, longitude);
                }
                getActivity().startActivityForResult(i, ADDRESS_CODE);
            }
        });

        editIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstNameEt.requestFocus();
                firstNameEt.setSelection(firstNameEt.getText().length());
                inUpdateState = true;
                carImgs.add(0, new ObjectEditImage(0, WEB, "starter"));
                carImagesAdapter.setCarAdapterData(carImgs, true);
                enableDisableEditText(true);

            }
        });

        doneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check()) {
                    editProfileData(false);
                    doneIv.setOnClickListener(null);
                }
            }
        });


        countryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryEt.getText().toString().equalsIgnoreCase(array[0].toString())) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                    selectedCountry = 0;
                } else if (countryEt.getText().toString().equalsIgnoreCase(array[1].toString())) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[1].toString());
                    selectedCountry = 1;
                } else if (countryEt.getText().toString().equalsIgnoreCase(array[2].toString())) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[2].toString());
                    selectedCountry = 2;
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                    selectedCountry = 0;
                }
                new AlertDialog.Builder(activity)
                        .setSingleChoiceItems(array, selectedCountry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                selectedCountry = selectedPosition;
                                countryEt.setText(array[selectedPosition]);
                                if (countryEt.getText().toString().equalsIgnoreCase(array[0].toString())) {
                                    txtVat.setText(getResources().getString(R.string.vat) + "24%");
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                                } else if (countryEt.getText().toString().equalsIgnoreCase(array[1].toString())) {
                                    txtVat.setText(getResources().getString(R.string.vat) + "25%");
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[1].toString());
                                } else if (countryEt.getText().toString().equalsIgnoreCase(array[2].toString())) {
                                    txtVat.setText(getResources().getString(R.string.vat) + "19%");
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[2].toString());
                                } else {
                                    txtVat.setText(getResources().getString(R.string.vat) + "Vat tax");
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                                }
                            }
                        })
                        .show();
            }
        });


        buisnessTypeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buisnessTypeEt.getText().toString().equalsIgnoreCase(buisnessTypearray[0].toString())) {
                    selectedTranspotrType = 0;
                } else if (buisnessTypeEt.getText().toString().equalsIgnoreCase(buisnessTypearray[1].toString())) {
                    selectedTranspotrType = 1;
                } else {
                    selectedTranspotrType = 0;
                }
                new AlertDialog.Builder(activity)
                        .setSingleChoiceItems(buisnessTypearray, selectedTranspotrType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                selectedTranspotrType = selectedPosition;
                                buisnessTypeEt.setText(buisnessTypearray[selectedPosition]);
                                if (buisnessTypearray[selectedPosition].toString().equalsIgnoreCase(getString(R.string.buisness))) {
                                    txtVat.setVisibility(View.VISIBLE);
                                    if (countryEt.getText().toString().equalsIgnoreCase(array[0].toString())) {
                                        txtVat.setText(getResources().getString(R.string.vat) + "24%");
                                    } else if (countryEt.getText().toString().equalsIgnoreCase(array[1].toString())) {
                                        txtVat.setText(getResources().getString(R.string.vat) + "25%");
                                    } else if (countryEt.getText().toString().equalsIgnoreCase(array[2].toString())) {
                                        txtVat.setText(getResources().getString(R.string.vat) + "19%");
                                    } else {
                                        txtVat.setText(getResources().getString(R.string.vat) + "Vat tax");
                                    }
                                } else {
                                    txtVat.setVisibility(View.GONE);
                                }
                            }
                        })
                        .show();
            }
        });
        btnGoOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOnlineStatus();
            }
        });


        editProfileImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic_type = PROFILE_PIC;
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity());
            }
        });

    }

    private boolean check() {
        boolean isValid = true;


        if (checkIfTransporter()) {

            if (carImgs.size() <= 1) {
                isValid = false;
                SmartUtils.showSnackBar(getActivity(), getString(R.string.add_car), Snackbar.LENGTH_LONG);
            }
            if (TextUtils.isEmpty(distanceRangeEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), distanceRangeEt, true, null, R.drawable.ic_distance_range);
            }

            if (TextUtils.isEmpty(widthEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), widthEt, true, null, R.drawable.ic_height_green);
            }

            if (TextUtils.isEmpty(heightEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), heightEt, true, null, R.drawable.ic_height_green);
            }


            if (TextUtils.isEmpty(lengthEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), lengthEt, true, null, R.drawable.ic_length_green);
            }


            if (TextUtils.isEmpty(weightEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), weightEt, true, null, R.drawable.ic_weight_green);
            }


            if (TextUtils.isEmpty(priceEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), priceEt, true, null, R.drawable.ic_dollars);
            }

            if (TextUtils.isEmpty(carNoEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), carNoEt, true, null, R.drawable.ic_car);
            }


            if (TextUtils.isEmpty(profileDetailsEt.getText().toString())) {
                isValid = false;
                focusEditTextRed(getActivity(), profileDetailsEt, true, null, R.drawable.ic_profile_detail);
            }
        }

        if (TextUtils.isEmpty(emailEt.getText().toString())) {
            focusEditTextRed(getActivity(), emailEt, true, null, R.drawable.ic_email);
            isValid = false;
        } else if (!SmartUtils.emailValidator(emailEt.getText().toString())) {
            focusEditTextRed(getActivity(), emailEt, true, getString(R.string.validation_invalid_email), R.drawable.ic_email);
            isValid = false;
        }
        if (TextUtils.isEmpty(addressEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(getActivity(), addressEt, true, null, R.drawable.ic_location);
        }
        if (TextUtils.isEmpty(countryEt.getText().toString())) {
            isValid = false;
            SmartUtils.showSnackBar(getActivity(), getString(R.string.validation_enter_country), Snackbar.LENGTH_LONG);
//            focusEditTextRed(getActivity(), countryEt, true, null, R.drawable.ic_location);
        }
        if (TextUtils.isEmpty(buisnessTypeEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(getActivity(), buisnessTypeEt, true, null, R.drawable.ic_location);
        }
        if (TextUtils.isEmpty(mobileEt.getText().toString())) {
            focusEditTextRed(getActivity(), mobileEt, true, null, R.drawable.ic_phone);
            isValid = false;
        } else if (mobileEt.getText().toString().length() < 8) {
            focusEditTextRed(getActivity(), mobileEt, true, getString(R.string.validation_short_phone_no), R.drawable.ic_phone);
            isValid = false;
        }
        if (TextUtils.isEmpty(lastNameEt.getText().toString())) {
            isValid = false;
            focusEditTextRed(getActivity(), lastNameEt, true, null, R.drawable.ic_name);
        }
        if (TextUtils.isEmpty(firstNameEt.getText().toString())) {
            focusEditTextRed(getActivity(), firstNameEt, true, null, R.drawable.ic_name);
            isValid = false;
        }

        return isValid;
    }


    private void getProfileData() {
        SmartUtils.showLoadingDialog(getActivity());
        Map<String, String> params = new HashMap<>();

        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(LANGUAGE, getLanguageCode());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "myProfile");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    try {
                        Log.e("@@@PROFILE_DATA", response.getJSONObject(DATA).toString());
                        profileDetailJson = response.getJSONObject(DATA);
                        if (!TextUtils.isEmpty(profileDetailJson.getString(PROFILE_IMAGE))) {
                            Picasso.with(getActivity()).load(profileDetailJson.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(profileImageIv);
                        }
                        firstNameEt.setText(profileDetailJson.getString(FIRST_NAME));
                        lastNameEt.setText(profileDetailJson.getString(LAST_NAME));
                        mobileEt.setText(profileDetailJson.getString(MOBILE));
                        addressEt.setText(profileDetailJson.getString(ADDRESS));
                        if (profileDetailJson.getString(COUNTRY).equalsIgnoreCase("")) {
                            countryEt.setText(getString(R.string.finland));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                        } else {
                            if (profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Finland") || profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Finland") || profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Suomi")) {
                                countryEt.setText(array[0].toString());
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                            } else if (profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Sweden") || profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Sverige") || profileDetailJson.getString(COUNTRY).equalsIgnoreCase("Ruotsi")) {
                                countryEt.setText(array[1].toString());
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[1].toString());
                            } else {
                                countryEt.setText(array[2].toString());
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[2].toString());
                            }

                        }
                        if (profileDetailJson.getString(TRANSPORTER_TYPE).equalsIgnoreCase("business")) {
                            buisnessTypeEt.setText(getContext().getString(R.string.buisness));
                        } else {
                            buisnessTypeEt.setText(getContext().getString(R.string._private));
                        }

                        emailEt.setText(profileDetailJson.getString(EMAIL));
                        latitude = profileDetailJson.getString(LATITUDE);


                        if (profileDetailJson.optString(IS_ONLINE).equalsIgnoreCase("1")) {
                            btnGoOnline.setText(R.string.go_offline);
                        } else {
                            btnGoOnline.setText(R.string.go_online);
                        }
                        longitude = profileDetailJson.getString(LONGITUDE);
                        if (profileDetailJson.getString(TRANSPORTER_TYPE).equalsIgnoreCase("business")) {
                            selectedTranspotrType = 0;
                            txtVat.setVisibility(View.VISIBLE);
                            if (countryEt.getText().toString().equalsIgnoreCase(array[0].toString())) {
                                selectedCountry = 0;
                                txtVat.setText(getResources().getString(R.string.vat) + "24%");
                            } else if (countryEt.getText().toString().equalsIgnoreCase(array[1].toString())) {
                                selectedCountry = 1;
                                txtVat.setText(getResources().getString(R.string.vat) + "25%");
                            } else if (countryEt.getText().toString().equalsIgnoreCase(array[2].toString())) {
                                selectedCountry = 2;
                                txtVat.setText(getResources().getString(R.string.vat) + "19%");
                            } else {
                                selectedCountry = 0;
                                txtVat.setText(getResources().getString(R.string.vat) + "Vat tax");
                            }
                        } else {
                            if (countryEt.getText().toString().equalsIgnoreCase(array[0].toString())) {
                                selectedCountry = 0;
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                            } else if (countryEt.getText().toString().equalsIgnoreCase(array[1].toString())) {
                                selectedCountry = 1;
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[1].toString());
                            } else if (countryEt.getText().toString().equalsIgnoreCase(array[2].toString())) {
                                selectedCountry = 2;
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[2].toString());
                            } else {
                                selectedCountry = 0;
                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, array[0].toString());
                            }
                            selectedTranspotrType = 1;
                            txtVat.setVisibility(View.GONE);
                        }

                        if (checkIfTransporter()) {
                            carImgs.clear();
                            for (int i = 0; i < profileDetailJson.getJSONArray("pics_of_car").length(); i++) {
                                carImgs.add(new ObjectEditImage(1, WEB, profileDetailJson.getJSONArray("pics_of_car").get(i).toString()));
                            }
                            carImagesAdapter.setCarAdapterData(carImgs, false);

                            profileDetailsEt.setText(profileDetailJson.getString(PROFILE_DETAIL));
                            carNoEt.setText(profileDetailJson.getString(NUMBER_PLATE));

                            if (!profileDetailJson.getString(RATE_PER_KM).equalsIgnoreCase("0")) {
                                priceEt.setText(profileDetailJson.getString(RATE_PER_KM));
                            }
                            if (!profileDetailJson.getString(BOX_WEIGHT).equalsIgnoreCase("0")) {
                                weightEt.setText(profileDetailJson.getString(BOX_WEIGHT));
                            }
                            if (!profileDetailJson.getString(BOX_LENGTH).equalsIgnoreCase("0")) {
                                lengthEt.setText(profileDetailJson.getString(BOX_LENGTH));
                            }
                            if (!profileDetailJson.getString(BOX_HEIGHT).equalsIgnoreCase("0")) {
                                heightEt.setText(profileDetailJson.getString(BOX_HEIGHT));
                            }
                            if (!profileDetailJson.getString(BOX_WIDTH).equalsIgnoreCase("0")) {
                                widthEt.setText(profileDetailJson.getString(BOX_WIDTH));
                            }
                            if (!profileDetailJson.getString(USER_RANGE).equalsIgnoreCase("0")) {
                                distanceRangeEt.setText(profileDetailJson.getString(USER_RANGE));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (responseCode == 400) {
                    try {
                        Log.e("@@@PROFILE_DATA", "CODE==400");
                        SmartUtils.showSnackBar(getActivity(), response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
                Log.e("@@@PROFILE_DATA", "Super Error");

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getActivity()).addToRequestQueueMultipart(requestParams, "", false);
    }


    private void editProfileData(boolean isFromBtnGoOnline) {
        SmartUtils.showLoadingDialog(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());

        if (isFromBtnGoOnline) {
            if (btnGoOnline.getText().toString().equalsIgnoreCase("go online")) {
                params.put(Constants.IS_ONLINE, "1");
            } else {
                params.put(Constants.IS_ONLINE, "0");
            }
        } else {
            if (btnGoOnline.getText().toString().equalsIgnoreCase("go online")) {
                params.put(Constants.IS_ONLINE, "0");
            } else {
                params.put(Constants.IS_ONLINE, "1");
            }
        }
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(FIRST_NAME, firstNameEt.getText().toString());
        params.put(LAST_NAME, lastNameEt.getText().toString());
        params.put(MOBILE, mobileEt.getText().toString());
        params.put(ADDRESS, addressEt.getText().toString());
        params.put(USER_TYPE, getUserType());
        params.put(EMAIL, emailEt.getText().toString());
        if (selectedTranspotrType == 0) {
            params.put(Constants.HITCHER_TYPE, "business");
        } else {
            params.put(Constants.HITCHER_TYPE, "private");
        }
        if (selectedCountry == 0) {
            params.put(Constants.COUNTRY, "Finland");
        } else if (selectedCountry == 1) {
            params.put(Constants.COUNTRY, "Sweden");
        } else {
            params.put(Constants.COUNTRY, "Germany");
        }

        params.put(LATITUDE, latitude);
        params.put(LONGITUDE, longitude);

        imageFilePaths.put(PROFILE_IMAGE, profilePicImagePath);

        for (int i = 1; i < carImgs.size(); i++) {
            if (carImgs.get(i).getImageType().equalsIgnoreCase(LOCAL) && carImgs.get(i).getIsStarter() != 0) {
                imageFilePaths.put("pics_of_car_" + i, carImgs.get(i).getImageUrl());
            }
        }

        if (checkIfTransporter()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (String image : carImgsDeleted) {
                stringBuilder.append(image);
                stringBuilder.append(",");
            }
            if (stringBuilder.length() > 0) {
                params.put(REMOVED_IMAGES, stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
            } else {
                params.put(REMOVED_IMAGES, "");
            }
            params.put(PROFILE_DETAIL, profileDetailsEt.getText().toString());
            params.put(NUMBER_PLATE, carNoEt.getText().toString());
            params.put(RATE_PER_KM, priceEt.getText().toString());
            params.put(BOX_WEIGHT, weightEt.getText().toString());
            params.put(BOX_LENGTH, lengthEt.getText().toString());
            params.put(BOX_HEIGHT, heightEt.getText().toString());
            params.put(BOX_WIDTH, widthEt.getText().toString());
            params.put(USER_RANGE, distanceRangeEt.getText().toString());
        }

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "editProfile");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    SmartUtils.hideLoadingDialog();

                    Intent i = new Intent(getActivity(), Home.class);
                    i.putExtra(SET_PAGE, PROFILE);
                    getActivity().startActivity(i);
                    getActivity().supportFinishAfterTransition();

                } else if (responseCode == 400) {
                    try {
                        SmartUtils.hideLoadingDialog();
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
        SmartWebManager.getInstance(getActivity()).addToRequestQueueMultipart(requestParams, imageFilePaths, true);
    }


    private void changeOnlineStatus() {
        SmartUtils.showLoadingDialog(getActivity());
        Map<String, String> params = new HashMap<>();

        params.put(TRANSPORTER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_DATA));
        params.put(LANGUAGE, getLanguageCode());

        if (profileDetailJson.optString(IS_ONLINE).equalsIgnoreCase("1")) {
            params.put(IS_ONLINE, "0");
        } else {
            params.put(IS_ONLINE, "1");
        }

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "statusChange");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, getActivity());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (profileDetailJson.optString(IS_ONLINE).equalsIgnoreCase("1")) {
                    btnGoOnline.setText(getString(R.string.go_offline));
                } else {
                    btnGoOnline.setText(getString(R.string.go_online));
                }
                SmartUtils.hideLoadingDialog();
                try {
                    SmartUtils.showSnackBar(getActivity(), response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getProfileData();

            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getActivity()).addToRequestQueueMultipart(requestParams, imageFilePaths, true);
    }


    public void openCropActivity() {
        if (carImgs.size() < 4) {
            pic_type = CAR_PIC;
            CropImage.activity()
                    .setAspectRatio(2, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getActivity());
        } else {
            SmartUtils.showSnackBar(getActivity(), getString(R.string.max_3_image), Snackbar.LENGTH_LONG);
        }
    }


    public void removeCarImg(int position) {
        if (carImgs.get(position).getImageType().equalsIgnoreCase(WEB)) {
            String[] imgUrl = carImgs.get(position).getImageUrl().split("/");
            carImgsDeleted.add(imgUrl[imgUrl.length - 1]);
        }
        carImgs.remove(position);
        carImagesAdapter.setCarAdapterData(carImgs, true);
    }


    private void enableDisableEditText(boolean isEnabled) {
        if (isEnabled) {
            inUpdateState = true;
            doneIv.setVisibility(View.VISIBLE);
            editIv.setVisibility(View.GONE);
            editProfileImageIv.setVisibility(View.VISIBLE);

        } else {
            inUpdateState = false;
            doneIv.setVisibility(View.GONE);
            editIv.setVisibility(View.VISIBLE);
            editProfileImageIv.setVisibility(View.GONE);
        }
        firstNameEt.setEnabled(isEnabled);
        lastNameEt.setEnabled(isEnabled);
        emailEt.setEnabled(isEnabled);
        mobileEt.setEnabled(isEnabled);
        addressEt.setEnabled(isEnabled);
        countryEt.setEnabled(isEnabled);
        buisnessTypeEt.setEnabled(isEnabled);
        profileDetailsEt.setEnabled(isEnabled);
        carNoEt.setEnabled(isEnabled);
        priceEt.setEnabled(isEnabled);
        weightEt.setEnabled(isEnabled);
        lengthEt.setEnabled(isEnabled);
        heightEt.setEnabled(isEnabled);
        heightLl.setEnabled(isEnabled);
        weightLl.setEnabled(isEnabled);
        distanceRangeLl.setEnabled(isEnabled);
        distanceRangeEt.setEnabled(isEnabled);
        widthEt.setEnabled(isEnabled);

    }


    private void setEditTextVisibility(int editTextVisibility) {

        carNoEt.setVisibility(editTextVisibility);
        profileDetailsEt.setVisibility(editTextVisibility);
        carImagesProfileRv.setVisibility(editTextVisibility);
        priceLl.setVisibility(editTextVisibility);
        weightLl.setVisibility(editTextVisibility);
        lengthLl.setVisibility(editTextVisibility);
        heightLl.setVisibility(editTextVisibility);
        widthLl.setVisibility(editTextVisibility);
        distanceRangeLl.setVisibility(editTextVisibility);
        btnGoOnline.setVisibility(editTextVisibility);
        rlCountry.setVisibility(editTextVisibility);
        countryEt.setVisibility(editTextVisibility);
        rlBtype.setVisibility(editTextVisibility);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d("@requestCode", String.valueOf(requestCode));

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                if (pic_type.equalsIgnoreCase(PROFILE_PIC)) {
                    profilePicImagePath = resultUri.getPath();
                    Picasso.with(getActivity()).load(resultUri).into(profileImageIv);
                } else if (pic_type.equalsIgnoreCase(CAR_PIC)) {
                    carImgs.add(1, new ObjectEditImage(1, LOCAL, resultUri.getPath()));
                    carImagesAdapter.setCarAdapterData(carImgs, true);
                }
            } else if (requestCode == ADDRESS_CODE) {
                selected_Address = (ArrayList<HashMap<String, String>>) data.getSerializableExtra(ADDRESS);
                HashMap<String, String> hashMapAddress = selected_Address.get(0);
                addressEt.setText(hashMapAddress.get(ADDRESS));
                latitude = hashMapAddress.get(LATITUDE);
                longitude = hashMapAddress.get(LONGITUDE);
            }
        }
    }

    public void onBackPressed() {
        try {

            firstNameEt.setText(profileDetailJson.getString(FIRST_NAME));
            lastNameEt.setText(profileDetailJson.getString(LAST_NAME));
            mobileEt.setText(profileDetailJson.getString(MOBILE));
            addressEt.setText(profileDetailJson.getString(ADDRESS));
            emailEt.setText(profileDetailJson.getString(EMAIL));

            if (checkIfTransporter()) {
                carImgs.remove(0);
                inUpdateState = false;
                carImagesAdapter.setCarAdapterData(carImgs, false);
                priceEt.setText(profileDetailJson.getString(RATE_PER_KM));
                profileDetailsEt.setText(profileDetailJson.getString(PROFILE_DETAIL));
                carNoEt.setText(profileDetailJson.getString(NUMBER_PLATE));
                weightEt.setText(profileDetailJson.getString(BOX_WEIGHT));
                lengthEt.setText(profileDetailJson.getString(BOX_LENGTH));
                heightEt.setText(profileDetailJson.getString(BOX_HEIGHT));
                widthEt.setText(profileDetailJson.getString(BOX_WIDTH));
                distanceRangeEt.setText(profileDetailJson.getString(USER_RANGE));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        enableDisableEditText(false);
    }
}


