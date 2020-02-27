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
        activity.postmatchFrag.hasMatchData=true;

        //add shot normally
        if (run.getTime() > 0) {
            addAndPushData(sandstorm,tag,run.getTime(),getVal(tag,sandstorm));
            Toast.makeText(getContext(), "Data tracked", Toast.LENGTH_SHORT).show();

            calculatePoints(tag,sandstorm);
        } else {
            //add shot post match
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Add power cell post match");

            // Set up the input
            final EditText input = new EditText(builder.getContext());
            input.setHint("What time was it placed at?");

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            //brings up dialog box and prompts user
            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (MainActivity.textNotEmpty(input)) {
                        int time = Integer.parseInt(input.getText().toString());
                        boolean sandstorm = time >= maxTime - 15;
                        addAndPushData(sandstorm, tag,time,getVal(tag,sandstorm));
                        calculatePoints(tag,sandstorm);
                        Toast.makeText(getContext(), "Data tracked", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { /*Do nothing on click*/}});
            builder.show();
        }
    }
    int getVal(String tag,boolean sandstorm){
        int value;
        if (tag.equals("inner")) {
            value = 3;
        } else if (tag.equals("Outer")) {
            value = 2;
        } else {
            value = 1;
        }
        return sandstorm ? value * 2 : value;
    }
    void calculatePoints(String tag,boolean sandstorm){
        int value;
        if (tag.equals("inner")) {
            value = 3;
        } else if (tag.equals("Outer")) {
            value = 2;
        } else {
            value = 1;
        }
        total += sandstorm ? value * 2 : value;
    }
    void addAndPushData(boolean sandstorm, String tag,int time,int point) {
        ArrayList<String> data = new ArrayList<>();
        data.add(Integer.toString(activity.getTeamNumber()));
        data.add(Integer.toString(activity.getRoundNumer()));
        data.add(Integer.toString(time));
        data.add(Boolean.toString(sandstorm));
        data.add(tag);
        data.add(Integer.toString(point));
        //adds match to header data
        activity.postmatchFrag.dataMatch.add(data);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        parent = (match) getParentFragment().getParentFragment();
        activity = (MainActivity) getActivity();
        //fetches all the buttons
        Button one =view.findViewById(R.id.LevelOne);
        Button two =view.findViewById(R.id.LevelTwo);
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