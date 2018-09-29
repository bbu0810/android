package com.hitchtransporter.transporter.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.Activities.Rating;
import com.hitchtransporter.transporter.Activities.TrackerHitcher;
import com.hitchtransporter.transporter.Activities.TrackerUser;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.hitchtransporter.transporter.POJO.ObjectOrderHistory;
import com.hitchtransporter.transporter.APIsClasses.OrderAPIs;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.Constants.*;

import static com.hitchtransporter.smart.framework.SmartUtils.checkIfTransporter;
import static com.hitchtransporter.smart.framework.SmartUtils.checkIfUser;
import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.getLatitude;
import static com.hitchtransporter.smart.framework.SmartUtils.getLongitude;
import static com.hitchtransporter.smart.framework.SmartUtils.hideLoadingDialog;
import static com.hitchtransporter.transporter.APIsClasses.OrderHistoryAPIs.setQuickBloxChatDialog;

public class OrderHistoryAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ObjectOrderHistory> orderHistoryDataArray;
    private OrderHistoryImple.OrderCancelImplementation orderCancelImplementation;
    private OrderHistoryImple.OrderStartImplementation orderStartImplementation;


    public OrderHistoryAdapter(Context context, ArrayList<ObjectOrderHistory> orderHistoryDataArray) {
        this.context = context;
        this.orderHistoryDataArray = orderHistoryDataArray;
    }

    private int VIEW_DATE = 0;
    private int VIEW_ORDER_DATA = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_DATE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history_date, parent, false);
            return new DateViewHolder(v);
        } else if (viewType == VIEW_ORDER_DATA) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history, parent, false);
            return new OrderHistoryDataViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final String opponentName, opponentId;
        final boolean isTransporter = checkIfTransporter();
        if (holder instanceof DateViewHolder) {
            final DateViewHolder dateViewHolder = (DateViewHolder) holder;
            try {

                dateViewHolder.dateTv.setText(dateFormatChanger(orderHistoryDataArray.get(position).getOrderData()));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (holder instanceof OrderHistoryDataViewHolder) {
            final OrderHistoryDataViewHolder orderHistoryDataViewHolder = (OrderHistoryDataViewHolder) holder;
            try {
                final JSONObject orderDetailJson = new JSONObject(orderHistoryDataArray.get(position).getOrderData());
               /* if (!IS_HISTORY_STARTRD && !orderDetailJson.getString(ORDER_STATUS).equalsIgnoreCase(STATUS_STARTED)) {
                    IS_HISTORY_STARTRD = true;
                    orderHistoryDataViewHolder.headerTitle.setVisibility(View.VISIBLE);
                } else
                    orderHistoryDataViewHolder.headerTitle.setVisibility(View.GONE);
*/

                orderHistoryDataViewHolder.total_amt_val.setText(String.valueOf(€ + " " + decimalFormatter(orderDetailJson.getString(AMOUNT))));
                orderHistoryDataViewHolder.txt_vat.setText(String.valueOf(orderDetailJson.getString(VAT_PERCENT)));

                orderHistoryDataViewHolder.app_fee_val.setText(String.valueOf(orderDetailJson.getString(APP_FEE)));
                orderHistoryDataViewHolder.txt_app_fee_vat.setText(String.valueOf(orderDetailJson.getString(APP_FEE_VAT)));
                orderHistoryDataViewHolder.refund_amt.setText(String.valueOf(orderDetailJson.getString(REFUND_AMOUNT)));
                orderHistoryDataViewHolder.final_amt.setText(String.valueOf(orderDetailJson.getString(FINAL_AMOUNT)));

                if (orderDetailJson.getString(REFUND_AMOUNT).equalsIgnoreCase("0")) {
                    orderHistoryDataViewHolder.refundAmountLl.setVisibility(View.GONE);
                    orderHistoryDataViewHolder.finalAmountLl.setVisibility(View.GONE);
                } else {
                    orderHistoryDataViewHolder.refundAmountLl.setVisibility(View.VISIBLE);
                    orderHistoryDataViewHolder.finalAmountLl.setVisibility(View.VISIBLE);
                }
//                orderHistoryDataViewHolder.vat_amt.setText("VAT " + orderDetailJson.getString(VAT_PERCENT_H) + "%" + " " + € + " " + decimalFormatter(orderDetailJson.getString(VAT_AMOUNT)));
                if (!orderDetailJson.getString(PROFILE_IMAGE).equalsIgnoreCase("")) {
                    Picasso.with(context).load(orderDetailJson.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(orderHistoryDataViewHolder.imageHistoryTv);
                }
                if (isTransporter) {
                    opponentId = orderDetailJson.getString(USER_ID);
                    opponentName = orderDetailJson.getString(USER_NAME);
                    orderHistoryDataViewHolder.nameHistoryTv.setText(opponentName);
                } else {
                    opponentId = orderDetailJson.getString(TRANSPORTER_ID);
                    opponentName = orderDetailJson.getString(TRANSPORTER_NAME);
                    orderHistoryDataViewHolder.nameHistoryTv.setText(opponentName);
                    orderHistoryDataViewHolder.ratingHistoryRb.setRating(Float.parseFloat(orderDetailJson.getString(RATING)));
                    orderHistoryDataViewHolder.ratingHistoryTv.setText(orderDetailJson.getString(RATING));

                }
                orderHistoryDataViewHolder.orderHistoryTv.setText(orderDetailJson.getString(ORDER_ID));

                orderHistoryDataViewHolder.distanceHistoryTv.setText(decimalFormatter(orderDetailJson.getString(DISTANCE)) + " " + Km);
                orderHistoryDataViewHolder.dateHistoryTv.setText(dateFormatChanger(orderDetailJson.getString(ORDER_DATETIME).split(" ")[0]));
                orderHistoryDataViewHolder.pickupHistoryTv.setText(orderDetailJson.getString(PICKUP_ADDRESS));
                orderHistoryDataViewHolder.dropoffHistoryTv.setText(orderDetailJson.getString(DROPOFF_ADDRESS));
                orderHistoryDataViewHolder.priceHistoryTv.setText(€ + " " + decimalFormatter(orderDetailJson.getString(AMOUNT)));
                orderHistoryDataViewHolder.heightHistoryTv.setText(orderDetailJson.getString(HEIGHT) + " " + Cm);
                orderHistoryDataViewHolder.weightHistoryTv.setText(orderDetailJson.getString(WEIGHT) + " " + Kg);
                orderHistoryDataViewHolder.widthHistoryTv.setText(orderDetailJson.getString(WIDTH) + " " + Cm);
                orderHistoryDataViewHolder.lengthHistoryTv.setText(orderDetailJson.getString(LENGTH) + " " + Cm);


                orderHistoryDataViewHolder.giveRatingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, Rating.class);
                        try {
                            i.putExtra(ORDER_ID, orderDetailJson.getString(ORDER_ID));
                            i.putExtra(TRANSPORTER_ID, orderDetailJson.getString(TRANSPORTER_ID));
                            i.putExtra(TRANSPORTER_NAME, orderDetailJson.getString(TRANSPORTER_NAME));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        context.startActivity(i);
                    }
                });


                orderHistoryDataViewHolder.trackerHistoryIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        if (checkIfUser()) {
                            i = new Intent(context, TrackerUser.class);
                        } else {
                            i = new Intent(context, TrackerHitcher.class);
                        }
                        try {
                            i.putExtra(ORDER_REQUEST_ID, orderDetailJson.getString(ORDER_REQUEST_ID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(i);
                    }
                });

                orderHistoryDataViewHolder.chatHistoryIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmartUtils.showLoadingDialog(context);
                        try {
                            setQuickBloxChatDialog(context, orderDetailJson.getString(CHAT_ID), orderDetailJson.getString(EMAIL), opponentId, opponentName);
                        } catch (JSONException e) {
                            hideLoadingDialog();
                            e.printStackTrace();
                        }
                    }
                });


                switch (orderDetailJson.getString(ORDER_STATUS)) {
                    case STATUS_PENDING:
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.VISIBLE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.pending));
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);

                        break;

                    case STATUS_APPROVED:
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.VISIBLE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.approved));
                        if (orderDetailJson.getString(IS_PAY).equalsIgnoreCase(STATUS_PAID)) {
                            orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.VISIBLE);
                            orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.paid));
                            orderHistoryDataViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i;
                                    if (checkIfUser()) {
                                        i = new Intent(context, TrackerUser.class);
                                    } else {
                                        i = new Intent(context, TrackerHitcher.class);
                                    }
                                    try {
                                        i.putExtra(ORDER_REQUEST_ID, orderDetailJson.getString(ORDER_REQUEST_ID));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    context.startActivity(i);
                                }
                            });
                        }
                        break;

                    case STATUS_REJECTED:
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.rejected));
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.GONE);
                        break;
                    case STATUS_STARTED:
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.VISIBLE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.on_going_order));
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_COMPLETED:
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.delivered));
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.GONE);
                        break;
                    case STATUS_CANCELLED:
                        orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);
                        orderHistoryDataViewHolder.orderStatusTv.setText(context.getString(R.string.cancelled));
                        orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.GONE);
                        break;

                }

                if (!isTransporter) {
                    if (orderDetailJson.getString(IS_RATING).equalsIgnoreCase("1")) {
                        orderHistoryDataViewHolder.giveRatingBtn.setVisibility(View.GONE);
                    } else {
                        orderHistoryDataViewHolder.giveRatingBtn.setVisibility(View.VISIBLE);
                    }
                }


                orderHistoryDataViewHolder.startJobBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            orderHistoryDataViewHolder.orderStartListener(orderDetailJson.getString(ORDER_ID),
                                    orderDetailJson.getString(ORDER_REQUEST_ID), getLatitude(), getLongitude(),
                                    orderDetailJson.getString(DROPOFF_LATITUDE), orderDetailJson.getString(DROPOFF_LONGITUDE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                orderHistoryDataViewHolder.cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SmartUtils.getConfirmDialog(context, context.getString(R.string.cancel_order), context.getString(R.string.cancel_order_txt),
                                context.getString(R.string.yes), context.getString(R.string.no), true, new AlertMagnatic() {
                                    @Override
                                    public void PositiveMethod(DialogInterface dialog, int id) {
                                        try {
                                            orderHistoryDataViewHolder.orderCancelListener(orderDetailJson.getString(ORDER_ID));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void NegativeMethod(DialogInterface dialog, int id) {
                                    }
                                });
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isTransporter) {
                orderHistoryDataViewHolder.giveRatingBtn.setVisibility(View.GONE);
                orderHistoryDataViewHolder.ratingHistoryRb.setVisibility(View.GONE);
                orderHistoryDataViewHolder.ratingHistoryTv.setVisibility(View.GONE);
                orderHistoryDataViewHolder.cancelOrderBtn.setVisibility(View.GONE);
                orderHistoryDataViewHolder.startJobBtn.setVisibility(View.GONE);
                orderHistoryDataViewHolder.trackerHistoryIv.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        int result;
        if (orderHistoryDataArray.get(position).getIsDate()) {
            result = VIEW_DATE;
        } else {
            result = VIEW_ORDER_DATA;
        }
        return result;
    }


    @Override
    public int getItemCount() {
        return orderHistoryDataArray.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        private SmartTextView dateTv;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.date_tv);
        }
    }


    public void setOrderHistory(ArrayList<ObjectOrderHistory> orderHistoryDataArray) {
        this.orderHistoryDataArray = orderHistoryDataArray;
        notifyDataSetChanged();

    }

    public class OrderHistoryDataViewHolder extends RecyclerView.ViewHolder {

        private SmartTextView orderHistoryTv;
        private RoundedImageView imageHistoryTv;
        private SmartTextView nameHistoryTv;
        private RatingBar ratingHistoryRb;
        private SmartTextView ratingHistoryTv;
        private SmartTextView distanceHistoryTv;
        private SmartTextView dateHistoryTv;
        private SmartTextView pickupHistoryTv;
        private SmartTextView dropoffHistoryTv;
        private SmartTextView priceHistoryTv;
        private SmartTextView trackerHistoryIv;
        private ImageView chatHistoryIv;
        private SmartTextView heightHistoryTv;
        private SmartTextView weightHistoryTv;
        private SmartTextView widthHistoryTv;
        private SmartTextView lengthHistoryTv;
        private SmartTextView orderStatusTv;
        private LinearLayout buttonsLl;
        private SmartButton giveRatingBtn;
        private SmartButton cancelOrderBtn;
        private SmartButton startJobBtn;
        // private SmartTextView headerTitle;
        private SmartTextView total_amt_val;
        private SmartTextView txt_vat;
        private SmartTextView app_fee_val;
        private SmartTextView txt_app_fee_vat;
        private SmartTextView refund_amt;
        private SmartTextView final_amt;
        private LinearLayout refundAmountLl;
        private LinearLayout finalAmountLl;

        public OrderHistoryDataViewHolder(View itemView) {
            super(itemView);
            refund_amt = itemView.findViewById(R.id.refund_amount);
            refundAmountLl = itemView.findViewById(R.id.llRefundAmount);
            finalAmountLl = itemView.findViewById(R.id.llFinalAmt);
            final_amt = itemView.findViewById(R.id.txtFinalAmount);
            total_amt_val = itemView.findViewById(R.id.total_amt_val);
            txt_vat = itemView.findViewById(R.id.txt_vat);
            app_fee_val = itemView.findViewById(R.id.app_fee_val);
            txt_app_fee_vat = itemView.findViewById(R.id.txt_app_fee_vat);

            orderHistoryTv = itemView.findViewById(R.id.order_history_tv);
            //  headerTitle = itemView.findViewById(R.id.txtHeader);
            imageHistoryTv = itemView.findViewById(R.id.image_history_tv);
            nameHistoryTv = itemView.findViewById(R.id.name_history_tv);
            ratingHistoryRb = itemView.findViewById(R.id.rating_history_rb);
            ratingHistoryTv = itemView.findViewById(R.id.rating_history_tv);
            distanceHistoryTv = itemView.findViewById(R.id.distance_history_tv);
            dateHistoryTv = itemView.findViewById(R.id.date_history_tv);
            pickupHistoryTv = itemView.findViewById(R.id.pickup_history_tv);
            dropoffHistoryTv = itemView.findViewById(R.id.dropoff_history_tv);
            priceHistoryTv = itemView.findViewById(R.id.price_history_tv);
            trackerHistoryIv = itemView.findViewById(R.id.tracker_history_iv);
            chatHistoryIv = itemView.findViewById(R.id.chat_history_iv);
            heightHistoryTv = itemView.findViewById(R.id.height_history_tv);
            weightHistoryTv = itemView.findViewById(R.id.weight_history_tv);
            widthHistoryTv = itemView.findViewById(R.id.width_history_tv);
            lengthHistoryTv = itemView.findViewById(R.id.length_history_tv);
            orderStatusTv = itemView.findViewById(R.id.order_status_tv);
            buttonsLl = itemView.findViewById(R.id.buttons_ll);
            giveRatingBtn = itemView.findViewById(R.id.give_rating_btn);
            cancelOrderBtn = itemView.findViewById(R.id.cancel_order_btn);
            startJobBtn = itemView.findViewById(R.id.start_job_btn);
        }


        public void orderStartListener(String orderId, String orderRequestId, String currentLatitude, String currentLongitude, String dropOffLatitude, String dropOffLongitude) {
            if (orderStartImplementation != null) {
                OrderAPIs.onStartJob(context, orderId, orderRequestId, currentLatitude, currentLongitude, dropOffLatitude, dropOffLongitude, orderStartImplementation);
            }
        }

        public void orderCancelListener(String orderId) {
            if (orderCancelImplementation != null) {
                OrderAPIs.onCancelJob(context, orderId, orderCancelImplementation);
            }
        }
    }

    public void setOrderStartImplementation(OrderHistoryImple.OrderStartImplementation orderStartImplementation) {
        this.orderStartImplementation = orderStartImplementation;
    }


    public void setOrderCancelImplementation(OrderHistoryImple.OrderCancelImplementation orderCancelImplementation) {
        this.orderCancelImplementation = orderCancelImplementation;
    }

}