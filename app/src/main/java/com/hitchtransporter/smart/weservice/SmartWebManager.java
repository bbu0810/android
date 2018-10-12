package com.hitchtransporter.smart.weservice;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hitchtransporter.smart.framework.Constants;
import com.hitchtransporter.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SmartWebManager implements Constants {

    private static final int TIMEOUT = 100000;

    public enum REQUEST_METHOD_PARAMS {CONTEXT, URL, PARAMS, TAG, RESPONSE_LISTENER}

    private static SmartWebManager mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    private SmartWebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SmartWebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmartWebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final boolean isShowSnackbar, final boolean isShowKeyBoard) {
        Log.v("@@@@@WSRequest", ((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)));
        final Context mContext = (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.v("@@@@@WSResponse", s);
                            JSONObject response = new JSONObject(s).getJSONObject("response");

                            if (SmartUtils.getResponseCode(response) == STATUS_SUCCESS) {
                                String errorMessage = SmartUtils.validateResponse(response, null);
                                if (!isShowKeyBoard){
                                    SmartUtils.hideSoftKeyboard(mContext);
                                }
                                if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                   // SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                                }
                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                            } else if (SmartUtils.getResponseCode(response) == STATUS_FAIL) {
                                String errorMessage = SmartUtils.validateResponse2(response, null);

                                    SmartUtils.hideSoftKeyboard(mContext);


                                if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                    //SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                                }
                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 400);
                            } else {
                                int responseCode = SmartUtils.getResponseCode(response);
                                String errorMessage = SmartUtils.validateResponse(response, null);
                                SmartUtils.hideSoftKeyboard(mContext);
                                if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                                    SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                                }
                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Log.v("@@@@@WSResponse", volleyError.toString());

                        SmartUtils.hideSoftKeyboard(mContext);
                        String errorMessage = VolleyErrorHelper.getMessage(volleyError, mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                             SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = (Map<String, String>) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS);
                Log.v("@@@@@wsparams", params.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(stringRequest);
    }


    public <T> void addToRequestQueueMultipart(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, String filePath, final boolean isShowSnackbar) {
        Log.v("@@@@@WSRequest", ((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)));
        final Context mContext = (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT);

        MultiPartRequest smMultiPartRequest = new MultiPartRequest(((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)),
                (Map<String, String>) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS), filePath, new Response.Listener() {

            @Override
            public void onResponse(Object object) {
                try {
                    JSONObject webData = (JSONObject) object;
                    JSONObject response = webData.getJSONObject("response");

                    Log.v("@@@@@WSResponse", response.toString());
                    if (SmartUtils.getResponseCode(response) == STATUS_SUCCESS) {
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            // SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                    } else if (SmartUtils.getResponseCode(response) == STATUS_FAIL) {
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            //SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 400);
                    } else {
                        int responseCode = SmartUtils.getResponseCode(response);
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("@@@@@WSError", error.toString());

                SmartUtils.hideSoftKeyboard(mContext);
                String errorMessage = VolleyErrorHelper.getMessage(error, mContext);
                SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
            }
        });
        smMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smMultiPartRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(smMultiPartRequest);
    }


    public <T> void addToRequestQueueMultipart(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, HashMap<String, String> filePath, final boolean isShowSnackbar) {
        Log.v("@@@@@WSRequest", ((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)));
        final Context mContext = (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT);
        MultiPartRequest smMultiPartRequest = new MultiPartRequest(((String) requestParams.get(REQUEST_METHOD_PARAMS.URL)),
                (Map<String, String>) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS), filePath, new Response.Listener() {
            @Override
            public void onResponse(Object object) {
                try {
                    JSONObject webData = (JSONObject) object;
                    JSONObject response = webData.getJSONObject("response");

                    Log.v("@@@@@WSResponse", response.toString());
                    if (SmartUtils.getResponseCode(response) == STATUS_SUCCESS) {
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            //     SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                    } else if (SmartUtils.getResponseCode(response) == STATUS_FAIL) {
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            //SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 400);
                    } else {
                        int responseCode = SmartUtils.getResponseCode(response);
                        String errorMessage = SmartUtils.validateResponse2(response, null);
                        SmartUtils.hideSoftKeyboard(mContext);
                        if (isShowSnackbar && !TextUtils.isEmpty(errorMessage)) {
                            SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                        }
                        ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage()!=null) {
                    Log.v("@@@@@WSError", error.getMessage());
                }
                else {
                    Log.v("@@@@@WSError", error.toString());

                }
                SmartUtils.hideSoftKeyboard(mContext);
                String errorMessage = VolleyErrorHelper.getMessage(error, mContext);
                SmartUtils.showSnackBar(mContext, errorMessage, Snackbar.LENGTH_LONG);
                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
            }
        });
        smMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        smMultiPartRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(smMultiPartRequest);
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(JSONObject tableRows, boolean isValidResponse, int responseCode) throws JSONException;

        void onResponseError();
    }
}