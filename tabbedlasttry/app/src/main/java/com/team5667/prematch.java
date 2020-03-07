package com.team5667;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

public class prematch extends tab {

    View view;
    public EditText teamNumber;
    public EditText name;
    public EditText roundNumber;

    public prematch(String tabText, int layout) {
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

    public String getName() {
        return name.getText().toString();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prematch, container, false);
        final Button button = view.findViewById(R.id.button);

        teamNumber = view.findViewById(R.id.teamNum);
        name = view.findViewById(R.id.prematchName);
        roundNumber = view.findViewById(R.id.roundNum);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (ready()) {
                    mainActivity.startMatch();
                    mainActivity.storeVal("web", mainActivity.setupWindowFrag.webAppUrl.getText().toString());
                    mainActivity.storeVal("sheet", mainActivity.setupWindowFrag.sheetUrl.getText().toString());

                } else {
                    Toast.makeText(getContext(), "Please ensure all values have been filled out", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    //checks if the user is ready to move to the next window
    boolean ready() {
        return MainActivity.textNotEmpty(teamNumber) && MainActivity.textNotEmpty(roundNumber);
    }

    public void reset() {
        roundNumber.setText("");
        teamNumber.setText("");
        name.setText("");
    }

    public int GetRoundNumber() {
        return Integer.parseInt(roundNumber.getText().toString());
    }

    public int GetTeamNumber() {
        return Integer.parseInt(teamNumber.getText().toString());
    }
}

