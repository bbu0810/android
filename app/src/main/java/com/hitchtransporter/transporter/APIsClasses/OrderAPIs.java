package com.hitchtransporter.transporter.APIsClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.hitchtransporter.transporter.Interfaces.TransporterHomeImple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.Constants.*;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.getLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.getLongitude;

public class OrderAPIs {


    public static void getOrderList(Activity context, boolean isRepeating, final TransporterHomeImple.GetOrderListImplementation getOrderListImplementation) {

        if (!isRepeating) {
            SmartUtils.showLoadingDialog(context);
        }

        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(CURRENT_LATITUDE, getLatitude());
        params.put(CURRENT_LONGITUDE, getLongitude());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL,context.getResources().getString(R.string.domain_name_local) + "getTransporterOrderList");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.e("@@HOME_LIST", "TESTING");
                    getOrderListImplementation.getOrderListSuccess(response);
                } else if (responseCode == 400) {
                    Log.e("@@HOME_LIST", "ERROR2 400 no data in orderlist");
                    getOrderListImplementation.getOrderListFail("");
                }
            }

            @Override
            public void onResponseError() {
                Log.e("@@HOME_LIST", "ERROR");

                SmartUtils.hideLoadingDialog();
            }
        });

        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", true);
    }


    public static void onStartJob(final Context context, final String orderId, final String orderRequestId, final String currentLatitude, final String currentLongitude, final String dropOffLatitude, final String dropOffLongitude, final OrderHistoryImple.OrderStartImplementation orderStartImplementation) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put(ORDER_ID, orderId);
        params.put(LANGUAGE, getLanguageCode());
        params.put(LATITUDE, String.valueOf(currentLatitude));
        params.put(LONGITUDE, String.valueOf(currentLongitude));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "orderDeliveryStart");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@orderhistory_success", response.toString());
                    try {
                        orderStartImplementation.onOrderStartSuccess(orderId, orderRequestId, response.getJSONObject(DATA).getString(MESSAGE), dropOffLatitude, dropOffLongitude);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        orderStartImplementation.onOrderStartFail(response.getJSONObject(DATA).getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
                orderStartImplementation.onOrderStartError();
            }
        });
        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", true);
    }


    public static void onFinishJob(final Context context, final String orderId, final OrderHistoryImple.OrderFinishImplementation orderFinishImplementation) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, orderId);
        params.put(LATITUDE, String.valueOf(getLatitude()));
        params.put(LONGITUDE, String.valueOf(getLongitude()));


        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "orderDeliveryComplete");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                        orderFinishImplementation.onOrderFinishSuccess(response.getJSONObject(DATA).getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                  /*  try {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else if (responseCode == 400) {
                    try {
                        orderFinishImplementation.onOrderFinishFail(response.getJSONObject(DATA).getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                  /*  try {
                        Toast.makeText(context, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
                orderFinishImplementation.onOrderFinishError();
            }
        });
        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", true);
    }


    public static void onCancelJob(final Context context, final String orderId, final OrderHistoryImple.OrderCancelImplementation orderCancelImplementation) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();

        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, orderId);
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "orderCancel");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                        orderCancelImplementation.onOrderCancelSuccess(response.getJSONObject(DATA).getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    try {
                        orderCancelImplementation.onOrderCancelFail(response.getJSONObject(DATA).getString(MESSAGE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
                SmartUtils.hideLoadingDialog();
                orderCancelImplementation.onOrderCancelError();

            }
        });
        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", true);
    }

}
