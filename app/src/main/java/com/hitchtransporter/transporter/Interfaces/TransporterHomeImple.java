package com.hitchtransporter.transporter.Interfaces;

import org.json.JSONObject;

public interface TransporterHomeImple {

    void goToTrackScreen(int position);

    interface GetOrderListImplementation {

        void getOrderListSuccess(JSONObject responseJsonObject);

        void getOrderListFail(String responseMessage);

        void getOrderListError();

    }

}
