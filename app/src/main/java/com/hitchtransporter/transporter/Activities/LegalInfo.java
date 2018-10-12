package com.hitchtransporter.transporter.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static java.util.jar.Pack200.Unpacker.FALSE;
import static java.util.jar.Pack200.Unpacker.TRUE;

public class
LegalInfo extends MasterActivity {

    private SmartTextView titleTermsTv, descTermsTv;
    private CheckBox legalCb;
    private SmartButton legalBtn;
    private boolean isLegal = false;

    @Override
    public int getLayoutID() {
        return R.layout.activity_legal_info;
    }

    @Override
    public int getDrawerLayoutID() {
        if (!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(IS_LEGAL, FALSE).equalsIgnoreCase(FALSE)) {
            return super.getDrawerLayoutID();
        } else {
            return 0;
        }
    }


    @Override
    public void initComponents() {
        super.initComponents();
        setHeaderToolbar(getString(R.string.legal_info));

        if (!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(IS_LEGAL, FALSE).equalsIgnoreCase(FALSE)) {
            setSwitch(true);
        }else {

        }

        isLegal = !SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(IS_LEGAL, FALSE).equalsIgnoreCase(FALSE);

        titleTermsTv = findViewById(R.id.title_terms_tv);
        descTermsTv = findViewById(R.id.desc_terms_tv);


        legalCb = findViewById(R.id.legal_cb);
        legalBtn = findViewById(R.id.legal_btn);

        if (!isLegal) {
            hideSwitch();
            setHeaderMarginZero();
            legalCb.setVisibility(View.VISIBLE);
            legalBtn.setVisibility(View.VISIBLE);
        }

        legalBtn.setEnabled(false);

        getTerms();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
        legalCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    legalBtn.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        legalBtn.setBackgroundResource(R.drawable.bg_primary);
                    }
                } else {
                    legalBtn.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        legalBtn.setBackgroundResource(R.drawable.bg_grey);
                    }
                }
            }
        });
        legalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(IS_LEGAL, TRUE);
                startActivity(new Intent(LegalInfo.this, Login.class));
                supportFinishAfterTransition();
            }
        });
    }

    @Override
    public void onBackPressed() {
        SmartUtils.getConfirmDialog(this, getString(R.string.quit_app), getString(R.string.quit_message),
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        finish();
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {

                    }
                });
    }

    private void getTerms() {
        SmartUtils.showLoadingDialog(LegalInfo.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "termsandconditions ");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, LegalInfo.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                       // titleTermsTv.setText(response.getJSONObject(DATA).getString("termsandcondition_title"));
                        descTermsTv.setText(Html.fromHtml(response.getJSONObject(DATA).getString("termsandcondition_description")));
                        if (!isLegal) {
                            legalBtn.setVisibility(View.VISIBLE);
                            legalCb.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                  /*  noDataFound.setVisibility(View.VISIBLE);
                    noDataFoundTv.setText(getString(R.string.no_notifications_found));*/
                    legalBtn.setVisibility(View.GONE);
                    legalCb.setVisibility(View.GONE);

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
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle
            actionBarDrawerToggle) {
        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(IS_LEGAL, FALSE).equalsIgnoreCase(TRUE)) {
            super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
            toolbar.setNavigationIcon(R.drawable.menu);
        }
    }
}
