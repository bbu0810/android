package com.hitchtransporter.transporter.APIsClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.Activities.Chat;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hitchtransporter.smart.framework.Constants.*;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfTransporter;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfUser;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.smart.framework.SmartUtils.hideLoadingDialog;

public class OrderHistoryAPIs {

    public static void setQuickBloxChatDialog(final Context context, final String chatId, String userByLogin, final String opponentId, final String opponentName) {

        QBUsers.getUserByLogin(userByLogin).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(final QBUser qbUser, Bundle bundle) {
                if (TextUtils.isEmpty(chatId)) {
                    createNewChatDialog(context, qbUser, opponentId, opponentName);
                } else {

                    QBRestChatService.getChatDialogById(chatId).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            hideLoadingDialog();
                            Intent i = new Intent(context, Chat.class);
                            i.putExtra("is_from_list",false);
                            i.putExtra(CHAT_DIALOG, qbChatDialog.getDialogId());
                            i.putExtra(OPPONENT_NAME, opponentName);
                            context.startActivity(i);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            hideLoadingDialog();
                            if (e.getMessage() != null) {
                                Log.e("@@ERR_SET_PRIVATE", e.getMessage());
                            } else {
                                Log.e("@@ERR_SET_PRIVATE", "NULL FROM QUICKBLOX");
                            }
                            createNewChatDialog(context, qbUser, opponentId, opponentName);

                        }
                    });
                }
            }

            @Override
            public void onError(QBResponseException e) {
                hideLoadingDialog();
                if (e.getMessage() != null) {
                    Log.e("@@ERR_GETTING_USER", e.getMessage());
                } else {
                    Log.e("@@ERR_GETTING_USER", "NULL FROM QUICKBLOX");
                }

            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private static void createNewChatDialog(final Context context, QBUser qbUser, final String opponentId, final String opponentName) {
        QBChatDialog dialog = DialogUtils.buildPrivateDialog(qbUser.getId());

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                hideLoadingDialog();

                //    Toast.makeText(context, "Created private chat dialog successfully", Toast.LENGTH_SHORT).show();

                setChatId(context, opponentId, qbChatDialog.getDialogId());

                Intent i = new Intent(context, Chat.class);
                i.putExtra("is_from_list",false);
                i.putExtra(CHAT_DIALOG, qbChatDialog.getDialogId());
                i.putExtra(OPPONENT_NAME, opponentName);

                context.startActivity(i);
            }

            @Override
            public void onError(QBResponseException e) {
                hideLoadingDialog();

                Toast.makeText(context, R.string.private_chat_failed, Toast.LENGTH_SHORT).show();
                if (e.getMessage() != null) {
                    Log.e("@@ERR_CREATE_PRIVATE", e.getMessage());
                } else {
                    Log.e("@@ERR_CREATE_PRIVATE", "NULL FROM QUICKBLOX");
                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private static void setChatId(final Context context, String opponentId, String chat_id) {
        SmartUtils.showLoadingDialog(context);

        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        String userId;
        String transporterId;

        if (checkIfUser()) {
            userId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID);
            transporterId = opponentId;
        } else {
            userId = opponentId;
            transporterId = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID);
        }

        params.put(USER_ID, userId);
        params.put(TRANSPORTER_ID, transporterId);
        params.put(CHAT_ID, chat_id);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "createChat");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    Log.d("@@@chat_creation", response.toString());
                  /*  try {
                        Toast.makeText(context, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else if (responseCode == 400) {
                    try {
                        Toast.makeText(context, response.getJSONObject(DATA).getString(MESSAGE), Toast.LENGTH_SHORT).show();
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
        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", true);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public static void getOrderHistory(Context context, final OrderHistoryImple.GetOrderHistoryImpl getOrderHistory) {
        SmartUtils.showLoadingDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        //params.put(USER_ID, "76");
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();

        if (checkIfTransporter()) {
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "getTransporterOrderHistory");
        } else {
            requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "getUserOrderHistory");
        }

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    getOrderHistory.onOrderHistoryReceived(response);
                } else if (responseCode == 400) {
                    getOrderHistory.onOrderHistoryReceivedFail(response);
                }
            }

            @Override
            public void onResponseError() {
                getOrderHistory.onOrderHistoryReceivedError();
            }
        });
        SmartWebManager.getInstance(context).addToRequestQueueMultipart(requestParams, "", false);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public static void sendPaymentDetails(Context context, String fromDate, String toDate, String email, final OrderHistoryImple.SendHistoryEmailImpl sendHistoryEmail) {

        SmartUtils.showLoadingDialog(context);

        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(USER_ID, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_ID, NO_USER_ID));
        params.put(FROM_DATE, fromDate);
        params.put(TO_DATE, toDate);
        params.put(EMAIL, email);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, context.getString(R.string.domain_name_local) + "UserOrdersMail");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, context);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode == 200) {
                    if(response.has("data")){
                        try {
                            sendHistoryEmail.onHistoryEmailSent(response.getJSONObject(DATA).getString(MESSAGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(activity, getString(R.string.check_your_email_for_detail), Toast.LENGTH_LONG).show();
                    }


                }
                if (responseCode == 400) {
                    sendHistoryEmail.onHistoryEmailSentFail(response.toString());


                }
            }

            @Override
            public void onResponseError() {
                sendHistoryEmail.onHistorySentError();


            }
        });
        SmartWebManager.getInstance(context.getApplicationContext()).addToRequestQueueMultipart(requestParams, "", true);

    }

}
