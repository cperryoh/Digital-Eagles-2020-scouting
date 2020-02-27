package com.team5667;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public boolean matchStarted = false;
    public TabLayout tabs;
    SharedPreferences mPrefs;
    ArrayList<StringRequest> requests =new ArrayList<>();
    pitScout pitScoutFrag = new pitScout("pit scouting", R.layout.pit_scouting);
    public supperScout supperScoutFrag = new supperScout("Super Scout", R.layout.super_scout);
    public prematch prematchFrag = new prematch("pre match", R.layout.prematch);
    public match matchFrag = new match("match", R.layout.match);
    public SetupWindow setupWindowFrag = new SetupWindow("Set up window", R.layout.setup_window);
    public postMatch postmatchFrag = new postMatch("post match", R.layout.postmatch);
    static int postMatchTab = 5;
    static int matchTab = 4;
    static int preMatchTab = 3;
    final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), new tab[]{setupWindowFrag, supperScoutFrag, pitScoutFrag, prematchFrag, matchFrag, postmatchFrag});

    public StringRequest pushSuperScout(final String comment, final String team, final String scouter, final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, setupWindowFrag.getWebApp(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();


                    }
                }
        ) {
            public Map<String, String> getVals(){
                return getParams();
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "superScouter");
                parmas.put("name", scouter);
                parmas.put("team", team);
                parmas.put("comment", comment);
                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds. Takes milliseconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        return stringRequest;
    }

    void addOffLine() {
        if (requests.size() != 0) {
            AppController.getInstance().getRequestQueue().add(requests.get(0));
            requests.remove(0);
            addOffLine();
        }
        Toast.makeText(getBaseContext(), "Offline Data Queue Cleared", Toast.LENGTH_SHORT).show();
        return;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 500);
                if (isNetworkAvailable() && requests.size() != 0) {

                    Toast.makeText(getBaseContext(), "Beginning To load queue", Toast.LENGTH_SHORT).show();
                    addOffLine();
                }
                return;
            }
        };
        run.run();
        mPrefs = getSharedPreferences("scouting app", 0);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.beginFakeDrag();
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Button button = (Button) findViewById(R.id.button);
        setTabs(false, postMatchTab);
        setTabs(false, matchTab);
    }

    public void pushData(StringRequest request) {
        if (isNetworkAvailable()) {
            AppController.getInstance().getRequestQueue().add(request);
        } else {
            Toast.makeText(getApplicationContext(), "Data added to offline queue", Toast.LENGTH_SHORT).show();
            requests.add(request);
        }
    }
    //string request for general data


    public StringRequest addData(final ArrayList<String> data, final ArrayList<String> tags, final String sheet) {
        Toast.makeText(getApplicationContext(), "Pushing data", Toast.LENGTH_SHORT).show();

        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        };

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, setupWindowFrag.getWebApp(), listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }

        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("action", "addItem");
                parmas.put("sheetUrl", setupWindowFrag.getSheet());
                parmas.put("sheet", sheet);
                for (int i = 0; i < tags.size(); i++) {
                    parmas.put("_"+i+"_"+tags.get(i), data.get(i));
                }
                return parmas;
            }
        };
        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        return stringRequest;
    }

    //get urls after opening app
    public String getVal(String tag) {
        return mPrefs.getString(tag, "");
    }

    //store val for app after closing
    public void storeVal(String tag, String val) {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(tag, val).commit();
    }

    //all data pushed goes through this function and is pushed to the queue
    public StringRequest pushComment(final String comment, final String driveTrain, final String team, final String scouter, final Context context) {
        Toast.makeText(getApplicationContext(), "Pushing Comment", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, setupWindowFrag.getWebApp(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addComment");
                parmas.put("sheetUrl", setupWindowFrag.getSheet());
                parmas.put("name", scouter);
                parmas.put("DriveTrain", driveTrain);
                parmas.put("team", team);
                parmas.put("comment", comment);
                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds. Takes milliseconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        return stringRequest;
    }

    //notification to urls empty
    public boolean checkurls(Context context) {
        if (!setupWindowFrag.sheetUrl.getText().toString().equals("") && !setupWindowFrag.sheetUrl.getText().toString().equals("")) {
            return true;
        }
        new AlertDialog.Builder(context)
                .setTitle("Data can not be pushed.")
                .setMessage("Please ensure that you have set up both web url and sheet url in the setup window.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {/*do nothing on click*/}
                });
        return false;
    }

    //resets match
    public void resetMatch() {
        setAllTabs(true);
        matchStarted = false;
        matchFrag.run.setTime(matchFrag.maxTime);
        setTabs(false, MainActivity.matchTab);
        setTabs(false, MainActivity.postMatchTab);
        tabs.getTabAt(preMatchTab).select();
        prematchFrag.reset();
        matchFrag.reset();
        postmatchFrag.reset();
    }

    //get team num
    public int getTeamNumber() {
        return prematchFrag.GetTeamNumber();
    }

    //get round number
    public int getRoundNumer() {
        return prematchFrag.GetRoundNumber();
    }

    //start and set up for match
    public void startMatch() {
        tabs.getTabAt(matchTab).select();
        setAllTabs(false);
        matchStarted = true;
    }
    //tabs functions

    //set tab at index to disable or enable
    void setTabs(boolean status, int index) {

        LinearLayout tabStrip = ((LinearLayout) tabs.getChildAt(0));
        tabStrip.getChildAt(index).setClickable(status);
    }

    //set all tabs to a certain value
    void setAllTabs(boolean state) {
        LinearLayout tabStrip = ((LinearLayout) tabs.getChildAt(0));
        tabStrip.setEnabled(state);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(state);
        }
    }

    //Static general functions

    //check to ensure prePos is not selected to the first item
    public static boolean checkSpinnerChange(Spinner spinner) {
        return spinner.getSelectedItemPosition() != 0;
    }

    //is edit text not empty
    public static boolean textNotEmpty(EditText text) {
        return !text.getText().toString().equals("");
    }

    //merge arrays with each other
    public static ArrayList<String> mergeArrays(ArrayList<String> base, ArrayList<String> mergeInto) {
        for (int i = 0; i < mergeInto.size(); i++) {
            base.add(mergeInto.get(i));
        }
        return base;
    }
}