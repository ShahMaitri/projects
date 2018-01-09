package com.project.presentme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.presentme.R;
import com.project.presentme.adapters.HistoryAdapter;
import com.project.presentme.utils.DatabaseUtils;
import com.project.presentme.utils.Distance;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    private View view;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        initUI();
        return view;
    }

    private void initUI () {
        DatabaseUtils databaseUtils = new DatabaseUtils(getActivity());
        ListView lv = view.findViewById(R.id.lvDistance);
        TextView tv = view.findViewById(R.id.tvNoData);
        HistoryAdapter adapter = new HistoryAdapter(getActivity(), databaseUtils.getDistances());
        lv.setAdapter(adapter);

        if (databaseUtils.getDistances().size() > 0) {
            tv.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }
    }
}
