package com.team5667;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

public class loadPort extends tab {
    match parent;
    timer run;

    public int total;
    private final int maxTime;
    MainActivity activity;

    public loadPort(String tabText, int layout, timer run, int maxTime) {
        super(tabText, layout);
        this.run = run;
        this.maxTime = maxTime;
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

    //loads power cell shot into array to be pushed to data base in post match
    void powerCellShot(final String tag) {
        final boolean sandstorm = run.getTime() >= maxTime - 15;
        addAndPushData(sandstorm, tag);
        Toast.makeText(getContext(), "Data tracked", Toast.LENGTH_SHORT).show();

        calculatePoints(sandstorm, getVal(tag));
    }

    int getVal(String tag) {
        if (!tag.equals("missed")) {
            if (tag.equals("Upper")) {
                return 2;
            } else {
                return 1;
            }
        }
        return -1;
    }

    void calculatePoints(boolean sandstorm, int val) {
        if (val != -1) {
            total += sandstorm ? val * 2 : val;
        }
    }

    void addAndPushData(boolean sandstorm, String tag) {

        if(!tag.equals("missed")){
            if (tag.equals("Upper")) {
                activity.postmatchFrag.upperShots++;
            } else if (tag.equals("Lower")) {
                activity.postmatchFrag.lowerShots++;
            }

            activity.postmatchFrag.sandStormShots = sandstorm ? activity.postmatchFrag.sandStormShots + 1 : activity.postmatchFrag.sandStormShots;
        }
        else {
            activity.postmatchFrag.missedShots++;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        parent = (match) getParentFragment().getParentFragment();
        activity = (MainActivity) getActivity();
        //fetches all the buttons
        Button one = view.findViewById(R.id.LevelOne);
        Button two = view.findViewById(R.id.LevelTwo);
        Button three = view.findViewById(R.id.LevelThree);

        //adds a general on click listener for the buttons
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String tag = button.getTag().toString();
                powerCellShot(tag);
            }
        };
        one.setOnClickListener(clickListener);
        two.setOnClickListener(clickListener);
        three.setOnClickListener(clickListener);
        return view;
    }
}