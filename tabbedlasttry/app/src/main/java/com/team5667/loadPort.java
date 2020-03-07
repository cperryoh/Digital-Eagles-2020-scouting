package com.team5667;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

public class loadPort extends tab {
    match parent;
    timer run;
    ImageView port;

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

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        parent = (match) getParentFragment().getParentFragment();
        port=view.findViewById(R.id.loadPort);
        port.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int test = event.getPointerCount();
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int[] viewCoords = new int[2];
                port.getLocationInWindow(viewCoords);
                int imageX = x- viewCoords[0]; // viewCoords[0] is the X coordinate
                int imageY =y- viewCoords[1];
                Drawable draw = getResources().getDrawable(R.drawable.map);
                Bitmap bitmap = ((BitmapDrawable)draw).getBitmap();
                int pixel = bitmap.getPixel(imageX,imageY);
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);
                Toast.makeText(getContext(),redValue+","+greenValue+","+blueValue,Toast.LENGTH_SHORT).show();

                return false;
            }
        });
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