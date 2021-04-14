package com.utexas.activityrecognition.ui.main.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;

public class InferencesListAdapter extends RecyclerView.Adapter<InferencesListAdapter.MyViewHolder> {

    String[] names, timestamps;
    int[] imgs;
    Context context;

    public InferencesListAdapter(Context ct, String[] names, String[] timestamps, int[] imgs){
        context = ct;
        this.names = names;
        this.timestamps = timestamps;
        this.imgs = imgs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inferences_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.inferenceName.setText(names[position]);
        holder.timestamp.setText(timestamps[position]);
        holder.image.setImageResource(imgs[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView inferenceName, timestamp;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inferenceName = itemView.findViewById(R.id.inferenceNameText);
            timestamp = itemView.findViewById(R.id.timestampText);
            image = itemView.findViewById(R.id.framePreviewImage);
        }
    }
}
