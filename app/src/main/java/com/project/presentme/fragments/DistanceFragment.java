package com.project.presentme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.presentme.R;
import com.project.presentme.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class DistanceFragment extends Fragment {

    private TextView tvDistance;
    private View view;

    public DistanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_distance, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        tvDistance = view.findViewById(R.id.distance);
        tvDistance.setText(String.valueOf(Constants.DISTANCE));
    }


}
