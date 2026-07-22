package com.maheswara660.chrono;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.LapViewHolder> {

    private final List<LapItem> lapList;

    public LapAdapter(List<LapItem> lapList) {
        this.lapList = lapList;
    }

    @NonNull
    @Override
    public LapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lap, parent, false);
        return new LapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LapViewHolder holder, int position) {
        LapItem item = lapList.get(position);
        holder.tvLapNumber.setText(item.getLapNumberFormatted());
        holder.tvLapSplit.setText(item.getSplitTimeFormatted());
        holder.tvLapTotal.setText(item.getTotalTimeFormatted());
    }

    @Override
    public int getItemCount() {
        return lapList.size();
    }

    public static class LapViewHolder extends RecyclerView.ViewHolder {
        TextView tvLapNumber;
        TextView tvLapSplit;
        TextView tvLapTotal;

        public LapViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLapNumber = itemView.findViewById(R.id.tv_lap_number);
            tvLapSplit = itemView.findViewById(R.id.tv_lap_split);
            tvLapTotal = itemView.findViewById(R.id.tv_lap_total);
        }
    }
}
