package com.hitchtransporter.transporter.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;

public class Complain extends MasterActivity {

    private SmartTextView orderComplainTv;
    private SmartEditText titleComplainEt;
    private SmartEditText descriptionComplainEt;


    @Override
    public void initComponents() {
        super.initComponents();
        orderComplainTv = findViewById(R.id.order_complain_tv);
        titleComplainEt = findViewById(R.id.title_complain_et);
        descriptionComplainEt = findViewById(R.id.description_complain_et);
        SmartButton sendComplainBtn = findViewById(R.id.send_complain_btn);
        sendComplainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    sendComplain();
                }
            }
        });
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_complain;
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setHeaderToolbar(getString(R.string.complain_form));
        orderComplainTv.setText(getIntent().getStringExtra(ORDER_ID));
    }

    private boolean check() {
        boolean isValid = true;


        if (TextUtils.isEmpty(descriptionComplainEt.getText().toString())) {
            SmartUtils.focusEditTextRed(Complain.this, descriptionComplainEt, true, getString(R.string.complain_description_error),0);
            isValid = false;
        }

        if (TextUtils.isEmpty(titleComplainEt.getText().toString())) {
            SmartUtils.focusEditTextRed(Complain.this, titleComplainEt, true, getString(R.string.complain_title_error),0);
            isValid = false;
        }
        return isValid;
    }


    private void sendComplain() {
        SmartUtils.showLoadingDialog(Complain.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, getIntent().getStringExtra(ORDER_ID));
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(TITLE, titleComplainEt.getText().toString());
        params.put(DESCRIPTION, descriptionComplainEt.getText().toString());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderComplain");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Complain.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Toast.makeText(Complain.this, getString(R.string.complaint_sent), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Complain.this, Home.class);
                    i.putExtra(SET_PAGE, ORDER_HISTORY);
                    startActivity(i);
                    supportFinishAfterTransition();
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(Complain.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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
}
