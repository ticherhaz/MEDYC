package com.ashrof.medyc.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.ashrof.medyc.R;

public class MedicinesFragment extends Fragment {

    private View root;
    private Button buttonAdd;

    public MedicinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_medicines, container, false);
        initView();
        return root;
    }

    private void initView() {
        buttonAdd = root.findViewById(R.id.btn_add);
        buttonAdd.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddMedicinesActivity.class)));
    }
}