package com.hitchtransporter.transporter.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.transporter.APIsClasses.PaymentHistoryAPIs;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.CustomClickListener;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartActivity;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.hitchtransporter.transporter.Adapters.PaymentListAdapter;
import com.hitchtransporter.transporter.Interfaces.PaymentHistoryImpl;
import com.hitchtransporter.transporter.POJO.ObjectOrderHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.emailValidator;
import static com.hitchtransporter.smart.framework.SmartUtils.extractDate;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed4;
import static com.hitchtransporter.smart.framework.SmartUtils.getCurrentDate;
import static com.hitchtransporter.smart.framework.SmartUtils.getDateDialog;

public class PaymentList extends MasterActivity implements PaymentHistoryImpl.getPaymentHistoryImpl,
        PaymentHistoryImpl.emailPaymentHistoryImpl {

    private RecyclerView paymentListRv;
    private JSONArray paymentListJson;
    private ArrayList<ObjectOrderHistory> payListDataArray = new ArrayList<>();
    private View noDataFound;
    private SmartTextView noDataFoundTv;
    private SmartTextView monthPaymentTv;
    private SmartTextView emailPaymentTv;
    private Dialog dialogImageSource;


    @Override
    public int getLayoutID() {
        return R.layout.activity_payment_list;
    }

    @Override
    public void prepareViews() {
        super.prepareViews();
        setHeaderToolbar(getString(R.string.payment_history));
        setSwitch(false);
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

    @Override
    public void initComponents() {
        super.initComponents();


        paymentListRv = findViewById(R.id.payment_list_rv);
        noDataFound = findViewById(R.id.layout_no_data);
        noDataFoundTv = findViewById(R.id.no_data_found_tv);
        monthPaymentTv = findViewById(R.id.month_payment_tv);
        emailPaymentTv = findViewById(R.id.email_payment_tv);
        dialogImageSource = new Dialog(PaymentList.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PaymentList.this);
        paymentListRv.setLayoutManager(linearLayoutManager);
        PaymentHistoryAPIs.getPaymentList(PaymentList.this, this);
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
        emailPaymentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog(v.getContext());
            }
        });
    }


    private void showEmailDialog(Context context) {
//        dialogImageSource.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImageSource.setContentView(R.layout.dialog_email_payment);

        final SmartActivity activity = (SmartActivity) context;

        ImageView closePaymentIv = dialogImageSource.findViewById(R.id.close_payment_iv);
        final SmartEditText fromDatePaymentEt = dialogImageSource.findViewById(R.id.from_date_payment_et);
        final SmartEditText toDatePaymentEt = dialogImageSource.findViewById(R.id.to_date_payment_et);
        final SmartEditText emailPaymentEt = dialogImageSource.findViewById(R.id.email_payment_et);
        SmartTextView sendPaymentTv = dialogImageSource.findViewById(R.id.send_payment_tv);


        fromDatePaymentEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateDialog(PaymentList.this, getCurrentDate(), new CustomClickListener() {
                    @Override
                    public void onClick(String value) {
                        fromDatePaymentEt.setText(value);
                    }
                }, getString(R.string.date_format), false);

            }
        });


        toDatePaymentEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateDialog(PaymentList.this, getCurrentDate(), new CustomClickListener() {
                    @Override
                    public void onClick(String value) {
                        toDatePaymentEt.setText(value);
                    }
                }, getString(R.string.date_format), false);


            }
        });

        sendPaymentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        if (TextUtils.isEmpty(fromDatePaymentEt.getText().toString())) {
            focusEditTextRed(PaymentList.this, fromDatePaymentEt, false, null, 0);
            Toast.makeText(PaymentList.this,getResources().getString(R.string.please_select_from_dat), Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(toDatePaymentEt.getText().toString())) {
            focusEditTextRed(PaymentList.this, toDatePaymentEt, false, null, 0);
            Toast.makeText(PaymentList.this, getResources().getString(R.string.please_select_to_dat), Toast.LENGTH_LONG).show();
        } else if (!checkDates(fromDatePaymentEt.getText().toString(), toDatePaymentEt.getText().toString())) {
            focusEditTextRed4(PaymentList.this, false, null, 0, fromDatePaymentEt, toDatePaymentEt);
            Toast.makeText(PaymentList.this,getString(R.string.todate_cannotconvert_into_fromdate), Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(emailPaymentEt.getText().toString())) {
            focusEditTextRed(PaymentList.this, emailPaymentEt, false, null, 0);
            Toast.makeText(PaymentList.this, getResources().getString(R.string.please_enter_an_email_idd), Toast.LENGTH_LONG).show();
        } else if (!emailValidator(emailPaymentEt.getText().toString())) {
            focusEditTextRed(PaymentList.this, emailPaymentEt, false, null, 0);
            Toast.makeText(PaymentList.this, getResources().getString(R.string.please_enter_a_valid_email_id), Toast.LENGTH_LONG).show();
        } else {

            PaymentHistoryAPIs.emailPaymentDetails(PaymentList.this, fromDatePaymentEt.getText().toString(),
                    toDatePaymentEt.getText().toString(),
                    emailPaymentEt.getText().toString(), PaymentList.this);

        }
    }
});

        closePaymentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImageSource.dismiss();
            }
        });

        dialogImageSource.show();
    }


    private void insertData(JSONArray dataArray, String query) {
        String date1 = "0";
        payListDataArray = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            try {
                if (query == null) {
                    if (!extractDate(dataArray.getJSONObject(i).getString(PAYMENT_DATE)).equalsIgnoreCase(date1)) {
                        date1 = extractDate(dataArray.getJSONObject(i).getString(PAYMENT_DATE));
                        payListDataArray.add(new ObjectOrderHistory(true, false, date1));
                    }
                    payListDataArray.add(new ObjectOrderHistory(false, false, dataArray.getJSONObject(i).toString()));
                } else {
                    String nameFromArray = dataArray.getJSONObject(i).getString(USER_NAME);
                    if (nameFromArray.toLowerCase().contains(query)) {
                        query = query.toLowerCase();
                        if (!extractDate(dataArray.getJSONObject(i).getString(PAYMENT_DATE)).equalsIgnoreCase(date1)) {
                            date1 = extractDate(dataArray.getJSONObject(i).getString(PAYMENT_DATE));
                            payListDataArray.add(new ObjectOrderHistory(true, false, date1));
                        }

                        payListDataArray.add(new ObjectOrderHistory(false, false, dataArray.getJSONObject(i).toString()));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(PaymentList.this, payListDataArray);
        paymentListRv.setAdapter(paymentListAdapter);

        if (payListDataArray.size() > 0) {
            noDataFound.setVisibility(View.GONE);
        } else {
            noDataFound.setVisibility(View.VISIBLE);
            noDataFoundTv.setText(getString(R.string.no_payment_history));
        }
    }


    @Override
    public void onEmailSent() {
        Log.d("@@@Login_success", ": true");
        Toast.makeText(PaymentList.this, getString(R.string.check_your_email_for_detail), Toast.LENGTH_LONG).show();
        dialogImageSource.dismiss();
    }

    @Override
    public void onEmailFailed() {

        Log.d("@@@Login_success", ": true");
        Toast.makeText(PaymentList.this, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEmailError() {
        SmartUtils.hideLoadingDialog();
        Toast.makeText(PaymentList.this, getString(R.string.somthing_went_wrong), Toast.LENGTH_LONG).show();
    }


    @Override
    public void getPaymentHistorySuccess(JSONObject data) {
        try {
            noDataFound.setVisibility(View.GONE);
            paymentListJson = data.getJSONObject(DATA).getJSONArray("payment_detail");
            monthPaymentTv.setText(€ + " " + decimalFormatter(data.getJSONObject(DATA).getString("total_payment")));

            insertData(paymentListJson, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getPaymentHistoryFailed() {
        noDataFoundTv.setText(R.string.no_payment_history);
        noDataFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void getPaymentHistoryError() {
        SmartUtils.hideLoadingDialog();

    }


    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        int yearToSet, monthToSet, dayToSet;
        SmartEditText editText;

        public DatePickerFragment(SmartEditText editText) {
            this.editText = editText;
            String dateToSet = editText.getText().toString();
            if (!TextUtils.isEmpty(dateToSet)) {
                yearToSet = Integer.valueOf(dateToSet.split("-")[0]);
                monthToSet = Integer.valueOf(dateToSet.split("-")[1]) - 1;
                dayToSet = Integer.valueOf(dateToSet.split("-")[2]);
            } else {
                final Calendar calendar = Calendar.getInstance();
                yearToSet = calendar.get(Calendar.YEAR);
                monthToSet = calendar.get(Calendar.MONTH);
                dayToSet = calendar.get(Calendar.DAY_OF_MONTH);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK, this, yearToSet, monthToSet, dayToSet);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            editText.setText(year + "-" + (month + 1) + "-" + day);
        }
    }


    private boolean checkDates(String fromDate, String toDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format));
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(fromDate);
            date2 = simpleDateFormat.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert date2 != null;
        long difference = date2.getTime() - date1.getTime();

        System.out.println("date1 : " + Calendar.getInstance().getTime());
        System.out.println("date2 : " + date2);
        System.out.println("different : " + difference);

//        Log.d("@@HOUR_DIFFERENCE", "" + TimeUnit.MILLISECONDS.toHours(different));

        return difference >= 0;
    }


    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle
            actionBarDrawerToggle) {
        super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
        toolbar.setNavigationIcon(R.drawable.menu);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_userlist, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (payListDataArray != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //if(!TextUtils.isEmpty(query)){
                        callSearch(query);
                   // }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    /*if(!TextUtils.isEmpty(newText)){*/
                        if(paymentListJson != null){
                            insertData(paymentListJson, newText);
                        }
                    //}


                    return true;
                }

                public void callSearch(String query) {
                }

            });
        }
        return true;
    }
}
