package com.hitchtransporter.transporter.APIsClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Activities.PaymentList;
import com.hitchtransporter.transporter.Interfaces.PaymentHistoryImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hitchtransporter.smart.framework.Constants.DATA;
import static com.hitchtransporter.smart.framework.Constants.EMAIL;
import static com.hitchtransporter.smart.framework.Constants.ENGLISH_CODE;
import static com.hitchtransporter.smart.framework.Constants.ENGLISH_STR;
import static com.hitchtransporter.smart.framework.Constants.FROM_DATE;
import static com.hitchtransporter.smart.framework.Constants.GERMAN_CODE;
import static com.hitchtransporter.smart.framework.Constants.GERMAN_STR;
import static com.hitchtransporter.smart.framework.Constants.LANGUAGE;
import static com.hitchtransporter.smart.framework.Constants.NO_DATA;
import static com.hitchtransporter.smart.framework.Constants.NO_USER_ID;
import static com.hitchtransporter.smart.framework.Constants.SELECTED_LOCALE;
import static com.hitchtransporter.smart.framework.Constants.TO_DATE;
import static com.hitchtransporter.smart.framework.Constants.USER_ID;
import static com.hitchtransporter.smart.framework.Constants.€;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;

public class PaymentHistoryAPIs {

    public static void getPaymentList(Context context, final PaymentHistoryImpl.getPaymentHistoryImpl getPaymentHistory) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "getPaymentList");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    getPaymentHistory.getPaymentHistorySuccess(response);
                } else if (responseCode == 400) {
                    getPaymentHistory.getPaymentHistoryFailed();

                }
            }

            @Override
            public void onResponseError() {
                getPaymentHistory.getPaymentHistoryError();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);
    }

    public static void emailPaymentDetails(final Context context, String fromDate, String toDate, String email, final PaymentHistoryImpl.emailPaymentHistoryImpl emailPaymentHistory) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();
        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(ENGLISH_STR)) {
            params.put(LANGUAGE, ENGLISH_CODE);
            Log.d("@@@LANGUAGE", "English");
        } else if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(GERMAN_STR)) {
            params.put(LANGUAGE, GERMAN_CODE);
            Log.d("@@@LANGUAGE", "German");
        } else {
            Log.d("@@@LANGUAGE", "Nothing");
        }
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(FROM_DATE, fromDate);
        params.put(TO_DATE, toDate);
        params.put(EMAIL, email);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "OrdersMail");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {

                    emailPaymentHistory.onEmailSent();
                }
                if (responseCode == 400) {

                    emailPaymentHistory.onEmailFailed();
                }
            }

            @Override
            public void onResponseError() {

                emailPaymentHistory.onEmailError();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);
    }


}
