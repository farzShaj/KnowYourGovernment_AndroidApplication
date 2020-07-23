package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class AsyncDataTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncDataTask";
    private DatabaseHandler databaseHandler;
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    protected String doInBackground(String... params){

        String c="hello";
        databaseHandler = new DatabaseHandler(mainActivity);
        try {
            JSONArray jObjMain = new JSONArray(params);
            Log.d(TAG, "doInBackground:adding1... "+jObjMain.length());
            for (int i = 0; i < jObjMain.length(); i++) {

                JSONObject alldata = (JSONObject) jObjMain.get(i);
                Log.d(TAG, "doInBackground:adding... "+jObjMain.get(i));
                String company = alldata.getString("name");
                String symbol = alldata.getString("symbol");
                databaseHandler.addStock(company,symbol);

            }
        }
        catch (Exception e){}
        return "";
    }
    AsyncDataTask(MainActivity ma) {
        mainActivity = ma;
    }


}
