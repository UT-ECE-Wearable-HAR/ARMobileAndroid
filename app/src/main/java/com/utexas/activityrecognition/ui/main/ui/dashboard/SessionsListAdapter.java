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
import com.utexas.activityrecognition.data.model.Session;
import com.utexas.activityrecognition.ui.main.ui.inferences.InferencesListView;

public class SessionsListAdapter extends RecyclerView.Adapter<SessionsListAdapter.MyViewHolder> {

    Context context;
    Session[] sessions;

    public SessionsListAdapter(Context ct, Session[] sessions){
        context = ct;
        this.sessions = sessions;
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
        String sessionName = "Session " + sessions[position].getId();
        holder.sessionName.setText(sessionName);
        holder.timestamp.setText(sessions[position].getStartTimeString());
        holder.image.setImageBitmap(sessions[position].getImgBitmap());
        holder.itemView.setOnClickListener((View v)-> {
            Toast.makeText(v.getContext(), sessions[position].getId() + "", Toast.LENGTH_SHORT).show();
            Intent showInferences = new Intent(v.getContext(), InferencesListView.class);
            showInferences.putExtra(InferencesListView.INFERENCES_LIST, sessions[position].getInferences());
            v.getContext().startActivity(showInferences);
        });
    }

    @Override
    public int getItemCount() {
        return sessions.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView sessionName, timestamp;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionName = itemView.findViewById(R.id.sessionNameText);
            timestamp = itemView.findViewById(R.id.timestampText);
            image = itemView.findViewById(R.id.framePreviewImage);
        }
    }
}
