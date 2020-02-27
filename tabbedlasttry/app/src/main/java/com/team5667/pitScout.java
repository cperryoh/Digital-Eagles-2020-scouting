package com.team5667;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;


import java.util.ArrayList;

public class pitScout extends tab {
    public pitScout(String tabText, int layout) {
        super(tabText, layout);
    }

    EditText teamNumber;
    EditText driveTrain;
    Spinner distance;
    Spinner capacity;
    MainActivity activity;
    EditText autoCount;
    EditText comments;
    EditText name;
    ArrayList<checkBoxGroup> groups = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.pit_scouting, container, false);

        //gets edit text boxes
        teamNumber = (EditText) view.findViewById(R.id.teamNumberPS);
        distance = view.findViewById(R.id.distance);
        driveTrain=view.findViewById(R.id.drive_train);
        autoCount = view.findViewById(R.id.autoCount);
        capacity = view.findViewById(R.id.capacity);
        //makes check box groups
        activity = (MainActivity) getActivity();
        groups = checkBoxGroup.generateGroups((LinearLayout) view.findViewById(R.id.mainWindow), getContext());
        comments = (EditText) view.findViewById(R.id.Comments);
        name = view.findViewById(R.id.name);
        //gets enter button
        final Button enter = (Button) view.findViewById(R.id.enter);

        //on click listener for enter button
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.textNotEmpty(teamNumber) && MainActivity.checkSpinnerChange(capacity) &&MainActivity.textNotEmpty(name)&&activity.checkurls(getContext())&&MainActivity.textNotEmpty(autoCount)&&MainActivity.checkSpinnerChange(distance)&&MainActivity.textNotEmpty(driveTrain)) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("Are you sure you would like to push the data, as it will clear all fields?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ArrayList<ArrayList<String>> rawData = checkBoxGroup.pullCheckBoxData(groups);/*tags and data from all check boxes*/
                                    final ArrayList<String> tags = new ArrayList<>();
                                    final ArrayList<String> values = new ArrayList<>();/*array of values from singular pieces of data*/

                                    tags.add("Team");
                                    values.add(teamNumber.getText().toString());

                                    tags.add("capacity");
                                    values.add(capacity.getSelectedItem().toString());

                                    tags.add("Scouter name");
                                    values.add(name.getText().toString());

                                    tags.add("PC count during auto");
                                    values.add(autoCount.getText().toString());

                                    tags.add("Shooting Distance");
                                    values.add(distance.getSelectedItem().toString());

                                    activity.pushData(activity.addData(MainActivity.mergeArrays(values, rawData.get(1)), MainActivity.mergeArrays(tags, rawData.get(0)), "Pit scouting"));
                                    if (!comments.getText().toString().equals("")) {
                                        activity.pushData(activity.pushComment(comments.getText().toString(),driveTrain.getText().toString(), teamNumber.getText().toString(), name.getText().toString(), getContext()));
                                    }
                                    clearAll();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    checkTeamNum(getContext());
                }
            }
        });
        return view;
    }

    //is team number empty
    public static void checkTeamNum(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Missing information")
                .setMessage("Please ensure all fields at the top have information entered.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {/*do nothing on click*/ }
                }).show();
    }

    //clears python
    void clearAll() {
        for (int i = 0; i < groups.size(); i++) {
            for (int x = 0; x < groups.get(i).checkBoxes.size(); x++) {
                groups.get(i).checkBoxes.get(x).setChecked(false);
            }
            if(groups.get(i).specialBox!=null) {
                groups.get(i).specialBox.setChecked(false);
            }
        }
        distance.setSelection(0);
        autoCount.setText("");
        driveTrain.setText("");
        teamNumber.setText("");
        capacity.setSelection(0);
        name.setText("");
        comments.setText("");
    }


}
