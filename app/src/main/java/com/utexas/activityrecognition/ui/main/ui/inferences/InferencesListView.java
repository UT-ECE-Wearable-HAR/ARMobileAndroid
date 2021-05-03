package com.utexas.activityrecognition.ui.main.ui.inferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.data.model.Inference;

import java.util.Arrays;

public class InferencesListView extends AppCompatActivity {

    public static String INFERENCES_LIST = "INFERENCES_LIST";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inferences_list);
        final RecyclerView inferencesList = findViewById(R.id.inferencesListView);
        populateInferencesList(inferencesList);
    }

    public void populateInferencesList(RecyclerView inferencesList){
        Parcelable[] parcelableInferences = getIntent().getParcelableArrayExtra(INFERENCES_LIST);
        Inference[] inferences = null;
        if (parcelableInferences != null) {
            inferences = Arrays.copyOf(parcelableInferences, parcelableInferences.length, Inference[].class);
        }
//        Date[] demoTimestamps = {new Date(1618360856000L), new Date(1618361756000L), new Date(1618363256000L), new Date(1618363856000L), new Date(1618364276000L)};
//        int[] demoImgs = {R.drawable.studying, R.drawable.napping, R.drawable.computer, R.drawable.typing, R.drawable.chores};

        String[] inferenceNames = new String[inferences.length];
        SharedPreferences sp = this.getSharedPreferences("inferenceInfo", Context.MODE_PRIVATE);
        for(int i = 0; i < inferences.length; i++){
            int activityId = inferences[i].getActivityId();
            inferenceNames[i] = sp.getString("activity" + activityId + "Name", "Activity " + activityId);
        }

        InferencesListAdapter adapter = new InferencesListAdapter(this, inferenceNames, inferences);
        inferencesList.setAdapter(adapter);
        inferencesList.setLayoutManager(new LinearLayoutManager(this));

    }
}
