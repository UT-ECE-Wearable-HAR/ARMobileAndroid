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
import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;
import com.utexas.activityrecognition.data.model.Session;

import org.json.JSONException;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        final RecyclerView sessionsList = root.findViewById(R.id.sessionsListView);
        final Button refreshButton = root.findViewById(R.id.refreshSessionsButton);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        populateSessionsList(sessionsList);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onClickRefresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return root;
    }

    public void populateSessionsList(RecyclerView sessionsList){
//        int[] sessionIds = {0,1,2,3,4};
//        Date[] sessionTimestamps = {new Date(1618360856000L), new Date(1618361756000L), new Date(1618363256000L), new Date(1618363856000L), new Date(1618364276000L)};
//        int[] sessionImgs = {R.drawable.studying, R.drawable.napping, R.drawable.computer, R.drawable.typing, R.drawable.chores};

        SessionsListAdapter adapter = new SessionsListAdapter(this.getActivity(), Session.getAllSessions(getContext()));
        sessionsList.setAdapter(adapter);
        sessionsList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

    }

    public void onClickRefresh() throws JSONException {
        //TODO add some loading symbol to UI
        Session.saveSession(getContext(), RecogitionAPIImpl.getInstance().getSession(getContext()));
        RecyclerView sessionsList = (RecyclerView) getView().findViewById(R.id.sessionsListView);
        populateSessionsList(sessionsList);
    }
}