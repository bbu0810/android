package com.hitchtransporter.transporter.Interfaces;

import org.json.JSONObject;

public interface OrderHistoryImple {

    interface GetOrderHistoryImpl {

        void onOrderHistoryReceived(JSONObject response);

        void onOrderHistoryReceivedFail(JSONObject response);

        void onOrderHistoryReceivedError();
    }

    interface OrderCancelImplementation {

        void onOrderCancelSuccess(String responseMessage);

        void onOrderCancelFail(String responseMessage);

        void onOrderCancelError();

    }

    interface OrderStartImplementation {

        void onOrderStartSuccess(String orderId, String orderRequestId, String responseMessage, String dropOffLatitude, String dropOffLongitude);

        void onOrderStartFail(String responseMessage);

        void onOrderStartError();
    }

    interface OrderFinishImplementation {

        void onOrderFinishSuccess(String responseMessage);

        void onOrderFinishFail(String responseMessage);

        void onOrderFinishError();
    }


    interface SendHistoryEmailImpl {

        void onHistoryEmailSent(String responseMessage);

        void onHistoryEmailSentFail(String responseMessage);

        void onHistorySentError();
    }

}
