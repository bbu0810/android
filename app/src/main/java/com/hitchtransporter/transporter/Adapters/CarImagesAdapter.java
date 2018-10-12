package com.hitchtransporter.transporter.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hitchtransporter.R;
import com.hitchtransporter.transporter.Interfaces.ProfileView;
import com.hitchtransporter.transporter.POJO.ObjectEditImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.hitchtransporter.smart.framework.Constants.WEB;

public class CarImagesAdapter extends RecyclerView.Adapter<CarImagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ObjectEditImage> carImgs;
    private boolean inUpdateState;


    private ProfileView.CarImageImplementation carImageImplementation;


    public CarImagesAdapter(Context context, ArrayList<ObjectEditImage> carImgs, boolean inUpdateState) {
        this.context = context;
        this.carImgs = carImgs;
        this.inUpdateState = inUpdateState;
    }

    @NonNull
    @Override
    public CarImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_car_image, viewGroup, false);
        return new CarImagesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        if (carImgs.get(position).getIsStarter() == 0) {
            holder.carImageIv.setImageResource(R.drawable.ic_upload_images);
            holder.removeCarIv.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carImageImplementation != null) carImageImplementation.onCarImageSelector();
                }
            });
        } else if (carImgs.get(position).getIsStarter() != 0) {
            holder.removeCarIv.setVisibility(View.VISIBLE);
            if (carImgs.get(position).getImageType().equalsIgnoreCase(WEB)) {
                if (!TextUtils.isEmpty(carImgs.get(position).getImageUrl())) {
                    Picasso.with(context).load(carImgs.get(position).getImageUrl()).placeholder(R.drawable.car_placeholder).into(holder.carImageIv);
                }
            } else {
                Picasso.with(context).load(new File(carImgs.get(position).getImageUrl())).into(holder.carImageIv);
            }
        }

        if (inUpdateState && carImgs.get(position).getIsStarter() != 0) {
            holder.removeCarIv.setVisibility(View.VISIBLE);
            holder.removeCarIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carImageImplementation != null)
                        carImageImplementation.onCarRemoveListener(position);
                }
            });
        } else {
            holder.removeCarIv.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return carImgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView carImageIv;
        private ImageView removeCarIv;


        public ViewHolder(View itemView) {
            super(itemView);

            carImageIv = itemView.findViewById(R.id.car_image_iv);
            removeCarIv = itemView.findViewById(R.id.remove_car_iv);
        }
    }

    public void setCarAdapterData(ArrayList<ObjectEditImage> objectEditImages, boolean inUpdateState) {
        this.carImgs = objectEditImages;
        this.inUpdateState = inUpdateState;
        notifyDataSetChanged();

    }

    public void setCarSelectListener(ProfileView.CarImageImplementation carImageImplementation) {
        this.carImageImplementation = carImageImplementation;
    }
}
