package com.example.eventnepal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends Fragment {

    // String API_KEY = "8190df9eb51445228e397e4185311a66"; // ### YOUE NEWS API HERE ###
    String API_KEY = "http://192.168.137.1:8080/sport_fanatic/api/member/api.php";
    String NEWS_SOURCE = "simant";
    ListView listnews;
    ProgressBar loader;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    static final String KEY_SOURCE = "location";
    static final String KEY_TITLE = "futsal_name";
    static final String KEY_DESCRIPTION = "futsal_desc";
    static final String KEY_URL = "image";
    static final String KEY_URLTOIMAGE = "image";
    static final String KEY_PUBLISHEDAT = "price";

    static final String KEY_URL_LOCATION = "map";

    private ListView esListView;
    private ProgressBar epProgressBar;
    private View mMainView;

    public HomeFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_home, container, false);

        esListView = mMainView.findViewById(R.id.listnews);
        epProgressBar = mMainView.findViewById(R.id.loader);
        esListView.setEmptyView(loader);

        if (Function.isNetworkAvailable(getActivity())) {
            new DownloadNews().execute();
        } else {
            // Toast.makeText(getActivity(), "Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
            existMode();
        }

        if (dataList != null) {
            dataList.clear();
        }

        return mMainView;
    }

    // Code terminated in line 86 and 102

    class DownloadNews extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            epProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String xml = "";

            String urlParameters = "";
            xml = Function.excuteGet("http://192.168.137.1:8080/sport_fanatic/api/member/api.php", urlParameters);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            epProgressBar.setVisibility(View.INVISIBLE);

            if (xml.length() > 10) { // Just checking if not empty

                try {
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("tbl_futsal");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_SOURCE, jsonObject.optString(KEY_SOURCE).toString());
                        map.put(KEY_TITLE, jsonObject.optString(KEY_TITLE).toString());
                        map.put(KEY_DESCRIPTION, jsonObject.optString(KEY_DESCRIPTION).toString());
                        map.put(KEY_URL, jsonObject.optString(KEY_URL).toString());
                        map.put(KEY_URLTOIMAGE, jsonObject.optString(KEY_URLTOIMAGE).toString());
                        map.put(KEY_PUBLISHEDAT, jsonObject.optString(KEY_PUBLISHEDAT).toString());

                        map.put(KEY_URL_LOCATION, jsonObject.optString(KEY_URL_LOCATION).toString());
                        dataList.add(map);

                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Sorry, We are under maintainance. It may take an hours.", Toast.LENGTH_SHORT).show();
                }

                ListNewsAdapter adapter = new ListNewsAdapter(getActivity(), dataList);
                esListView.setAdapter(adapter);


                esListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        HashMap<String, String> map = dataList.get(position);
                        Intent i = new Intent(getActivity(), EventActivity.class);
                        // i.putExtra("url",map.get(KEY_URL));
                        i.putExtra("url", map.get(KEY_URL));
                        i.putExtra("title", map.get(KEY_TITLE));
                        i.putExtra("source", map.get(KEY_SOURCE));
                        i.putExtra("desc", map.get(KEY_DESCRIPTION));
                        i.putExtra("date", map.get(KEY_PUBLISHEDAT));

                        i.putExtra("location", dataList.get(+position).get(KEY_URL_LOCATION));
                        startActivity(i);
                    }
                });


            } else {
                Toast.makeText(getActivity(), "No news found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void existMode() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());

        // Hanlde new AlertDialog.Builder(HomeFragment.this); into this.getContext();

        alertDialogBuilder
                .setMessage("Please check your internet connection or visit the offline mode.")
                .setCancelable(false)
                .setPositiveButton("Offline Mode",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // startActivity(new Intent(getContext(), LibraryFragment.class));
                                // handle getApplicationContext() to getContext() in fragment .java
                                // Intent intent = new Intent(getContext(), Solution.class);
                                // startActivity(intent);
                            }
                        })
                .setNegativeButton("EXIT",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //  finish();
                                getActivity().finish();
                                // Handle onClick event in fragment layout ot exit
                                // handle above offline capabilities

                            }

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setTitle("Are you sure, you want to exit?");
        // alert.setMessage("Message");

        alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getActivity().finish();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

}

