package com.hitchtransporter.transporter.HomeFragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hitchtransporter.transporter.APIsClasses.OrderHistoryAPIs;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.CustomClickListener;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartActivity;
import com.hitchtransporter.smart.framework.SmartFragment;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.Activities.Home;
import com.hitchtransporter.transporter.Adapters.OrderHistoryAdapter;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.hitchtransporter.transporter.POJO.ObjectOrderHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.hitchtransporter.smart.framework.SmartUtils.checkIfTransporter;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfUser;
import static com.hitchtransporter.smart.framework.SmartUtils.emailValidator;
import static com.hitchtransporter.smart.framework.SmartUtils.extractDate;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed;
import static com.hitchtransporter.smart.framework.SmartUtils.focusEditTextRed4;
import static com.hitchtransporter.smart.framework.SmartUtils.getCurrentDate;
import static com.hitchtransporter.smart.framework.SmartUtils.getDateDialog;


public class OrderHistory extends SmartFragment implements OrderHistoryImple.GetOrderHistoryImpl, OrderHistoryImple.OrderCancelImplementation, OrderHistoryImple.OrderStartImplementation, OrderHistoryImple.SendHistoryEmailImpl {

    private RecyclerView orderHistoryRv;
    private RecyclerView ongoingOrdersRv;

    private JSONObject allOrderHistory;

    private View noDataFound;
    private SmartTextView noDataFoundTv;
    private SmartTextView emailPaymentTV;
    private Dialog dialogImageSource;
    private SmartActivity activity;

    private SmartTextView ongoingOrdersTv;
    private SmartTextView orderHistoryTv;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (SmartActivity) context;
    }

    public OrderHistory() {
        // Required empty public constructor
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_order_history;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {
        orderHistoryRv = currentView.findViewById(R.id.order_history_rv);
        ongoingOrdersRv = currentView.findViewById(R.id.ongoing_orders_rv);

        noDataFound = currentView.findViewById(R.id.layout_no_data);
        noDataFoundTv = currentView.findViewById(R.id.no_data_found_tv);

        ongoingOrdersTv = currentView.findViewById(R.id.ongoing_orders_tv);
        orderHistoryTv = currentView.findViewById(R.id.order_history_tv);

        emailPaymentTV = currentView.findViewById(R.id.email_payment_tv);
        if (checkIfTransporter()) {
            emailPaymentTV.setVisibility(View.GONE);
        } else {
            emailPaymentTV.setVisibility(View.VISIBLE);
        }

        dialogImageSource = new Dialog(activity);

        orderHistoryRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderHistoryRv.setHasFixedSize(true);

        ongoingOrdersRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        ongoingOrdersRv.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void prepareViews(View currentView) {
        OrderHistoryAPIs.getOrderHistory(getActivity(), this);
    }

    @Override
    public void setActionListeners(View currentView) {
        emailPaymentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });
    }

    private boolean setRVs(JSONObject orderData, String jsonKey, SmartTextView tvTitle, RecyclerView RVs, String query) throws JSONException {
        boolean hasData = false;
        if (orderData.getJSONObject(DATA).has(jsonKey) && orderData.getJSONObject(DATA).getJSONArray(jsonKey).length() != 0) {
            ArrayList<ObjectOrderHistory> objectOrderHistories = insertData(orderData.getJSONObject(DATA).getJSONArray(jsonKey), query);
            if (objectOrderHistories.size() > 0) {
                setHasOptionsMenu(true);
                tvTitle.setVisibility(View.VISIBLE);
                RVs.setVisibility(View.VISIBLE);
                OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), objectOrderHistories);
                orderHistoryAdapter.setOrderStartImplementation(this);
                orderHistoryAdapter.setOrderCancelImplementation(this);
                orderHistoryAdapter.setOrderHistory(objectOrderHistories);
                RVs.setAdapter(orderHistoryAdapter);
                hasData = true;
            } else {
                tvTitle.setVisibility(View.GONE);
                RVs.setVisibility(View.GONE);
            }
        }
        return hasData;
    }


    private void divideAndInsertData(JSONObject orderData, String query) {
        boolean hasData = false;
        try {
            if (setRVs(orderData, ONGOING_ORDERS, ongoingOrdersTv, ongoingOrdersRv, query))
                hasData = true;

            if (setRVs(orderData, REST_ORDERS, orderHistoryTv, orderHistoryRv, query))
                hasData = true;

            if (hasData) {
                noDataFound.setVisibility(View.GONE);
            } else {
                noDataFound.setVisibility(View.VISIBLE);
                noDataFoundTv.setText(R.string.no_order_history);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<ObjectOrderHistory> insertData(JSONArray dataArray, String query) {
        String date = "0";
        ArrayList<ObjectOrderHistory> objectOrderHistories = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            try {
                if (query == null) {
                    if (!extractDate(dataArray.getJSONObject(i).getString(ORDER_DATETIME)).equalsIgnoreCase(date)) {
                        date = extractDate(dataArray.getJSONObject(i).getString(ORDER_DATETIME));
                        objectOrderHistories.add(new ObjectOrderHistory(true, false, date));
                    }

                    objectOrderHistories.add(new ObjectOrderHistory(false, false, dataArray.getJSONObject(i).toString()));
                } else {
                    String nameFromArray;
                    if (checkIfUser()) {
                        nameFromArray = dataArray.getJSONObject(i).getString(TRANSPORTER_NAME);
                    } else {
                        nameFromArray = dataArray.getJSONObject(i).getString(USER_NAME);
                    }
                    if (nameFromArray.toLowerCase().contains(query)) {
                        query = query.toLowerCase();
                        if (!extractDate(dataArray.getJSONObject(i).getString(ORDER_DATETIME)).equalsIgnoreCase(date)) {
                            date = extractDate(dataArray.getJSONObject(i).getString(ORDER_DATETIME));
                            objectOrderHistories.add(new ObjectOrderHistory(true, false, date));
                        }

                        objectOrderHistories.add(new ObjectOrderHistory(false, false, dataArray.getJSONObject(i).toString()));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objectOrderHistories;

    }


    private void showEmailDialog() {
//        dialogImageSource.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImageSource.setContentView(R.layout.dialog_email_payment);

        ImageView closePaymentIv = dialogImageSource.findViewById(R.id.close_payment_iv);
        final SmartEditText fromDatePaymentEt = dialogImageSource.findViewById(R.id.from_date_payment_et);
        final SmartEditText toDatePaymentEt = dialogImageSource.findViewById(R.id.to_date_payment_et);
        final SmartEditText emailPaymentEt = dialogImageSource.findViewById(R.id.email_payment_et);
        SmartTextView sendPaymentTv = dialogImageSource.findViewById(R.id.send_payment_tv);


        fromDatePaymentEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateDialog(getActivity(), getCurrentDate(), new CustomClickListener() {
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
                getDateDialog(getActivity(), getCurrentDate(), new CustomClickListener() {
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
                    focusEditTextRed(OrderHistory.this.getContext(), fromDatePaymentEt, false, null, 0);
                    Toast.makeText(OrderHistory.this.getContext(), getString(R.string.please_select_from_dat), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(toDatePaymentEt.getText().toString())) {
                    focusEditTextRed(OrderHistory.this.getContext(), toDatePaymentEt, false, null, 0);
                    Toast.makeText(OrderHistory.this.getContext(), getString(R.string.please_select_to_dat), Toast.LENGTH_LONG).show();
                } else if (!checkDates(fromDatePaymentEt.getText().toString(), toDatePaymentEt.getText().toString())) {
                    focusEditTextRed4(OrderHistory.this.getContext(), false, null, 0, fromDatePaymentEt, toDatePaymentEt);
                    Toast.makeText(OrderHistory.this.getContext(), getString(R.string.to_date_cannot_text), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(emailPaymentEt.getText().toString())) {
                    focusEditTextRed(OrderHistory.this.getContext(), emailPaymentEt, false, null, 0);
                    Toast.makeText(OrderHistory.this.getContext(), getString(R.string.please_enter_an_email_idd), Toast.LENGTH_LONG).show();
                } else if (!emailValidator(emailPaymentEt.getText().toString())) {
                    focusEditTextRed(OrderHistory.this.getContext(), emailPaymentEt, false, null, 0);
                    Toast.makeText(OrderHistory.this.getContext(), getString(R.string.please_enter_a_valid_email_id), Toast.LENGTH_LONG).show();
                } else {

                    OrderHistoryAPIs.sendPaymentDetails(getActivity(), fromDatePaymentEt.getText().toString(),
                            toDatePaymentEt.getText().toString(),
                            emailPaymentEt.getText().toString(), OrderHistory.this);
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


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onOrderStartSuccess(String orderId, String orderRequestId, String orderDataMessage, String dropOffLatitude, String dropOffLongitude) {

    }

    @Override
    public void onOrderStartFail(String orderDataMessage) {

    }

    @Override
    public void onOrderStartError() {

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onOrderCancelSuccess(String orderDataMessage) {
        Toast.makeText(getActivity(), orderDataMessage, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), Home.class);
        i.putExtra(SET_PAGE, ORDER_HISTORY);
        startActivity(i);
    }

    @Override
    public void onOrderCancelFail(String orderDataMessage) {
        Toast.makeText(getActivity(), orderDataMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderCancelError() {

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onOrderHistoryReceived(JSONObject orderData) {
        allOrderHistory = orderData;
        divideAndInsertData(allOrderHistory, null);
        SmartUtils.hideLoadingDialog();
    }

    @Override
    public void onOrderHistoryReceivedFail(JSONObject orderDataMessage) {
        SmartUtils.hideLoadingDialog();
        noDataFound.setVisibility(View.VISIBLE);
        noDataFoundTv.setText(R.string.no_order_history);
    }

    @Override
    public void onOrderHistoryReceivedError() {
        SmartUtils.hideLoadingDialog();
//        Toast.makeText(activity,getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onHistoryEmailSent(String orderDataMessage) {
        SmartUtils.hideLoadingDialog();
        if (dialogImageSource != null) {
            dialogImageSource.dismiss();
        }
        Log.d("@@@Login_success", ": true");
        Toast.makeText(activity, orderDataMessage, Toast.LENGTH_LONG).show();
        dialogImageSource.dismiss();
    }


    @Override
    public void onHistoryEmailSentFail(String orderDataMessage) {
        SmartUtils.hideLoadingDialog();
        Log.d("@@@Login_success", ": true");
        //Toast.makeText(activity, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHistorySentError() {

    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.menu_search_userlist, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (allOrderHistory != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    callSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    divideAndInsertData(allOrderHistory, newText);

                    return true;
                }

                public void callSearch(String query) {
                }

            });
        }
    }

}