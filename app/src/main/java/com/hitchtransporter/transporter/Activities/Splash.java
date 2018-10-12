package com.hitchtransporter.transporter.Activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.CoreMaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.icu.util.ULocale.getLanguage;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static java.util.jar.Pack200.Unpacker.FALSE;
import static java.util.jar.Pack200.Unpacker.TRUE;

public class Splash extends CoreMaster  {

    MapFragment mMapFragment;
    private GoogleMap mMap;

    @Override
    public void prepareViews() {
    }


    @Override
    public void setActionListeners() {
    }


    @Override
    public int getLayoutID() {
        return R.layout.activity_splash;
    }


    @Override
    public void initComponents() {
        super.initComponents();


    /*    mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();*/


        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(NO_DATA)) {
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SELECTED_LOCALE, ENGLISH_STR);
        }
        String lang = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, ENGLISH_STR);
        SmartUtils.ForcefullyLocaleChange(Splash.this, lang);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(IS_LEGAL, FALSE).equalsIgnoreCase(TRUE)) {
                    auotLogin();
                } else {
                    Intent intent = new Intent(Splash.this, LegalInfo.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 1500);
    }


    private void auotLogin() {
        Map<String, String> params = new HashMap<>();

        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(DEVICE_TOKEN, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_FIREBASE_REGID, NO_DATA));
        params.put(DEVICE_TYPE, ANDROID);
        params.put(LONGITUDE, SmartUtils.getLongitude());
        params.put(LATITUDE, SmartUtils.getLatitude());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "autoLogin");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Splash.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    Log.d("@@@autoLogin_success", ": true");
                    try {
                        Intent intent;
                        if (response.getJSONObject(DATA).getString("is_login").equalsIgnoreCase("1")) {
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(USER_TYPE, response.getJSONObject(DATA).getString(USER_TYPE));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGGED_USER, response.getJSONObject(DATA).toString());
                            intent = new Intent(Splash.this, Home.class);
                        } else {
                            intent = new Intent(Splash.this, Login.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        supportFinishAfterTransition();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    supportFinishAfterTransition();
                }
            }

            @Override
            public void onResponseError() {
                Toast.makeText(Splash.this, getString(R.string.autologin_unable), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                supportFinishAfterTransition();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, true, false);
    }


}

