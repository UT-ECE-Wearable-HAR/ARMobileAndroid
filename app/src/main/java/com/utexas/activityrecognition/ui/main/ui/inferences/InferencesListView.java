package com.utexas.activityrecognition.ui.main.ui.inferences;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.data.model.Inference;

import java.util.ArrayList;
import java.util.Date;

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
        ArrayList<Inference> inferences = getIntent().getParcelableArrayListExtra(INFERENCES_LIST);
        Date[] demoTimestamps = {new Date(1618360856000L), new Date(1618361756000L), new Date(1618363256000L), new Date(1618363856000L), new Date(1618364276000L)};
        int[] demoImgs = {R.drawable.studying, R.drawable.napping, R.drawable.computer, R.drawable.typing, R.drawable.chores};

        String[] demoNames = new String[demoImgs.length];
        for(int i = 0; i < demoImgs.length; i++){
            demoNames[i] = "Inference " + i;
        }

        String[] demoTimestampStrings = new String[demoTimestamps.length];
        for(int i = 0; i < demoTimestampStrings.length; i ++){
            String d = demoTimestamps[i].toString();
            String[] dateParts = d.split(":");
            demoTimestampStrings[i] = dateParts[0] + ":" + dateParts[1];;
        }

        InferencesListAdapter adapter = new InferencesListAdapter(this, demoNames, demoTimestampStrings, demoImgs);
        inferencesList.setAdapter(adapter);
        inferencesList.setLayoutManager(new LinearLayoutManager(this));

    }
}
