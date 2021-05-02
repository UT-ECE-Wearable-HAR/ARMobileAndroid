package com.utexas.activityrecognition.ui.main.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;
import com.utexas.activityrecognition.ui.main.ui.inferences.InferencesListView;

public class SessionsListAdapter extends RecyclerView.Adapter<SessionsListAdapter.MyViewHolder> {

    String[] names, timestamps;
    int[] imgs;
    int[] ids;
    Context context;

    public SessionsListAdapter(Context ct, int[] ids, String[] names, String[] timestamps, int[] imgs){
        context = ct;
        this.ids = ids;
        this.names = names;
        this.timestamps = timestamps;
        this.imgs = imgs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sessions_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.sessionName.setText(names[position]);
        holder.timestamp.setText(timestamps[position]);
        holder.image.setImageResource(imgs[position]);
        holder.sessionId = ids[position];
        holder.itemView.setOnClickListener((View v)-> {
            Toast.makeText(v.getContext(), ids[position] + "", Toast.LENGTH_SHORT).show();
            Intent showInferences = new Intent(v.getContext(), InferencesListView.class);
            showInferences.putParcelableArrayListExtra(InferencesListView.INFERENCES_LIST, RecogitionAPIImpl.getInstance().getInferences(v.getContext(), ids[position]));
            v.getContext().startActivity(showInferences);
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView sessionName, timestamp;
        ImageView image;
        int sessionId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionName = itemView.findViewById(R.id.sessionNameText);
            timestamp = itemView.findViewById(R.id.timestampText);
            image = itemView.findViewById(R.id.framePreviewImage);
        }
    }
}
