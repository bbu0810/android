package com.hitchtransporter.transporter.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.Toast;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.AA_Classes.MasterActivity;
import com.hitchtransporter.transporter.HomeFragments.TransporterHome;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.SmartUtils.formatTimeStampDate;

public class ChatList extends MasterActivity {

    RecyclerView myChatsRv;
    ArrayList<QBChatDialog> qbChatDialogsArray = new ArrayList<>();
    private View noChatsDialogLayout;
    private SmartTextView noDataFoundTv;
    public final static String RECEIVE_MESSAGE_FROM_CHAT = "RECEIVE_MESSAGE_FROM_CHAT";
    private static boolean chatListIsOpen = false;
    private ChatListReceiver chatListReceiver;

    public static boolean isChatListIsOpen() {
        return chatListIsOpen;
    }

    public static void setChatListIsOpen(boolean chatListIsOpen) {
        ChatList.chatListIsOpen = chatListIsOpen;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_my_chats;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        setHeaderToolbar(getString(R.string.my_chats));
        setSwitch(true);

        myChatsRv = findViewById(R.id.mychats_rv);
        noChatsDialogLayout = findViewById(R.id.layout_no_chats_dialog);
        noDataFoundTv = findViewById(R.id.no_data_found_tv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatList.this);
        myChatsRv.setLayoutManager(linearLayoutManager);


    }

    @Override
    protected void onResume() {
        super.onResume();
        chatListReceiver = new ChatListReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_MESSAGE_FROM_CHAT);
        registerReceiver(chatListReceiver, intentFilter);
        setChatListIsOpen(true);
        qbChatDialogsArray.clear();
        loadChatDialogs(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        setChatListIsOpen(false);
        unregisterReceiver(chatListReceiver);
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

    private void loadChatDialogs(boolean isLoad) {
        if(isLoad){
            SmartUtils.showLoadingDialog(ChatList.this);
        }
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
        requestBuilder.sortDesc("last_message_date_sent");

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                SmartUtils.hideLoadingDialog();


                qbChatDialogsArray = qbChatDialogs;

                if (qbChatDialogsArray.size() == 0) {
                    noChatsDialogLayout.setVisibility(View.VISIBLE);
                    noDataFoundTv.setText(R.string.no_chats_available);
                }

                ChatDialogAdapter adapter = new ChatDialogAdapter();
                myChatsRv.setAdapter(adapter);


            }

            @Override
            public void onError(QBResponseException e) {
                SmartUtils.hideLoadingDialog();
                if (qbChatDialogsArray.size() == 0) {
                    noChatsDialogLayout.setVisibility(View.VISIBLE);
                    noDataFoundTv.setText(R.string.no_chats_available);
                }

                Toast.makeText(ChatList.this, getString(R.string.chat_dialog_failed), Toast.LENGTH_SHORT).show();
                if (e.getMessage() != null) {
                    Log.e("ERR_LOADING_CHAT_DIALOG", e.getMessage());
                } else {
                    Log.e("ERR_LOADING_CHAT_DIALOG", "NULL FROM QUICKBLOX");
                }
            }
        });

    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle
            actionBarDrawerToggle) {
        super.manageAppBar(actionBar, toolbar, actionBarDrawerToggle);
        toolbar.setNavigationIcon(R.drawable.menu);
    }

    private class ChatDialogAdapter extends RecyclerView.Adapter<ChatDialogAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_chat_dialogs, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.opponentTv.setText(qbChatDialogsArray.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ChatList.this, Chat.class);
                    i.putExtra(CHAT_DIALOG, qbChatDialogsArray.get(position).getDialogId());
                    i.putExtra(OPPONENT_NAME, qbChatDialogsArray.get(position).getName());
                    i.putExtra("is_from_list",true);
                    startActivity(i);
                }
            });


            holder.oppnentInitTv.setText(holder.opponentTv.getText().toString().substring(0, 1).toUpperCase());
            if (TextUtils.isEmpty(qbChatDialogsArray.get(position).getLastMessage())) {
                holder.lastMsgTv.setText(R.string.click_start_conversation);
            } else {
                holder.lastMsgTv.setText(qbChatDialogsArray.get(position).getLastMessage());

            }

            if (qbChatDialogsArray.get(position).getLastMessageDateSent() != 0) {
                Log.d("@@date", String.valueOf(qbChatDialogsArray.get(position).getLastMessageDateSent()));
                holder.lastMsgDateTv.setVisibility(View.VISIBLE);
                holder.lastMsgDateTv.setText(formatTimeStampDate(ChatList.this, qbChatDialogsArray.get(position).getLastMessageDateSent()));
            } else {
                holder.lastMsgDateTv.setVisibility(View.GONE);
            }

            int chatCount = qbChatDialogsArray.get(position).getUnreadMessageCount();
            if (chatCount > 0) {
                holder.unreadMsgTv.setVisibility(View.VISIBLE);
                holder.unreadMsgTv.setText(String.valueOf(chatCount));
            }


        }

        @Override
        public int getItemCount() {
            return qbChatDialogsArray.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SmartTextView oppnentInitTv;
            private SmartTextView opponentTv;
            private SmartTextView lastMsgTv;
            private SmartTextView lastMsgDateTv;
            private SmartTextView unreadMsgTv;


            public ViewHolder(View itemView) {
                super(itemView);
                oppnentInitTv = itemView.findViewById(R.id.opponent_init_tv);
                opponentTv = itemView.findViewById(R.id.opponent_tv);
                lastMsgTv = itemView.findViewById(R.id.last_msg_tv);
                lastMsgDateTv = itemView.findViewById(R.id.last_msg_date_tv);
                unreadMsgTv = itemView.findViewById(R.id.unread_msg_tv);
            }
        }
    }


    private class ChatListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECEIVE_MESSAGE_FROM_CHAT)) {
                //section_id":"8","type":3,"message":"ok"
                qbChatDialogsArray.clear();
                loadChatDialogs(false);
            }

        }
    }


}
