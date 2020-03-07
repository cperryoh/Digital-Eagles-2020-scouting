package com.team5667;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;


import java.util.ArrayList;

public class SetupWindow extends tab {
    public EditText sheetUrl;
    public EditText webAppUrl;
    ArrayList<EditText> urls = new ArrayList<>();
    boolean web;
    boolean sheet;
    public SetupWindow(String tabText, int layout) {
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
        webAppUrl=view.findViewById(R.id.webAppUrl);
        urls.add(webAppUrl);
        sheetUrl=view.findViewById(R.id.sheetUrl);
        urls.add(sheetUrl);
        final MainActivity activity = (MainActivity)getActivity();
        if(!activity.getVal("web").equals("")){
            webAppUrl.setText(activity.getVal("web"));

        }
        if(!activity.getVal("sheet").equals("")){
            sheetUrl.setText(activity.getVal("sheet"));
        }
        boolean forMyTeam=true;
        if(forMyTeam) {

            webAppUrl.setText("https://script.google.com/macros/s/AKfycbyb2hmTm9u9QkqD3sH-6ynar0LKGENOPjy8IugUYoo7PRym9EU/exec");
            webAppUrl.setEnabled(false);
            sheetUrl.setText("https://docs.google.com/spreadsheets/d/1P4mR-f3pnhm0vtGWHvsAGvWPhZtxuLVGLKnz5rDAHcs/edit#gid=0");
            sheetUrl.setEnabled(false);

        }else{
            for(int i =0;i<urls.size();i++){
                final int finalI = i;
                urls.get(i).addTextChangedListener(new TextWatcher() {
                    int index= finalI;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(index==0){
                            Toast.makeText(getContext(),"Web changed",Toast.LENGTH_SHORT).show();
                            if (MainActivity.textNotEmpty(webAppUrl)){
                                web=true;
                                activity.storeVal("web",webAppUrl.getText().toString());
                            }else{

                                web=false;
                            }
                            if(web&&sheet){
                                ((MainActivity)getActivity()).setAllTabs(true);
                                ((MainActivity)getActivity()).setTabs(false,MainActivity.postMatchTab);
                                ((MainActivity)getActivity()).setTabs(false,MainActivity.matchTab);
                            }
                            else{
                                ((MainActivity)getActivity()).setAllTabs(false);
                            }
                        }else{
                            if (MainActivity.textNotEmpty(sheetUrl)){
                                sheet=true;
                                activity.storeVal("sheet",sheetUrl.getText().toString());
                            }else{

                                sheet=false;
                            }
                            if(web&&sheet){
                                ((MainActivity)getActivity()).setAllTabs(true);
                                ((MainActivity)getActivity()).setTabs(false,MainActivity.matchTab);
                                ((MainActivity)getActivity()).setTabs(false,MainActivity.postMatchTab);
                            }
                            else{
                                ((MainActivity)getActivity()).setAllTabs(false);
                            }
                        }
                    }
                });
            }
        }

        //open up tutorial link
        (view.findViewById(R.id.setUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://docs.google.com/document/d/1Jzd016uVxW_6dBimzpTt3D2xOkI78RoheJa4Q6rddbk/edit?usp=sharing"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        return view;
    }
    public String getSheet(){
        return  sheetUrl.getText().toString();
    }
    public String getWebApp(){
        return  webAppUrl.getText().toString();
    }
}
