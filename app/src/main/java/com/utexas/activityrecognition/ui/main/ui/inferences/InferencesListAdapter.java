package com.utexas.activityrecognition.ui.main.ui.inferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.data.model.Inference;

public class InferencesListAdapter extends RecyclerView.Adapter<InferencesListAdapter.MyViewHolder> {

    String[] names;
    Inference[] inferences;
    Context context;

    public InferencesListAdapter(Context ct, String[] names, Inference[] inferences){
        context = ct;
        this.names = names;
        this.inferences = inferences;
    }

    @NonNull
    @Override
    public InferencesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inferences_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InferencesListAdapter.MyViewHolder holder, int position) {
        holder.inferenceName.setText(names[position]);
        holder.timestamp.setText(inferences[position].getStartTimeString());
        holder.image.setImageBitmap(inferences[position].getImgBitmap());
        holder.itemView.setOnClickListener((View v)-> {
            Toast.makeText(v.getContext(), inferences[position].getId() + "", Toast.LENGTH_SHORT).show();
        });
        holder.edit_button.setOnClickListener((View v)-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(v.getContext().getString(R.string.prompt_name_activity));
            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sp = context.getSharedPreferences(Inference.INFERENCE_STORE_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putString("activity" + inferences[position].getActivityId() + "Name", input.getText().toString());
                    spEditor.apply();
                    holder.inferenceName.setText(input.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return inferences.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView inferenceName, timestamp;
        ImageView image;
        Button edit_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inferenceName = itemView.findViewById(R.id.inferenceNameText);
            timestamp = itemView.findViewById(R.id.timestampText);
            image = itemView.findViewById(R.id.framePreviewImage);
            edit_button = itemView.findViewById(R.id.edit_button);
        }
    }
}
