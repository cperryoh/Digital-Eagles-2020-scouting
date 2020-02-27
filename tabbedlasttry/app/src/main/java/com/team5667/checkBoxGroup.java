package com.team5667;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team5667.R;

import java.util.ArrayList;

public class checkBoxGroup {
    public ArrayList<CheckBox> checkBoxes;
    public CheckBox specialBox;
    public String sectionName;

    Context context;
    public boolean disableBox;

    //constructer for groups with a special box
    public checkBoxGroup(ArrayList<CheckBox> boxes, final CheckBox specialBox, final boolean disableBox, String sectionName, final Context context) {
        this.specialBox = specialBox;
        this.checkBoxes = boxes;
        this.disableBox = disableBox;
        this.context = context;
        this.sectionName = sectionName;
        for (int i = 0; i < boxes.size(); i++) {
            //checkBoxes.get(i).getBackground().setTint(context.getResources().getColor(R.color.colorAccent));
            checkBoxes.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkToEnsureAll();
                }
            });
        }
        specialBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (disableBox) {
                        turnOffAll();
                    } else {
                        checkAll();
                    }
                }
            }
        });
        //specialBox.getBackground().setTint(context.getResources().getColor(R.color.colorAccent));
    }
    //no special box
    public checkBoxGroup(final ArrayList<CheckBox> boxes, String sectionName, final Context context) {
        this.checkBoxes = boxes;
        this.sectionName = sectionName;
        this.context = context;
        for (int i = 0; i < boxes.size(); i++) {
            //checkBoxes.get(i).getBackground().setTint(context.getResources().getColor(R.color.colorAccent));
        }
        if (boxes.get(0).getTag().toString().equals("radial")) {
            boxes.get(0).setChecked(true);
            CompoundButton.OnCheckedChangeListener checkListener =new  CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        turnOffAll();
                        compoundButton.setChecked(true);

                    }
                    else{
                        if(!compoundButton.getTag().toString().equals("radial")){
                            turnOffAll();
                            boxes.get(0).setChecked(true);
                        }
                    }
                }
            };
            for (CheckBox box:boxes) {
                box.setOnCheckedChangeListener(checkListener);
            }

        }
    }

    //gen groups of check boxes
    public static ArrayList<checkBoxGroup> generateGroups(LinearLayout linearLayout, Context context) {
        //loop through components in window to find groups
        ArrayList<checkBoxGroup> out = new ArrayList<>();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {

            //makes sure tag is not null
            if (linearLayout.getChildAt(i).getTag() != null) {
                //check to see if it is group
                if (linearLayout.getChildAt(i).getTag().toString().equals("Check boxes")) {
                    //gather varibles needed for the group
                    ViewGroup view = (ViewGroup) linearLayout.getChildAt(i);
                    view = (ViewGroup) view.getChildAt(0);
                    CheckBox specialBox = null;
                    boolean isDisableGroup = true;
                    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
                    String sectionName = "";
                    //loop through all check boxes and determine group type
                    for (int x = 0; x < view.getChildCount(); x++) {
                        String tag = (String) view.getChildAt(x).getTag();
                        if (view.getChildAt(x).getTag() != null) {
                            // is it the lable for the group
                            if (!tag.equals("lable")) {
                                CheckBox checkBox = (CheckBox) view.getChildAt(x);
                                if (tag.equals("disable") || tag.equals("all")) {
                                    specialBox = checkBox;
                                    isDisableGroup = tag.equals("disable");
                                } else {

                                    checkBoxes.add(checkBox);
                                }
                            } else {
                                //sets lable for said group
                                sectionName = ((TextView) view.getChildAt(x)).getText().toString();
                                ((TextView) view.getChildAt(x)).setGravity(Gravity.CENTER);
                            }
                        }

                    }
                    //determains which constructer should be used
                    if (specialBox != null) {
                        out.add(new checkBoxGroup(checkBoxes, specialBox, isDisableGroup, sectionName, context));
                    } else {
                        out.add(new checkBoxGroup(checkBoxes, sectionName, context));
                    }
                }
            }
        }
        return out;
    }

    //Checks the value of the check boxes and determains if special box should be checked
    void checkAll() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(true);
        }
    }

    //Holds the majority of logic for normal, non special check boxes
    void checkToEnsureAll() {
        if (disableBox) {
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isChecked()) {
                    specialBox.setChecked(false);
                    return;
                }
            }
        } else {
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (!checkBoxes.get(i).isChecked()) {
                    specialBox.setChecked(false);
                    return;
                }
            }
        }

        specialBox.setChecked(true);
    }

    //disables all boxes
    void turnOffAll() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(false);
        }
    }

    //convertes an array of groups to two lists of tags and a data
    public static ArrayList<ArrayList<String>> pullCheckBoxData(ArrayList<checkBoxGroup> groups) {
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            for (int x = 0; x < groups.get(i).checkBoxes.size(); x++) {
                data.add(Boolean.toString(groups.get(i).checkBoxes.get(x).isChecked()));
                tags.add(groups.get(i).checkBoxes.get(x).getText().toString());
            }
        }
        ArrayList<ArrayList<String>> out = new ArrayList<>();
        out.add(tags);
        out.add(data);
        return out;
    }

}