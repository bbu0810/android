package com.hitchtransporter.transporter.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;

public class ReviewList extends MasterActivity {

    private SmartTextView avgRatingTv;
    private RatingBar avgRatingRb;
    private SmartTextView totalRatingTv;
    private RecyclerView reviewRv;
    private ReviewAdapter reviewAdapter;
    private JSONArray reviewListJson;
    private View noDataFound;
    private SmartTextView noDataFoundTv;


    @Override
    public int getLayoutID() {
        return R.layout.activity_review_list;
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setHeaderToolbar(getString(R.string.hitcher_reviews));
        setSwitch(false);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        noDataFound = findViewById(R.id.layout_no_data);
        noDataFoundTv = findViewById(R.id.no_data_found_tv);

        avgRatingTv = findViewById(R.id.avg_rating_tv);
        avgRatingRb = findViewById(R.id.avg_rating_rb);
        totalRatingTv = findViewById(R.id.total_rating_tv);
        reviewRv = findViewById(R.id.review_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReviewList.this);
        reviewRv.setLayoutManager(linearLayoutManager);

        getReviews();
    }

    private void getReviews() {
        SmartUtils.showLoadingDialog(ReviewList.this);
        Map<String, String> params = new HashMap<>();
        if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SELECTED_LOCALE, NO_DATA).equalsIgnoreCase(GERMAN_STR)) {
            params.put(LANGUAGE, GERMAN_CODE);
        } else {
            params.put(LANGUAGE, ENGLISH_CODE);
        }

        params.put(TRANSPORTER_ID, getIntent().getStringExtra(TRANSPORTER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "getRatingDetail");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, ReviewList.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                SmartUtils.hideLoadingDialog();
                if (responseCode == 200) {
                    Log.d("@@@Login_success", ": true");
                    try {
                        noDataFound.setVisibility(View.GONE);
                        reviewListJson = response.getJSONObject(DATA).getJSONArray(RATING_DETAIL);
                        avgRatingTv.setText(String.valueOf(Float.parseFloat(response.getJSONObject(DATA).getJSONObject(AVERAGE_DETAIL).getString(AVG_RATING))));
                        avgRatingRb.setRating(Float.parseFloat(response.getJSONObject(DATA).getJSONObject(AVERAGE_DETAIL).getString(AVG_RATING)));
                        totalRatingTv.setText(response.getJSONObject(DATA).getJSONObject(AVERAGE_DETAIL).getString(USER_REVIEW));
                        reviewAdapter = new ReviewAdapter();
                        reviewRv.setAdapter(reviewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 400) {
                    noDataFound.setVisibility(View.VISIBLE);
                    noDataFoundTv.setText(R.string.no_reviews_found);
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideLoadingDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);
    }

    private class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_review_list, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                if (!TextUtils.isEmpty(reviewListJson.getJSONObject(position).getString(PROFILE_IMAGE))) {
                    Picasso.with(ReviewList.this).load(reviewListJson.getJSONObject(position).getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(holder.imageReviewIv);
                }
                holder.nameReviewIv.setText(reviewListJson.getJSONObject(position).getString(USER_NAME));
                holder.dateReviewTv.setText(dateFormatChanger(reviewListJson.getJSONObject(position).getString(RATING_INSERTED_DATE).split(" ")[0]));
                holder.ratingReviewTv.setText(String.valueOf(Double.parseDouble(reviewListJson.getJSONObject(position).getString(RATING))));
                holder.ratingReviewRb.setRating(Float.parseFloat(reviewListJson.getJSONObject(position).getString(RATING)));

                holder.descriptionReviewTv.setText(reviewListJson.getJSONObject(position).getString(DESCRIPTION).trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return reviewListJson.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private RoundedImageView imageReviewIv;
            private SmartTextView nameReviewIv;
            private SmartTextView dateReviewTv;
            private RatingBar ratingReviewRb;
            private SmartTextView ratingReviewTv;
            private SmartTextView descriptionReviewTv;


            public ViewHolder(View itemView) {
                super(itemView);
                imageReviewIv = itemView.findViewById(R.id.image_review_iv);
                nameReviewIv = itemView.findViewById(R.id.name_review_iv);
                dateReviewTv = itemView.findViewById(R.id.date_review_tv);
                ratingReviewRb = itemView.findViewById(R.id.rating_review_rb);
                ratingReviewTv = itemView.findViewById(R.id.rating_review_tv);
                descriptionReviewTv = itemView.findViewById(R.id.description_review_tv);
            }
        }
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
