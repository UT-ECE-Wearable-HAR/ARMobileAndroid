package com.utexas.activityrecognition.ui.main.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utexas.activityrecognition.R;

import java.util.Date;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        final RecyclerView inferencesList = root.findViewById(R.id.inferencesListView);
        final Button refreshButton = root.findViewById(R.id.refreshInferencesButton);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        populateInferencesList(inferencesList);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRefresh();
            }
        });
        return root;
    }

    public void populateInferencesList(RecyclerView inferencesList){
        String[] demoNames = {"Activity 1", "Activity 2", "Activity 3", "Activity 4", "Activity 5"};
        Date[] demoTimestamps = {new Date(1618360856000L), new Date(1618361756000L), new Date(1618363256000L), new Date(1618363856000L), new Date(1618364276000L)};
        int[] demoImgs = {R.drawable.studying, R.drawable.napping, R.drawable.computer, R.drawable.typing, R.drawable.chores};

        String[] demoTimestampStrings = new String[demoTimestamps.length];
        for(int i = 0; i < demoTimestampStrings.length; i ++){
            String d = demoTimestamps[i].toString();
            String[] dateParts = d.split(":");
            demoTimestampStrings[i] = dateParts[0] + ":" + dateParts[1];;
        }

        InferencesListAdapter adapter = new InferencesListAdapter(this.getActivity(), demoNames, demoTimestampStrings, demoImgs);
        inferencesList.setAdapter(adapter);
        inferencesList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

    }

    public void onClickRefresh(){
        RecyclerView inferencesList = (RecyclerView) getView().findViewById(R.id.inferencesListView);
        inferencesList.setVisibility(View.VISIBLE);
    }
}