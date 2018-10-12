package com.hitchtransporter.transporter.HomeFragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartFragment;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.AA_Classes.SmartLocalBrodcast;
import com.hitchtransporter.transporter.APIsClasses.OrderAPIs;
import com.hitchtransporter.transporter.Activities.Home;
import com.hitchtransporter.transporter.Activities.TrackerHitcher;
import com.hitchtransporter.transporter.Adapters.TransporterHomeAdapter;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.hitchtransporter.transporter.Interfaces.TransporterHomeImple;
import com.hitchtransporter.transporter.Services.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.SmartUtils.getLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.getLongitude;
import static com.hitchtransporter.smart.framework.SmartUtils.setLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.setLongitude;

public class TransporterHome extends SmartFragment implements OnMapReadyCallback, OrderHistoryImple.OrderStartImplementation
        , OrderHistoryImple.OrderCancelImplementation,
        OrderHistoryImple.OrderFinishImplementation,
        TransporterHomeImple {

    public final static String RECEIVE_MESSAGE_FROM_FCM = "RECEIVE_MESSAGE_FROM_FCM";
    private static boolean transporterHomeIsOpen = false;
    //private GoogleMap mMap;
    boolean refreshHandler = true;
    Runnable refreshTracker;
    private RecyclerView transporterHomeRv;
    private TransporterHomeAdapter transporterHomeAdapter;
    private JSONArray orderListJson;
    private ArrayList<JSONObject> orderDetailArray = new ArrayList<>();
    private View noDataFound;
    private SmartTextView noDataFoundTv;
    private Handler mHandler = new Handler();
    private TransporterHomeImple.GetOrderListImplementation getOrderListImplementation;
    private BroadcastReceiver liveTracking;
    private TranspoterReceiver transpoterReceiver;
    private Intent serviceIntent;

    /*public TransporterHome() {
        // Required empty public constructor
    }
*/
    public static boolean isTransporterHomeIsOpen() {
        return transporterHomeIsOpen;
    }

    public static void setTransporterHomeIsOpen(boolean transporterHomeIsOpen) {
        TransporterHome.transporterHomeIsOpen = transporterHomeIsOpen;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_transporter_home;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {

        transporterHomeRv = currentView.findViewById(R.id.transporter_home_rv);
        noDataFound = currentView.findViewById(R.id.layout_no_data);
        noDataFoundTv = currentView.findViewById(R.id.no_data_found_tv);

        transporterHomeAdapter = new TransporterHomeAdapter(getActivity(), orderDetailArray, Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
        transporterHomeRv.setAdapter(transporterHomeAdapter);
        transporterHomeRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        transporterHomeAdapter.setGoToTrackScreenImplementation(this);
        transporterHomeAdapter.setOrderStartImplementation(this);
        transporterHomeAdapter.setOrderCancelImplementation(this);
        transporterHomeAdapter.setOrderFinishImplementation(this);

        liveTracking = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                setLongitude(intent.getStringExtra(LONGITUDE));
                setLatitude(intent.getStringExtra(LATITUDE));
            }
        };

        getOrderListImplementation = new GetOrderListImplementation() {
            @Override
            public void getOrderListSuccess(JSONObject responseJsonObject) {
                try {
                    noDataFound.setVisibility(View.GONE);
                    orderListJson = responseJsonObject.getJSONObject(DATA).getJSONArray("order_detail");
                    setHasOptionsMenu(true);
                    insertData("");
                    startRefreshingTask();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("@@HOME_LIST", "ERROR1");

                }
            }

            //{"status":"0","data":{"message":"VIIMEAIKAISIA TILAUKSIA EI LÖYDY"}}
            @Override
            public void getOrderListFail(String responseMessage) {
                Log.i("response message", ">>>>" + responseMessage);
                noDataFound.setVisibility(View.VISIBLE);
                noDataFoundTv.setText(getString(R.string.no_recent_orders));

            }

            @Override
            public void getOrderListError() {

            }
        };

        refreshTracker = new Runnable() {
            @Override
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            OrderAPIs.getOrderList(getActivity(), true, getOrderListImplementation);
                        }
                    }
                };
                mHandler.postDelayed(runnable, 5000);
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        transpoterReceiver = new TranspoterReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_MESSAGE_FROM_FCM);
        getActivity().registerReceiver(transpoterReceiver, intentFilter);
        TransporterHome.setTransporterHomeIsOpen(true);
        SmartLocalBrodcast.setRegisetBrodCast(getContext(), liveTracking, BROADCAST_MESSAGE);
        startLocationService("", "");
        setOrderListListener();
        SmartLocalBrodcast.setRegisetBrodCast(getContext(), liveTracking, BROADCAST_MESSAGE);
        OrderAPIs.getOrderList(getActivity(), false, getOrderListImplementation);
    }

    @Override
    public void onPause() {
        TransporterHome.setTransporterHomeIsOpen(false);
        getActivity().unregisterReceiver(transpoterReceiver);
        SmartLocalBrodcast.setUnregisterReceiver(getContext(), liveTracking);
        getActivity().stopService(serviceIntent);
        if (mHandler != null) {
            mHandler.removeCallbacks(refreshTracker);
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onPause();
    }

    @Override
    public void prepareViews(View currentView) {
        //OrderAPIs.getOrderList(getContext(), false, getOrderListImplementation);
    }

    @Override
    public void setActionListeners(View currentView) {

    }

    private void insertData(String filter) {
        orderDetailArray.clear();

        for (int i = 0; i < orderListJson.length(); i++) {
            if (TextUtils.isEmpty(filter)) {
                try {
                    orderDetailArray.add(orderListJson.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
//                    String nameFromJsonArray = orderListJson.getJSONObject(i).getString(FIRST_NAME) + " " + orderListJson.getJSONObject(i).getString(LAST_NAME);
                    String nameFromJsonArray = orderListJson.getJSONObject(i).getString(USER_NAME);
                    filter = filter.toLowerCase();
                    if (nameFromJsonArray.toLowerCase().contains(filter)) {
                        orderDetailArray.add(orderListJson.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        transporterHomeAdapter.notifyDataSetChanged();
    }

    private void startLocationService(String orderId, String orderRequestId) {
        serviceIntent = new Intent(getActivity(), LocationService.class);
        serviceIntent.putExtra(ORDER_ID, orderId);
        serviceIntent.putExtra(ORDER_REQUEST_ID, orderRequestId);
        getActivity().startService(serviceIntent);
    }


    //THREADS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void goToNavigation(String lattitude, String longitude) {
        try {
            String UrlToGive = "http://maps.google.com/maps?saddr=" + getLatitude() + "," + getLongitude() + Uri.encode("(My address 1)") +
                    "&daddr=" + lattitude + "," + longitude + Uri.encode("(Drop off location)");

            Log.e("@@url", UrlToGive);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(UrlToGive));
            startActivity(intent);

        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en")));
        }
    }

    void startRefreshingTask() {
        if (refreshHandler) {
            refreshTracker.run();
        }
    }

    //DOING THE MAP THING--------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;
    }


    //GET ORDERS LISTENERS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void setOrderListListener() {
    }


    //ORDER START LISTENERS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onOrderStartSuccess(String orderId, String orderRequestId, final String responseMessage, final String dropOffLatitude, final String dropOffLongitude) {
        startLocationService(orderId, orderRequestId);

        SmartUtils.getConfirmDialog(getActivity(), getString(R.string.job_startrd), getString(R.string.mdguser),
                getString(R.string.yes), getString(R.string.no), true, new AlertMagnatic() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
                        goToNavigation(dropOffLatitude, dropOffLongitude);

                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                        OrderAPIs.getOrderList(getActivity(), false, getOrderListImplementation);
                    }

                });
    }

    @Override
    public void onOrderStartFail(String responseMessage) {
        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onOrderStartError() {

    }


    //ORDER CANCEL LISTENERS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onOrderCancelSuccess(String responseMessage) {
        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), Home.class);
        i.putExtra(SET_PAGE, ORDER_HISTORY);
        startActivity(i);
    }

    @Override
    public void onOrderCancelFail(String responseMessage) {
        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderCancelError() {

    }


    //ORDER FINISH LISTENERS--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onOrderFinishSuccess(String responseMessage) {
        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
        getActivity().stopService(new Intent(getActivity(), LocationService.class));
        Intent i = new Intent(getActivity(), Home.class);
        i.putExtra(SET_PAGE, ORDER_HISTORY);
        getActivity().startActivity(i);
        getActivity().supportFinishAfterTransition();
    }

    @Override
    public void onOrderFinishFail(String responseMessage) {
        Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onOrderFinishError() {

    }


    //GO TO TRACK SCREEN LISTENER--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void goToTrackScreen(int position) {
        Intent i = new Intent(getActivity(), TrackerHitcher.class);
        try {
            /*mHandler.removeCallbacks(refreshTracker);
            mHandler.removeCallbacksAndMessages(null);*/
            i.putExtra(ORDER_REQUEST_ID, orderDetailArray.get(position).getString(ORDER_REQUEST_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(i);
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            OrderAPIs.getOrderList(getActivity(), false, getOrderListImplementation);
        }
        return super.onOptionsItemSelected(item);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.menu_search_refresh_userlist, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (orderListJson != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    callSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    insertData(newText);
                    return true;
                }

                public void callSearch(String query) {
                }

            });
        }
    }


    private class TranspoterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECEIVE_MESSAGE_FROM_FCM)) {
                //section_id":"8","type":3,"message":"ok"
                OrderAPIs.getOrderList(getActivity(), true, getOrderListImplementation);
            }

        }
    }
}
