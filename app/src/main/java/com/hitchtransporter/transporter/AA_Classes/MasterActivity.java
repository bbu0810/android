package com.hitchtransporter.transporter.AA_Classes;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.Constants;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartSuperMaster;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Activities.BankAccount;
import com.hitchtransporter.transporter.Activities.ChatList;
import com.hitchtransporter.transporter.Activities.Home;
import com.hitchtransporter.transporter.Activities.LegalInfo;
import com.hitchtransporter.transporter.Activities.Login;
import com.hitchtransporter.transporter.Activities.Notification;
import com.hitchtransporter.transporter.Activities.PaymentList;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;

import org.jivesoftware.smack.SmackException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Pack200;

import static android.widget.Toast.LENGTH_LONG;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfTransporter;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfUser;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.hideLoadingDialog;
import static com.hitchtransporter.smart.framework.SmartUtils.showLoadingDialog;


public abstract class MasterActivity extends SmartSuperMaster implements LocationListener {


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 0x8;
    private final int PERMISSIONS_REQUEST_CODE = 501;
    protected GoogleApiClient mmGoogleApiClient;
    protected String currentLattitude;
    protected String currentLongitude;
    LocationManager locationManager;
    Location loc;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    private Location location;
    private LinearLayout navHeaderLl;
    private SmartTextView headerToolbarTv;
    private ImageView switchIv;
    private RoundedImageView userProfileNav;
    private SmartTextView homeNav;
    private SmartTextView orderHistoryNav;
    private SmartTextView userNameNav;
    private SmartTextView addressNav;
    private SmartTextView notificationNav;
    private SmartTextView paymentHistoryNav;
    private SmartTextView myChatsNav;
    private SmartTextView accountNav;
    private SmartTextView legalNav;
    private SmartTextView languageNav;
    private SmartTextView share;
    private SmartTextView logoutNav;
    private Dialog setLanguageDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(MasterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MasterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            return;
        }*/
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        assert locationManager != null;
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (mmGoogleApiClient == null) {
            mmGoogleApiClient = new GoogleApiClient.Builder(MasterActivity.this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                    ActivityCompat.checkSelfPermission(MasterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(MasterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                                return;
                            }
                            loc = LocationServices.FusedLocationApi.getLastLocation(mmGoogleApiClient);
                            if (loc != null) {
                                location = loc;
                                SmartUtils.setLatitude(String.valueOf(location.getLatitude()));
                                SmartUtils.setLongitude(String.valueOf(location.getLongitude()));
                                Log.e("@@@Location2", "" + location.getLatitude() + "-------" + location.getLongitude());
                                getLocation();

                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mmGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();

            mmGoogleApiClient.connect();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectGoggle();
    }

    public void connectGoggle() {
        if (mmGoogleApiClient != null) {
            mmGoogleApiClient.connect();
        }
    }

    @Override
    public void initComponents() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        headerToolbarTv = findViewById(R.id.header_toolbar_tv);
        switchIv = findViewById(R.id.switch_iv);

        if (getDrawerLayoutID() != 0) {
            navHeaderLl = findViewById(R.id.nav_header_ll);
            userProfileNav = findViewById(R.id.user_profile_nav);
            userNameNav = findViewById(R.id.user_name_nav);
            addressNav = findViewById(R.id.address_nav);
            homeNav = findViewById(R.id.home_nav);
            orderHistoryNav = findViewById(R.id.orderhistory_nav);
            notificationNav = findViewById(R.id.notification_nav);
            paymentHistoryNav = findViewById(R.id.payment_history_nav);
            myChatsNav = findViewById(R.id.my_chats_nav);
            accountNav = findViewById(R.id.account_nav);
            legalNav = findViewById(R.id.legal_nav);
            languageNav = findViewById(R.id.language_nav);
            share = findViewById(R.id.share_nav);
            logoutNav = findViewById(R.id.logout_nav);

        }

        initializeQuickBloxFramework(getString(R.string.quickblox_app_id), getString(R.string.quickblox_auth_id), getString(R.string.quickblox_auth_secret), getString(R.string.quickblox_account_key));

        if (!isChatLoggedIn() && SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, null) != null) {
            createSessionForChat();
        }

    }

    @Override
    public void prepareViews() {
        if (getDrawerLayoutID() != 0) {
            String user_url;
            JSONObject user = SmartUtils.getUser();
            try {

                if (!user.getString(PROFILE_IMAGE).equalsIgnoreCase("")) {
                    user_url = user.getString(PROFILE_IMAGE);
                } else {
                    user_url = "";
                }

                if (user != null) {
                    setNavDetails(user_url, user.getString(FIRST_NAME), user.getString(LAST_NAME), user.getString(ADDRESS));
                }

            } catch (Exception e) {
                Log.e("@@NO_USER_DATA===", e.toString());
            }
        }
    }


    @Override
    public void setActionListeners() {
        if (getDrawerLayoutID() != 0) {
            navHeaderLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MasterActivity.this, Home.class);
                    i.putExtra(SET_PAGE, PROFILE);
                    startActivity(i);
                    supportFinishAfterTransition();
                }
            });
            homeNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, Home.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            orderHistoryNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, Home.class);
                    intent.putExtra(SET_PAGE, ORDER_HISTORY);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });


            notificationNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, Notification.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            paymentHistoryNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, PaymentList.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            myChatsNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, ChatList.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            accountNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, BankAccount.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            legalNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MasterActivity.this, LegalInfo.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            });

            languageNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLanguage();
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String appSharingLink = "https://play.google.com/store/apps/details?id=com.hitchtransporter&hl=en"; //TODO set app link once uploaded on PlayStore
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, appSharingLink);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)));
                }
            });

            //"latitude":"23.025664499999998","longitude":"72.5086098"

            logoutNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChatLoggedIn()) {
                        try {
                            QBChatService.getInstance().logout();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                    doLogout();
                }
            });
        }
        if (switchIv != null) {
            switchIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchRoles();
                }
            });
        }
    }


    public void setNavDetails(String img_url, String first_name, String last_name, String address) {
        if (!TextUtils.isEmpty(img_url))
            Picasso.with(this).load(img_url).placeholder(R.drawable.profile_grey).into(userProfileNav);
        else {
            userProfileNav.setImageResource(R.drawable.profile_grey);
        }
        userNameNav.setText(first_name + " " + last_name);
        addressNav.setText(address);

    }


    public void setSwitch(boolean hasNavigation) {
        if (checkIfTransporter()) {
            if (hasNavigation) {
                paymentHistoryNav.setVisibility(View.VISIBLE);
                accountNav.setVisibility(View.VISIBLE);
            }
            switchIv.setImageResource(R.drawable.switch_hitcher);
        } else {
            if (hasNavigation) {
                paymentHistoryNav.setVisibility(View.GONE);
                accountNav.setVisibility(View.GONE);
            }
            switchIv.setImageResource(R.drawable.switch_user);
        }
    }


    public void hideSwitch() {
        switchIv.setVisibility(View.GONE);
    }


    private boolean isChatLoggedIn() {
        return QBChatService.getInstance().isLoggedIn();
    }


    public void setHeaderToolbar(String heading) {
        headerToolbarTv.setText(heading);
    }


    public void setHeaderMarginZero() {
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(headerToolbarTv.getLayoutParams());
        lp.setMargins(0, 0, 0, 0);
        lp.gravity = Gravity.CENTER;
        headerToolbarTv.setLayoutParams(lp);
    }


    public void switchRoles() {
        String alertMsg;

        if (checkIfTransporter()) {
            alertMsg = getString(R.string.switch_message_1);
        } else {
            alertMsg = getString(R.string.switch_message_2);
        }

        SmartUtils.getConfirmDialog(MasterActivity.this, getString(R.string.switch1), alertMsg,
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {

                    @Override
                    public void PositiveMethod(final DialogInterface dialog, int id) {

                        dialog.dismiss();
                        dialog.cancel();
                        SmartUtils.showLoadingDialog(MasterActivity.this);

                        Map<String, String> params = new HashMap<>();
                        params.put(LANGUAGE, getLanguageCode());
                        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
                        if (checkIfUser()) {
                            params.put(USER_TYPE, TRANSPORTER_CODE);
                        } else {
                            params.put(USER_TYPE, USER_CODE);
                        }
                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "roleChange");
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, MasterActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.GET_PACKS);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                SmartUtils.hideLoadingDialog();
                                if (responseCode == 200) {
                                    try {
                                        Log.e("@@SWITCH_INFO====", response.getJSONObject(DATA).toString());
                                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_TYPE, response.getJSONObject(DATA).getString(USER_TYPE));
                                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_ID, response.getJSONObject(DATA).getString(USER_ID));
                                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, response.getJSONObject(DATA).toString());

                                        Intent intent = new Intent(MasterActivity.this, Home.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        supportFinishAfterTransition();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (responseCode == 400) {
                                    try {
                                        SmartUtils.showSnackBar(MasterActivity.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
    }


    public void doLogout() {
        SmartUtils.getConfirmDialog(MasterActivity.this, getString(R.string.logout), getString(R.string.logout_message),
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {

                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        SmartUtils.showLoadingDialog(MasterActivity.this);

                        Map<String, String> params = new HashMap<>();
                        params.put(LANGUAGE, getLanguageCode());
                        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

                        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "logout");
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, MasterActivity.this);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.GET_PACKS);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
                        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                SmartUtils.hideLoadingDialog();
                                if (responseCode == 200) {
                                    try {
                                        SmartUtils.showSnackBar(MasterActivity.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
                                        // FacebookSdk.sdkInitialize(getApplicationContext());
                                        Log.d("@@USER_DATA", new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, NO_DATA)).toString());
                                        JSONObject usersData = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, NO_DATA));
                                        if (usersData.has(SOCIAL_TYPE)) {
                                            String socialType = usersData.getString(SOCIAL_TYPE);
                                            switch (socialType) {
                                                case FACEBOOK_CODE:
                                                    LoginManager.getInstance().logOut();
                                                    break;
                                                case GOOGLE_CODE:
                                                    Auth.GoogleSignInApi.signOut(mmGoogleApiClient).setResultCallback(
                                                            new ResultCallback<Status>() {
                                                                @Override
                                                                public void onResult(Status status) {
                                                                }
                                                            });
                                                    break;
                                                case TWITTER_CODE:
                                                    TwitterCore.getInstance().getSessionManager().clearActiveSession();
                                                    break;
                                            }

                                        }
                                        eraseAndExit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else if (responseCode == 400) {
                                    try {
                                        Toast.makeText(MasterActivity.this, response.getJSONObject(DATA).getString(MESSAGE), LENGTH_LONG).show();
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
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
    }


    private void eraseAndExit() {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_ID, null);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, null);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_TYPE, null);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(QUICK_BLOX_USER_ID, null);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(IS_LOGGED_IN, Pack200.Unpacker.FALSE);
        //SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, null);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_HITCHER_COUNTRY, null);

        Intent intent = new Intent(MasterActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        supportFinishAfterTransition();
    }


    private void setLanguage() {

        setLanguageDialog = new Dialog(this);
        setLanguageDialog.setContentView(R.layout.dialog_set_language);

        RadioGroup languageRg = setLanguageDialog.findViewById(R.id.language_rg);

        RadioButton englishRb = setLanguageDialog.findViewById(R.id.english_rb);
        RadioButton germanRb = setLanguageDialog.findViewById(R.id.german_rb);
        RadioButton fennishRb = setLanguageDialog.findViewById(R.id.fennih_rb);
        RadioButton swedishRb = setLanguageDialog.findViewById(R.id.swedish_rb);

        String selected_locale = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, ENGLISH_STR);

        switch (selected_locale) {
            case ENGLISH_STR:
                englishRb.setChecked(true);
                germanRb.setChecked(false);
                fennishRb.setChecked(false);
                swedishRb.setChecked(false);
                break;
            case GERMAN_STR:
                englishRb.setChecked(false);
                germanRb.setChecked(true);
                fennishRb.setChecked(false);
                swedishRb.setChecked(false);
                break;
            case FINNISH_STR:
                englishRb.setChecked(false);
                germanRb.setChecked(false);
                fennishRb.setChecked(true);
                swedishRb.setChecked(false);
                break;
            case SWEDISH_STR:
                englishRb.setChecked(false);
                germanRb.setChecked(false);
                fennishRb.setChecked(false);
                swedishRb.setChecked(true);
                break;
        }

        languageRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.english_rb) {
                    SmartUtils.ForcefullyLocaleChange(MasterActivity.this, ENGLISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, ENGLISH_STR);
                    restartActivity(true);
                    setLanguageDialog.dismiss();
                } else if (checkedId == R.id.german_rb) {
                    SmartUtils.ForcefullyLocaleChange(MasterActivity.this, GERMAN_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, GERMAN_STR);
                    restartActivity(true);
                    setLanguageDialog.dismiss();
                } else if (checkedId == R.id.fennih_rb) {
                    SmartUtils.ForcefullyLocaleChange(MasterActivity.this, FINNISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, FINNISH_STR);
                    restartActivity(true);
                    setLanguageDialog.dismiss();
                } else if (checkedId == R.id.swedish_rb) {
                    SmartUtils.ForcefullyLocaleChange(MasterActivity.this, SWEDISH_STR);
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, SWEDISH_STR);
                    restartActivity(true);
                    setLanguageDialog.dismiss();
                }
            }
        });
        setLanguageDialog.show();
    }


    protected void restartActivity(boolean isLogin) {
        if (isLogin) {
            languageChange();
        } else {
            Intent intent = getIntent();
            startActivity(intent);
            supportFinishAfterTransition();
        }

    }

    private void languageChange() {
        SmartUtils.showLoadingDialog(this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "languageChange");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Intent intent = getIntent();
                    startActivity(intent);
                    supportFinishAfterTransition();

                } else if (responseCode == 400) {
                    SmartUtils.showSnackBar(MasterActivity.this, getString(R.string.somthing_went_wrong), Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(this).addToRequestQueueMultipart(requestParams, "", true);
    }


//    protected synchronized void buildmGoogleApiClient() {
//        mmGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();
//    }


    @Override
    public void postOnCreate() {

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mmGoogleApiClient != null && mmGoogleApiClient.isConnected()) {
            mmGoogleApiClient.disconnect();
        }
    }

    //--LOCATION SETUP----------------------------------------------------------------------------------------------------------------------


    private void getLocation() {
        try {
            // enableLoc();

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
                        loc = LocationServices.FusedLocationApi.getLastLocation(mmGoogleApiClient);
                        //   loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            SmartUtils.setLatitude(String.valueOf(loc.getLatitude()));
                            SmartUtils.setLongitude(String.valueOf(loc.getLongitude()));
                            Log.d(TAG, "ERROR3 somewhere during location checkup");
                            this.location = loc;
                            Log.e("@@@Location", "" + location.getLatitude() + location.getLongitude());
                        } else {
                            Log.d(TAG, "ERROR4");
                            enableLoc();
                        }

//                            updateUI(loc);
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
                        if (loc != null) {
                            this.location = loc;
                            setCurrentLattitude(String.valueOf(location.getLatitude()));
                            Log.e("@@@Location2", "" + location.getLatitude() + "------" + location.getLongitude());
                        } else {
                            enableLoc();
                        }

//                            updateUI(loc);
                    }
                } else {
                    Log.d(TAG, "ERROR1");

                    if (loc != null) {
                        loc.setLatitude(0);
                        loc.setLongitude(0);
                    } else {
                        enableLoc();
                    }
//                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "ERROR2 Can't get location");

                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void enableLoc() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(false);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mmGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MasterActivity.this, 105);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            assert locationManager != null;
            isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            getLocation();
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("@@CHECK==", "check2");
        setCurrentLattitude(String.valueOf(location.getLatitude()));
        // setCurrentLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
    }

    public String getCurrentLattitude() {
        return currentLattitude;
    }

    public void setCurrentLattitude(String currentLattitude) {
        this.currentLattitude = currentLattitude;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        Log.i("GoogleLocationApi", "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:

                        Log.i("GoogleLocationApi", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * Starting the location updates
     */
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            getLocation();
        }

    }


    //--QUICKBLOX SETUP----------------------------------------------------------------------------------------------------------------------

    private void createSessionForChat() {
        showLoadingDialog(MasterActivity.this);
        String user = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(QUICK_BLOX_USER_ID, NO_DATA);

        //Load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUsersHolder.getInstance().putUsers(qbUsers);
                //  subscribeToPushNotifications(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_FIREBASE_REGID, NO_DATA));

            }

            @Override
            public void onError(QBResponseException e) {
                SmartUtils.hideLoadingDialog();
            }
        });

        final QBUser qbuser = new QBUser(user, QUICK_BLOX_PASSWORD);
        QBAuth.createSession(qbuser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                qbuser.setId(qbSession.getUserId());
                qbuser.setPassword(QUICK_BLOX_PASSWORD);

                //QBChatService.setDebugEnabled(true); // enable chat logging

                QBChatService.setDefaultPacketReplyTimeout(30000);

                QBChatService.getInstance().login(qbuser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        hideLoadingDialog();
                        //    Toast.makeText(MasterActivity.this, "Session Login Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        SmartUtils.hideLoadingDialog();
                        //     Toast.makeText(MasterActivity.this, "Session Login failed", Toast.LENGTH_SHORT).show();
                        if (e.getMessage() != null) {
                            Log.e("@@ERR_CHAT_SESSION", e.getMessage());
                        } else {
                            Log.e("@@ERR_CHAT_SESSION", "NULL ERROR FROM QUICKBLOX");
                        }
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                SmartUtils.hideLoadingDialog();
                //Toast.makeText(MasterActivity.this, "Session could not be created", Toast.LENGTH_SHORT).show();

                if (e.getMessage() != null) {
                    Log.e("@@ERR_CHAT_SESSION2", e.getMessage());
                } else {
                    Log.e("@@ERR_CHAT_SESSION2", "NULL ERROR FROM QUICKBLOX");
                }

            }
        });


    }


    private void initializeQuickBloxFramework(String app_id, String auth_id, String auth_secret, String account_key) {
        QBSettings.getInstance().init(getApplicationContext(), app_id, auth_id, auth_secret);
        QBSettings.getInstance().setAccountKey(account_key);
    }

    //--DEFAULT METHODS----------------------------------------------------------------------------------------------------------------------

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
    }

    @Override
    public int getDrawerLayoutID() {
        return R.layout.drawer;
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }
}
