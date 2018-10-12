package com.hitchtransporter.transporter.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.transporter.POJO.ObjectOrderHistory;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.Constants.ADDRESS;
import static com.hitchtransporter.smart.framework.Constants.AMOUNT;
import static com.hitchtransporter.smart.framework.Constants.APP_FEE;
import static com.hitchtransporter.smart.framework.Constants.APP_FEE_VAT;
import static com.hitchtransporter.smart.framework.Constants.FINAL_AMOUNT;
import static com.hitchtransporter.smart.framework.Constants.ORDER_ID;
import static com.hitchtransporter.smart.framework.Constants.PROFILE_IMAGE;
import static com.hitchtransporter.smart.framework.Constants.REFUND_AMOUNT;
import static com.hitchtransporter.smart.framework.Constants.USER_NAME;
import static com.hitchtransporter.smart.framework.Constants.VAT_PERCENT;
import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;

public class PaymentListAdapter extends RecyclerView.Adapter {


    private Context context;
    private ArrayList<ObjectOrderHistory> payListDataArray;

    private int VIEW_DATE = 0;
    private int VIEW_PAYLIST = 1;

    public PaymentListAdapter(Context context, ArrayList<ObjectOrderHistory> payListDataArray) {
        this.context = context;
        this.payListDataArray = payListDataArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_DATE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history_date, parent, false);
            return new DateViewHolder(v);
        } else if (viewType == VIEW_PAYLIST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_list, parent, false);
            return new PaylistViewHolder(v);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DateViewHolder) {
            final DateViewHolder dateViewHolder = (DateViewHolder) holder;
            try {
                dateViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
                dateViewHolder.dateTv.setText(dateFormatChanger(payListDataArray.get(position).getOrderData()));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (holder instanceof PaylistViewHolder) {
            final PaylistViewHolder paylistViewHolder = (PaylistViewHolder) holder;
            try {
                final JSONObject orderDetailJson = new JSONObject(payListDataArray.get(position).getOrderData());
                if (!TextUtils.isEmpty(orderDetailJson.getString(PROFILE_IMAGE))) {
                    Picasso.with(context).load(orderDetailJson.getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(paylistViewHolder.imagePaylistIv);
                }
                paylistViewHolder.orderNoPaymentTv.setText(orderDetailJson.getString(ORDER_ID));
                paylistViewHolder.namePaylistTv.setText(orderDetailJson.getString(USER_NAME));
                paylistViewHolder.addressPaylistTv.setText(orderDetailJson.getString(ADDRESS));
                paylistViewHolder.totalAmountTv.setText(decimalFormatter(String.valueOf(orderDetailJson.getString(AMOUNT))));
                paylistViewHolder.app_fee_val.setText(String.valueOf(orderDetailJson.getString(APP_FEE)));
                paylistViewHolder.app_fee_vat_val.setText(String.valueOf(orderDetailJson.getString(APP_FEE_VAT)));
                paylistViewHolder.vat_percent.setText(String.valueOf(orderDetailJson.getString(VAT_PERCENT)));
                paylistViewHolder.refund_amt.setText(String.valueOf(orderDetailJson.getString(REFUND_AMOUNT)));
                paylistViewHolder.final_amt.setText(String.valueOf(orderDetailJson.getString(FINAL_AMOUNT)));

                if (orderDetailJson.getString(REFUND_AMOUNT).equalsIgnoreCase("0")) {
                    paylistViewHolder.refundAmountLl.setVisibility(View.GONE);
                    paylistViewHolder.finalAmountLl.setVisibility(View.GONE);
                } else {
                    paylistViewHolder.refundAmountLl.setVisibility(View.VISIBLE);
                    paylistViewHolder.finalAmountLl.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("@@PAYMENT_LIST_ERROR", e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return payListDataArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        int result;
        if (payListDataArray.get(position).getIsDate()) {
            result = VIEW_DATE;
        } else {
            result = VIEW_PAYLIST;
        }
        return result;
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        private SmartTextView dateTv;

        DateViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.date_tv);
        }
    }

    public class PaylistViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imagePaylistIv;

        private SmartTextView orderNoPaymentTv;
        private SmartTextView namePaylistTv;
        private SmartTextView addressPaylistTv;
        private SmartTextView totalAmountTv;
        private SmartTextView app_fee_vat_val;
        private SmartTextView app_fee_val;
        private SmartTextView vat_percent;
        private SmartTextView refund_amt;
        private SmartTextView final_amt;

        private LinearLayout refundAmountLl;
        private LinearLayout finalAmountLl;


        PaylistViewHolder(View itemView) {
            super(itemView);
            totalAmountTv = itemView.findViewById(R.id.total_amount_tv);
            app_fee_vat_val = itemView.findViewById(R.id.app_fee_vat_val);
            app_fee_val = itemView.findViewById(R.id.app_fee_val);
            vat_percent = itemView.findViewById(R.id.vat_percent);
            refund_amt = itemView.findViewById(R.id.refund_amount);

            final_amt = itemView.findViewById(R.id.txtFinalAmount);
            refundAmountLl = itemView.findViewById(R.id.llRefundAmount);
            finalAmountLl = itemView.findViewById(R.id.llFinalAmt);


            imagePaylistIv = itemView.findViewById(R.id.image_paylist_iv);

            orderNoPaymentTv = itemView.findViewById(R.id.order_no_payment_tv);
            namePaylistTv = itemView.findViewById(R.id.name_paylist_tv);
            addressPaylistTv = itemView.findViewById(R.id.address_paylist_tv);

        }

    }
}
