package com.hitchtransporter.transporter.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitchtransporter.R;
import com.hitchtransporter.smart.customViews.RoundedImageView;
import com.hitchtransporter.smart.customViews.SmartButton;
import com.hitchtransporter.smart.customViews.SmartTextView;
import com.hitchtransporter.smart.framework.AlertMagnatic;
import com.hitchtransporter.smart.framework.SmartUtils;
import com.hitchtransporter.transporter.Activities.OrderDetails;
import com.hitchtransporter.transporter.Activities.TrackerHitcher;
import com.hitchtransporter.transporter.HomeFragments.TransporterHome;
import com.hitchtransporter.transporter.Interfaces.OrderHistoryImple;
import com.hitchtransporter.transporter.Interfaces.TransporterHomeImple;
import com.hitchtransporter.transporter.APIsClasses.OrderAPIs;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.Constants.*;
import static com.hitchtransporter.smart.framework.SmartUtils.dateFormatChanger;
import static com.hitchtransporter.smart.framework.SmartUtils.decimalFormatter;
import static com.hitchtransporter.smart.framework.SmartUtils.extractDate;
import static com.hitchtransporter.smart.framework.SmartUtils.extractTime;
import static com.hitchtransporter.smart.framework.SmartUtils.timeFormatChanger3;

public class TransporterHomeAdapter extends RecyclerView.Adapter<TransporterHomeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<JSONObject> orderDetailArray;

    private Double currentLatitude;
    private Double currentLongitude;
    private TransporterHome transporterHome = new TransporterHome();

    private OrderHistoryImple.OrderStartImplementation orderStartImplementation;
    private OrderHistoryImple.OrderFinishImplementation orderFinishImplementation;
    private OrderHistoryImple.OrderCancelImplementation orderCancelImplementation;
    private TransporterHomeImple transporterHomeImple;


    public TransporterHomeAdapter(Context context, ArrayList<JSONObject> orderDetailArray, Double currentLatitude, Double currentLongitude) {
        this.context = context;
        this.orderDetailArray = orderDetailArray;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;

    }


    @NonNull
    @Override
    public TransporterHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_order_list, viewGroup, false);
        return new TransporterHomeAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        try {
            holder.orderNoHomeTv.setText(orderDetailArray.get(position).getString(ORDER_ID));


            if (!orderDetailArray.get(position).getString(PROFILE_IMAGE).equalsIgnoreCase("")) {
                Picasso.with(context).load(orderDetailArray.get(position).getString(PROFILE_IMAGE)).placeholder(R.drawable.profile_grey).into(holder.imageOrderlistIv);
            }
            holder.nameOrderlistTv.setText(orderDetailArray.get(position).getString(USER_NAME));
            holder.pickupOrderlistTv.setText(orderDetailArray.get(position).getString(PICKUP_ADDRESS));
            holder.dropoffOrderlistTv.setText(orderDetailArray.get(position).getString(DROPOFF_ADDRESS));
            holder.timeOrderlistTv.setText(timeFormatChanger3(extractTime(orderDetailArray.get(position).getString(ORDER_DATETIME))));
            holder.dateOrderlistTv.setText(dateFormatChanger(extractDate(orderDetailArray.get(position).getString(ORDER_DATETIME))));
            holder.amountOrderlistTv.setText(€ + decimalFormatter(orderDetailArray.get(position).getString(AMOUNT)));

            switch (orderDetailArray.get(position).getString(ORDER_STATUS)) {
                case STATUS_PENDING:
                    holder.cancelJobBtn.setVisibility(View.GONE);
                    holder.pickupStartjobBtn.setVisibility(View.GONE);
                    holder.orderStatusHomeTv.setText(context.getString(R.string.pending));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, OrderDetails.class);
                            try {
                                i.putExtra(ORDER_REQUEST_ID, orderDetailArray.get(position).getString(ORDER_REQUEST_ID));
                                i.putExtra(ORDER_ID, orderDetailArray.get(position).getString(ORDER_ID));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            context.startActivity(i);
                        }
                    });
                    break;

                case STATUS_APPROVED:
                    holder.cancelJobBtn.setVisibility(View.VISIBLE);

                    if (orderDetailArray.get(position).getString(IS_PAY).equalsIgnoreCase(STATUS_UNPAID)) {

                        holder.orderStatusHomeTv.setText(context.getString(R.string.approved));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, OrderDetails.class);
                                try {
                                    i.putExtra(ORDER_REQUEST_ID, orderDetailArray.get(position).getString(ORDER_REQUEST_ID));
                                    i.putExtra(ORDER_ID, orderDetailArray.get(position).getString(ORDER_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(i);
                            }
                        });
                    } else {
                        holder.orderStatusHomeTv.setText(context.getString(R.string.paid));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, TrackerHitcher.class);
                                try {
                                    i.putExtra(ORDER_REQUEST_ID, orderDetailArray.get(position).getString(ORDER_REQUEST_ID));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(i);
                            }
                        });

                        if (orderDetailArray.get(position).getString(FOR_TODAY).equalsIgnoreCase("1")) {
                            holder.pickupStartjobBtn.setVisibility(View.VISIBLE);
                            if (orderDetailArray.get(position).getString(IS_NEARBY).equalsIgnoreCase("1")) {

                                holder.pickupStartjobBtn.setText(context.getString(R.string.start_job));
                                holder.pickupStartjobBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        SmartUtils.getConfirmDialog(context, context.getString(R.string.start_job), context.getString(R.string.are_you_sure_you_want_to_start_job_),
                                                context.getString(R.string.yes), context.getString(R.string.no), true, new AlertMagnatic() {
                                                    @Override
                                                    public void PositiveMethod(DialogInterface dialog, int id) {
                                                        try {
//                                                                goToNavigation(orderDetailArray.get(position).getString(DROPOFF_LATITUDE), orderDetailArray.get(position).getString(DROPOFF_LONGITUDE));
                                                            holder.orderStartListener(orderDetailArray.get(position).getString(ORDER_ID),
                                                                    orderDetailArray.get(position).getString(ORDER_REQUEST_ID),
                                                                    String.valueOf(currentLatitude), String.valueOf(currentLongitude),
                                                                    orderDetailArray.get(position).getString(DROPOFF_LATITUDE),
                                                                    orderDetailArray.get(position).getString(DROPOFF_LONGITUDE));

                                                           /* TransporterHome transporterHome = new TransporterHome();
                                                            transporterHome.startJob(orderDetailArray.get(position).getString(ORDER_ID)
                                                                    , orderDetailArray.get(position).getString(ORDER_REQUEST_ID)
                                                                    , orderDetailArray.get(position).getString(DROPOFF_LATITUDE)
                                                                    , orderDetailArray.get(position).getString(DROPOFF_LONGITUDE));*/

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
                            } else {

                                holder.pickupStartjobBtn.setText(context.getString(R.string.go_to_pick_up_location));
                                holder.pickupStartjobBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String UrlToGive = "http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + Uri.encode("(My address 1)") +"&daddr=" + orderDetailArray.get(position).getString(PICKUP_LATITUDE) + "," + orderDetailArray.get(position).getString(PICKUP_LONGITUDE) + Uri.encode("(My address 2)");

                                            Log.e("@@url", UrlToGive);
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(UrlToGive));
                                            context.startActivity(intent);

                                        } catch (Exception e) {
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en")));
                                        }
                                    }
                                });
                            }
                        } else {
                            holder.pickupStartjobBtn.setVisibility(View.GONE);
                        }
                    }
                    break;

                case STATUS_STARTED:
                    holder.orderStatusHomeTv.setText(context.getString(R.string.dispatched));
                    holder.cancelJobBtn.setVisibility(View.VISIBLE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.goToTrackScreen(position);
                        }
                    });

                    if (orderDetailArray.get(position).getString(IS_NEARBY_FINISH).equalsIgnoreCase("1")) {
                        holder.pickupStartjobBtn.setVisibility(View.VISIBLE);
                        holder.pickupStartjobBtn.setText(context.getString(R.string.finish_job));
                        holder.pickupStartjobBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SmartUtils.getConfirmDialog(context, context.getString(R.string.finish_job), context.getString(R.string.are_you_sure_you_want_to_finish_the_job_),
                                        context.getString(R.string.yes), context.getString(R.string.no), true, new AlertMagnatic() {
                                            @Override
                                            public void PositiveMethod(DialogInterface dialog, int id) {
                                                try {
                                                    holder.orderFinishListener(orderDetailArray.get(position).getString(ORDER_ID));
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
                    } else {
                        holder.pickupStartjobBtn.setVisibility(View.GONE);
                    }
                    break;
                default:
                    holder.pickupStartjobBtn.setVisibility(View.GONE);

            }

            holder.cancelJobBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartUtils.getConfirmDialog(context, context.getString(R.string.cancel_job), context.getString(R.string.are_you_sure_you_want_to_finish_the_job_),
                            context.getString(R.string.yes), context.getString(R.string.no), true, new AlertMagnatic() {
                                @Override
                                public void PositiveMethod(DialogInterface dialog, int id) {
                                    try {
                                        holder.orderCancelListener(orderDetailArray.get(position).getString(ORDER_ID));
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

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return orderDetailArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageOrderlistIv;
        private SmartTextView nameOrderlistTv;
        private SmartTextView pickupOrderlistTv;
        private SmartTextView dropoffOrderlistTv;
        private SmartTextView timeOrderlistTv;
        private SmartTextView dateOrderlistTv;
        private SmartTextView amountOrderlistTv;
        private SmartTextView orderNoHomeTv;
        private SmartTextView orderStatusHomeTv;
        private SmartButton pickupStartjobBtn;
        private SmartButton cancelJobBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            orderNoHomeTv = itemView.findViewById(R.id.order_no_home_tv);
            orderStatusHomeTv = itemView.findViewById(R.id.order_status_home_tv);

            imageOrderlistIv = itemView.findViewById(R.id.image_orderlist_iv);
            nameOrderlistTv = itemView.findViewById(R.id.name_orderlist_tv);
            pickupOrderlistTv = itemView.findViewById(R.id.pickup_orderlist_tv);
            dropoffOrderlistTv = itemView.findViewById(R.id.dropoff_orderlist_tv);
            timeOrderlistTv = itemView.findViewById(R.id.time_orderlist_tv);
            dateOrderlistTv = itemView.findViewById(R.id.date_orderlist_tv);
            amountOrderlistTv = itemView.findViewById(R.id.amount_orderlist_tv);
            pickupStartjobBtn = itemView.findViewById(R.id.pickup_startjob_btn);
            cancelJobBtn = itemView.findViewById(R.id.cancel_home_btn);

        }

        public void goToTrackScreen(int position) {
            if (transporterHomeImple != null) {
                transporterHomeImple.goToTrackScreen(position);
            }
        }

        public void orderStartListener(String orderId, String orderRequestId, String currentLatitude, String currentLongitude, String dropOffLatitude, String dropOffLongitude) {
            if (orderStartImplementation != null) {
                OrderAPIs.onStartJob(context, orderId, orderRequestId, currentLatitude, currentLongitude, dropOffLatitude, dropOffLongitude, orderStartImplementation);
            }
        }

        public void orderFinishListener(String orderId) {
            if (orderFinishImplementation != null) {
                OrderAPIs.onFinishJob(context, orderId, orderFinishImplementation);
            }
        }

        public void orderCancelListener(String orderId) {
            if (orderCancelImplementation != null) {
                OrderAPIs.onCancelJob(context, orderId, orderCancelImplementation);
            }
        }
    }

    public void setGoToTrackScreenImplementation(TransporterHomeImple transporterHomeImple) {
        this.transporterHomeImple = transporterHomeImple;
    }

    public void setOrderStartImplementation(OrderHistoryImple.OrderStartImplementation orderStartImplementation) {
        this.orderStartImplementation = orderStartImplementation;
    }


    public void setOrderFinishImplementation(OrderHistoryImple.OrderFinishImplementation orderFinishImplementation) {
        this.orderFinishImplementation = orderFinishImplementation;
    }

    public void setOrderCancelImplementation(OrderHistoryImple.OrderCancelImplementation orderCancelImplementation) {
        this.orderCancelImplementation = orderCancelImplementation;
    }

}
