package com.team5667;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class supperScout extends tab {
    EditText comment;
    EditText name;
    MainActivity mainActivity;
    EditText team;
    Button enter;
    public supperScout(String tabText, int layout) {
        super(tabText, layout);
    }
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
        mainActivity=(MainActivity)getActivity();
        enter = view.findViewById(R.id.EnterSuperScout);
        name = view.findViewById(R.id.SSName);
        team = view.findViewById(R.id.SSTeam);
        comment=view.findViewById(R.id.SSComment);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.textNotEmpty(name)&&MainActivity.textNotEmpty(comment)&&MainActivity.textNotEmpty(team)){
                    mainActivity.pushData(mainActivity.pushSuperScout(comment.getText().toString(),team.getText().toString(),name.getText().toString(),getContext()));
                    comment.setText("");
                    name.setText("");
                    team.setText("");
                }
                else {
                    Toast.makeText(getContext(),"Please Ensure All Fields Have Values",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

}
