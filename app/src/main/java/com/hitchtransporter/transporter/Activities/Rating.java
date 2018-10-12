package com.hitchtransporter.transporter.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
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

public class Rating extends MasterActivity {

    private SmartTextView nameRatingTv;
    private SmartTextView orderRatingTv;
    private SmartEditText descriptionRatingEt;
    private RatingBar ratebarRatingRb;
    private SmartButton submitRatingBtn;


    @Override
    public int getLayoutID() {
        return R.layout.activity_rating;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setHeaderToolbar(getString(R.string.review_order));
        nameRatingTv.setText(getIntent().getStringExtra(TRANSPORTER_NAME));
        orderRatingTv.setText(getIntent().getStringExtra(ORDER_ID));

    }

    @Override
    public void initComponents() {
        super.initComponents();
        nameRatingTv = findViewById(R.id.name_rating_tv);
        orderRatingTv = findViewById(R.id.order_rating_tv);
        descriptionRatingEt = findViewById(R.id.description_rating_et);
        ratebarRatingRb = findViewById(R.id.ratebar_rating_rb);
        submitRatingBtn = findViewById(R.id.submit_rating_btn);

        submitRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    sendRating();
                }
            }
        });

    }

    private boolean check() {
        boolean isValid = true;

        if (ratebarRatingRb.getRating() == 0) {
            isValid = false;
            SmartUtils.showSnackBar(Rating.this, getString(R.string.rate_the_order), Snackbar.LENGTH_LONG);
        }

        return isValid;
    }

    private void sendRating() {
        SmartUtils.showLoadingDialog(Rating.this);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(ORDER_ID, getIntent().getStringExtra(ORDER_ID));
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(TRANSPORTER_ID, getIntent().getStringExtra(TRANSPORTER_ID));
        params.put(RATING, String.valueOf(ratebarRatingRb.getRating()));
        params.put(DESCRIPTION, descriptionRatingEt.getText().toString());

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "orderRating");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Rating.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Toast.makeText(Rating.this, getString(R.string.thankyou_rating), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Rating.this, Home.class);
                    i.putExtra(SET_PAGE, ORDER_HISTORY);
                    startActivity(i);
                    supportFinishAfterTransition();
                } else if (responseCode == 400) {
                    try {
                        SmartUtils.showSnackBar(Rating.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);
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
