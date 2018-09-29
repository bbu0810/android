package com.hitchtransporter.transporter.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartEditText;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.smart.weservice.SmartWebManager;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.hitchtransporter.transporter.AA_Classes.QBChatMessageHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.hitchtransporter.smart.framework.SmartUtils.getLanguageCode;
import static com.hitchtransporter.transporter.Activities.ChatList.setChatListIsOpen;

public class Chat extends MasterActivity implements QBChatDialogMessageListener {

    int receiverId = 0, senderID = 0;
    private QBChatDialog qbChatDialog;
    private RecyclerView chatRv;
    private SmartEditText chatEt;
    private ImageView chatSendBtn;
    private View noChatsLayout;
    private SmartTextView noDataFoundTv;
    private ArrayList<QBChatMessage> messages = new ArrayList<>();

    private String dialogId, senderName;

    private ChatMessageAdapter chatMessageAdapter;
    private ChatReceiver chatReceiver;


    @Override
    public int getLayoutID() {
        return R.layout.activity_chat;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();

        setHeaderToolbar(getIntent().getStringExtra(OPPONENT_NAME));
        setSwitch(false);

        noChatsLayout = findViewById(R.id.layout_no_chats);
        noDataFoundTv = findViewById(R.id.no_data_found_tv);
        chatRv = findViewById(R.id.chat_rv);
        chatEt = findViewById(R.id.chat_et);
        chatSendBtn = findViewById(R.id.chat_send_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Chat.this);
        chatRv.setLayoutManager(linearLayoutManager);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
            senderName = String.valueOf(getIntent().getExtras().get(SENDER_NAME));
            if (senderName.equalsIgnoreCase("null")) {
                senderName = String.valueOf(getIntent().getStringExtra(OPPONENT_NAME));
            }
            dialogId = String.valueOf(getIntent().getExtras().get(CHAT_ID));
            if (dialogId.equalsIgnoreCase("null")) {
                dialogId = String.valueOf(getIntent().getStringExtra(CHAT_DIALOG));
            }
        }

        if (dialogId == null) {
            Intent i = new Intent(Chat.this, ChatList.class);
            startActivity(i);
        } else {
            setHeaderToolbar(senderName);
            initChatDialogs(dialogId);
        }
        //retrieveAllMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(CURRENT_OPPONENT_ID, dialogId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatReceiver = new ChatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatList.RECEIVE_MESSAGE_FROM_CHAT);
        registerReceiver(chatReceiver, intentFilter);
        setChatListIsOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setChatListIsOpen(false);
        unregisterReceiver(chatReceiver);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(CURRENT_OPPONENT_ID, "");
    }

    private void scrollData() {
        if (messages != null) {
            chatRv.scrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();

        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msgToSend = chatEt.getText().toString();
                if (!msgToSend.equalsIgnoreCase("")) {
                    noChatsLayout.setVisibility(View.GONE);

                    msgToSend = msgToSend.trim();
                    final QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                    chatMessage.setBody(msgToSend);
                    chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                    chatMessage.setSaveToHistory(true);

                    try {
                        qbChatDialog.sendMessage(chatMessage);

                        QBChatMessageHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                        messages = QBChatMessageHolder.getInstance().getChatMessageByDialogId(qbChatDialog.getDialogId());

                        chatMessageAdapter.notifyDataSetChanged();
                        chatRv.smoothScrollToPosition(messages.size() - 1);

                        chatEt.setText("");
                        chatEt.setFocusable(true);


                        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
                        for (int userId : qbChatDialog.getOccupants()) {
                            if (userId != QBChatService.getInstance().getUser().getId()) {
                                Log.d("@@receiverId===", "" + userId);
                                receiverId = userId;
                                userIds.add(receiverId);
                            } else {
                                senderID = userId;
                                Log.d("@@senderID===", "" + senderID);
                            }
                        }

                        QBUsers.getUser(receiverId).performAsync(new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {
                                try {
                                    JSONObject userData = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_USER, NO_DATA));
                                    sendNotification(chatMessage.getBody(),
                                            String.valueOf(senderID),
                                            String.valueOf(receiverId),
                                            qbUser.getLogin(),
                                            userData.getString(FIRST_NAME) + " " + userData.getString(LAST_NAME),
                                            qbChatDialog.getDialogId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                        Toast.makeText(Chat.this, R.string.msg_sending_failed, LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void retrieveAllMessages(boolean isLoad) {
        noChatsLayout.setVisibility(View.GONE);
        if (isLoad) {
            SmartUtils.showLoadingDialog(Chat.this);
        }

        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500); //get limit 500 messages

        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    noChatsLayout.setVisibility(View.GONE);
                    //Put messages to cache
                    SmartUtils.hideLoadingDialog();

                    QBChatMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);
                    messages = qbChatMessages;
                    if (messages.size() == 0) {
                        noChatsLayout.setVisibility(View.VISIBLE);
                        noDataFoundTv.setText(R.string.start_conversation);
                    }
                    chatMessageAdapter = new ChatMessageAdapter();

                    chatRv.setAdapter(chatMessageAdapter);


                    chatRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                chatRv.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (chatRv.getAdapter().getItemCount() > 0) {
                                            chatRv.scrollToPosition(
                                                    chatRv.getAdapter().getItemCount() - 1);
                                        }
                                    }
                                }, 100);
                            }
                        }
                    });

                    scrollData();

                }

                @Override
                public void onError(QBResponseException e) {
                    SmartUtils.hideLoadingDialog();

                }
            });
        }
    }

    private void initChatDialogs(String dialogId) {
        QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialogInner, Bundle bundle) {
                qbChatDialog = qbChatDialogInner;

                try {
                    qbChatDialog.initForChat(QBChatService.getInstance());
                    qbChatDialog.addMessageListener(Chat.this);
                    retrieveAllMessages(true);
                } catch (IllegalArgumentException e) {
                    Intent i = new Intent(Chat.this, ChatList.class);
                    startActivity(i);
                }

            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(Chat.this, R.string.chat_dialog_failed, LENGTH_LONG).show();
                if (e.getMessage() != null) {
                    Log.e("@@ERR_CHAT_DIALOG", e.getMessage());
                } else {
                    Log.e("@@ERR_CHAT_DIALOG", "NULL FROM QUICKBLOX");
                }
            }
        });
    }


    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        //Cache Message
        if (qbChatMessage != null) {
            //  noChatsLayout.setVisibility(View.GONE);
            QBChatMessageHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
            messages = QBChatMessageHolder.getInstance().getChatMessageByDialogId(qbChatMessage.getDialogId());
            chatMessageAdapter = new ChatMessageAdapter();

            chatRv.setAdapter(chatMessageAdapter);
            scrollData();
        }
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        if (e.getMessage() != null) {
            Log.e("@@ERR_MSGS", e.getMessage());
        } else {
            Log.e("@@ERR_MSGS", "NULL FROM QUICKBLOX");
        }
    }

    private void sendNotification(String msg, String sender_id, String receiver_id, String receiver_email, String sender_name, String chat_id) {
        Map<String, String> params = new HashMap<>();
        params.put(LANGUAGE, getLanguageCode());
        params.put(MSG, msg);
        params.put(SENDER_ID, sender_id);
        params.put(RECEIVER_ID, receiver_id);
        params.put(RECEIVER_EMAIL, receiver_email);
        params.put(CHAT_ID, chat_id);
        params.put(SENDER_NAME, sender_name);
        params.put(TYPE, ANDROID);

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getString(R.string.domain_name_local) + "saveChat");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, Chat.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, params);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                if (responseCode != 200) {
                    try {
                        SmartUtils.showSnackBar(Chat.this, response.getJSONObject(DATA).getString(MESSAGE), Snackbar.LENGTH_LONG);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        Log.e("@@ERR_CHAT_NOTIFICATION", response.getString(DATA));
                        //  SmartUtils.showSnackBar(Chat.this, response.getString(DATA), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponseError() {
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, false, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qbChatDialog != null) {
            qbChatDialog.removeMessageListrener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (qbChatDialog != null) {
            qbChatDialog.removeMessageListrener(this);
        }
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
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

    @Override
    public void onBackPressed() {
        SmartUtils.hideSoftKeyboard(Chat.this);
        super.onBackPressed();

        if (getIntent().getBooleanExtra("is_from_list", true)) {
            supportFinishAfterTransition();
        } else {

            Intent i = new Intent(Chat.this, ChatList.class);
            startActivity(i);
        }

    }

    private class ChatMessageAdapter extends RecyclerView.Adapter {

        private int VIEW_SEND = 0;
        private int VIEW_RECIVED = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_SEND) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_send_messages, parent, false);
                return new SendViewHolder(v);
            } else if (viewType == VIEW_RECIVED) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receive_messages, parent, false);
                return new RecievedViewHolder(v);
            }
            return null;
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof SendViewHolder) {
                final SendViewHolder sendViewHolder = (SendViewHolder) holder;
                sendViewHolder.messageTv.setText(messages.get(position).getBody());
                sendViewHolder.messageDateTimeTv.setText(SmartUtils.formatTimeStampDate2(Chat.this, messages.get(position).getDateSent()));
            } else if (holder instanceof RecievedViewHolder) {
                final RecievedViewHolder recievedViewHolder = (RecievedViewHolder) holder;
                recievedViewHolder.messageTv.setText(messages.get(position).getBody());
                recievedViewHolder.messageDateTimeTv.setText(SmartUtils.formatTimeStampDate2(Chat.this, messages.get(position).getDateSent()));
            }
        }

        @Override
        public int getItemCount() {
            if (messages != null) {
                return messages.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int result;
            if (messages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())) {
                result = VIEW_SEND;
            } else {
                result = VIEW_RECIVED;
            }
            return result;
        }


        private class SendViewHolder extends RecyclerView.ViewHolder {
            private SmartTextView messageTv, messageDateTimeTv;

            private SendViewHolder(View itemView) {
                super(itemView);
                messageTv = itemView.findViewById(R.id.message_tv);
                messageDateTimeTv = itemView.findViewById(R.id.message_date_time_tv);
            }
        }

        private class RecievedViewHolder extends RecyclerView.ViewHolder {
            private SmartTextView messageTv, messageDateTimeTv;


            private RecievedViewHolder(View itemView) {
                super(itemView);
                messageTv = itemView.findViewById(R.id.message_tv);
                messageDateTimeTv = itemView.findViewById(R.id.message_date_time_tv);
            }
        }
    }

    private class ChatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ChatList.RECEIVE_MESSAGE_FROM_CHAT)) {
                //section_id":"8","type":3,"message":"ok"
                retrieveAllMessages(false);
            }

        }
    }
}
