package com.maizic.maizic.adpter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.maizic.maizic.R;
import com.maizic.maizic.roomdatabase.RoomModel;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.DealViewHolder> {

    private List<RoomModel> list;
    private onClicked onClick;

    public CameraAdapter( onClicked onClick){
        this.list = list;

    }

    public void SetData(List<RoomModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_camera_layout, parent, false);
        return new DealViewHolder(view);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

        holder.deviceID.setText(list.get(position).getDeviceID());
    }

    @Override
    public int getItemCount() {
        if (list==null)
            return 0;
        return list.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView deviceID,deviceName,devicePass;
        public ImageView clickItem;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);

            clickItem = itemView.findViewById(R.id.item_face);
            deviceID=itemView.findViewById(R.id.ItemTitleName);

            clickItem.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onClick.onItemClicked(getAdapterPosition(),list);
        }
    }


    public interface onClicked{
        void onItemClicked(int position, List<RoomModel> list);
    }
}