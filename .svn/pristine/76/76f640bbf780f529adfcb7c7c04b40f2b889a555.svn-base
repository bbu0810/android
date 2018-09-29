package com.hitchtransporter.transporter.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hitchtransporter.smart.framework.Constants;
import com.hitchtransporter.smart.framework.SmartApplication;



/**
 * Created by tasol on 29/6/16.
 */
public class InstanceIDService extends FirebaseInstanceIdService implements Constants {

    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "@@Refreshed token: " + refreshedToken);

        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FIREBASE_REGID, refreshedToken);
    }
}
