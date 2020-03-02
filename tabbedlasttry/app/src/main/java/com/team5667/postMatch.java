package com.team5667;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;


import java.util.ArrayList;

public class postMatch extends tab {
    ArrayList<checkBoxGroup> groups = new ArrayList<>();
    MainActivity activity;
    Spinner distance;
    CheckBox parked;
    Spinner climb;
    public boolean leftDuringAuto;
    public boolean hasMatchData = false;

    CheckBox balanced;
    Spinner condition;
    public ArrayList<ArrayList<String>> dataMatch = new ArrayList<>();

    public ArrayList<String> tagsMatch = new ArrayList<>();

    public postMatch(String tabText, int layout) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        balanced = view.findViewById(R.id.balancedPost);
        climb = view.findViewById(R.id.climb_spinner);
        distance = view.findViewById(R.id.distance2);
        parked = view.findViewById(R.id.parked);
        condition = view.findViewById(R.id.condition2);
        //adds tags for google sheet
        activity = (MainActivity) getActivity();

        Button button = (Button) view.findViewById(R.id.enterData);
        climb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos != 0 && pos != 1) {
                    parked.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        parked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    climb.setSelection(1);
                }
            }
        });
        //makes check box groups
        LinearLayout layout = view.findViewById(R.id.main_post);
        groups = checkBoxGroup.generateGroups(layout, getContext());

        //push of header data and match data
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setMessage("Are you sure you would like to push the data, as it will clear all fields?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //makes queue for data
                                if (MainActivity.checkSpinnerChange(condition) && MainActivity.checkSpinnerChange(climb) && MainActivity.checkSpinnerChange(distance)) {
                                    int finalScore=0;
                                    if (hasMatchData) {
                                        finalScore = activity.matchFrag.getPoints();
                                    }
                                    finalScore += MainActivity.CheckedPointsVal(parked, 5);
                                    finalScore += MainActivity.CheckedPointsVal(balanced, 15);
                                    finalScore += climb.getSelectedItemPosition() < 1 ? 25 : 0;
                                    activity = (MainActivity) getActivity();

                                    //adds color wheel to match header data
                                    groups.add(activity.matchFrag.colorWheelFrag.groups.get(0));

                                    //gets data and divdeds it into tags and data
                                    ArrayList<ArrayList<String>> rawData = checkBoxGroup.pullCheckBoxData(groups);
                                    ArrayList<String> tags = rawData.get(0);
                                    final ArrayList<String> data = rawData.get(1);

                                    //adds some data from the window
                                    tags.add("round");
                                    data.add(activity.prematchFrag.roundNumber.getText().toString());

                                    tags.add("team");
                                    data.add(activity.prematchFrag.teamNumber.getText().toString());

                                    tags.add("starting position");
                                    data.add(activity.prematchFrag.prePos.getSelectedItem().toString());

                                    tags.add("Condition");
                                    data.add(condition.getSelectedItem().toString());

                                    tags.add("Scouter name");
                                    data.add(activity.prematchFrag.getName());

                                    tags.add("Left during auto");
                                    data.add(Boolean.toString(leftDuringAuto));

                                    tags.add("Climb");
                                    data.add(climb.getSelectedItem().toString());

                                    tags.add("Shooting Distance");
                                    data.add(distance.getSelectedItem().toString());

                                    tags.add("Points scored");
                                    data.add(Integer.toString(finalScore));

                                    //tracks match data
                                    if (hasMatchData) {
                                        for (int i = 0; i < dataMatch.size(); i++) {
                                            activity.pushData(activity.addData(dataMatch.get(i), tagsMatch, "Match data"));
                                        }
                                    }

                                    activity.pushData(activity.addData(data, tags, "Header data"));
                                    activity.resetMatch();
                                } else {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Some values have not been set")
                                            .setMessage("Enter all values that apply " + activity.getTeamNumber())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {/*do nothing on click*/ }
                                            }).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        return view;
    }

    //clears all data in the check boxes
    void clearAll() {
        for (int i = 0; i < groups.size(); i++) {
            for (int x = 0; x < groups.get(i).checkBoxes.size(); x++) {

                groups.get(i).checkBoxes.get(x).setChecked(false);
            }
            if (groups.get(i).specialBox != null) {
                groups.get(i).specialBox.setChecked(false);
            }
        }
    }

    //starts the reset of the window
    void reset() {
        clearAll();
        climb.setSelection(0);
        dataMatch = new ArrayList<>();
        distance.setSelection(0);
        parked.setChecked(false);
        leftDuringAuto = false;
        hasMatchData = false;
        condition.setSelection(0);
    }

}
