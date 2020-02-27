package com.team5667;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class match extends tab {
    public timer run;
    CheckBox crossed;
    public ProgressBar progressBar;
    public final int maxTime = 150;

    TabLayout tabs;
    loadPort loadPortFrag;
    colorWheel colorWheelFrag;
    Handler handler = new Handler();
    TextView timeBox;

    public match(String tabText, int layout) {
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

    //fuck this, it does some shit
    //tbh tho it resets the progress bar and match scouting windows
    public void reset() {
        progressBar.setProgress(100);
        //progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        run.setTime(maxTime);
        crossed.setChecked(false);
        loadPortFrag.total=0;
        tabs.getTabAt(0).select();
        crossed.setEnabled(true);
        setTabs(false,1);
        colorWheelFrag.reset();
        timeBox.setText(Integer.toString(maxTime));
    }
    void setTabs(boolean status, int index) {
        LinearLayout tabStrip = ((LinearLayout) tabs.getChildAt(0));
        tabStrip.getChildAt(index).setClickable(status);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.match, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tabs=view.findViewById(R.id.nTab);
        final MainActivity mainActivity = (MainActivity) getActivity();
        Button reset = view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ((MainActivity)getActivity()).resetMatch();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing;
                    }
                }).show();
            }
        });

        crossed=view.findViewById(R.id.crossed);
        Button end =view.findViewById(R.id.end);
        //on click listener for the end button
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(run.getTime()<=0){
                    mainActivity.tabs.getTabAt(MainActivity.postMatchTab).select();
                    mainActivity.matchStarted=false;
                    mainActivity.postmatchFrag.leftDuringAuto=crossed.isChecked();
                }
            }
        });
        //progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progressBar.setProgress(progressBar.getMax());
        timeBox = (TextView) view.findViewById(R.id.time);
        timeBox.setText(Integer.toString(maxTime));

        //timer
        run = new timer(maxTime) {

            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                if (mainActivity.matchStarted && time > 0) {
                    time -= 1;
                    progressBar.setProgress((int) ((double) progressBar.getMax() * ((double) time / maxTime)));
                    timeBox.setText(Integer.toString(time));
                    double precentageDone = (double) progressBar.getProgress() / (double) progressBar.getMax();
                    //under 1/3 left
                    if (precentageDone <= (1.0 / 3.0)) {
                        //progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    }
                    //under 2/3 above 1/3
                    else if (precentageDone <= (2.0 / 3.0)) {
                        //progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                    }
                    //above 2/3
                    else {
                        //progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    if(time<=maxTime-15){
                        crossed.setEnabled(false);
                        setTabs(true,1);
                    }
                }
            }
        };
        run.run();
        //sets up nested fragments and tabs and pager
        loadPortFrag = new loadPort("Load port", R.layout.load_port, run, maxTime);
        colorWheelFrag = new colorWheel("Color wheel", R.layout.color_wheel);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(view.getContext(), getChildFragmentManager(), new tab[]{loadPortFrag, colorWheelFrag});
        final TabLayout tabs = (TabLayout) view.findViewById(R.id.nTab);
        ViewPager viewPager = view.findViewById(R.id.NPager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        viewPager.beginFakeDrag();
        setTabs(false,1);
        return view;
    }
    public int getPoints(){
        int overAll = loadPortFrag.total;
        overAll+= MainActivity.CheckedPointsVal(colorWheelFrag.groups.get(0).checkBoxes.get(0),10);
        overAll+=MainActivity.CheckedPointsVal(crossed,5);
        overAll+= MainActivity.CheckedPointsVal(colorWheelFrag.groups.get(0).checkBoxes.get(1),20);
        return overAll;
    }
}
