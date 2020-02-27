package com.team5667;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

public class colorWheel extends tab {
    ArrayList<checkBoxGroup> groups = new ArrayList<>();

    public colorWheel(String tabText, int layout) {
        super(tabText, layout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 2;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    void reset() {
        for (int i = 0; i < groups.size(); i++) {
            for (int x = 0; x < groups.get(i).checkBoxes.size(); x++) {
                groups.get(i).checkBoxes.get(x).setChecked(false);
            }
            groups.get(i).specialBox.setChecked(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        //grabs the one check box group in window
        groups = checkBoxGroup.generateGroups((LinearLayout) view, getContext());
        return view;
    }
}
