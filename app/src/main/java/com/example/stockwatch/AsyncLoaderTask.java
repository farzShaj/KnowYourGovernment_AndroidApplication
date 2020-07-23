package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncLoaderTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncLoaderTask";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    //private Bitmap bitmap;
    private DatabaseHandler databaseHandler;

    private static final String stocknameURL = "https://api.iextrading.com/1.0/ref-data/symbols";
   // private static final String iconUrl = "http://openweathermap.org/img/w/";

    AsyncLoaderTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params) {


        Uri.Builder buildURL = Uri.parse(stocknameURL).buildUpon();

        //buildURL.appendQueryParameter("q", params[0]);
        //buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric"));
        //buildURL.appendQueryParameter("appid", apikey);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            conn.disconnect();
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }
    @Override
    protected void onPostExecute(String s) {
        //new AsyncDataTask(mainActivity).execute(s);
        databaseHandler = new DatabaseHandler(mainActivity);
        try {
            JSONArray jObjMain = new JSONArray(s);
            Log.d(TAG, "doInBackground:adding1... "+jObjMain.length());
            for (int i = 0; i < jObjMain.length(); i++) {

                JSONObject jCountry = (JSONObject) jObjMain.get(i);
                Log.d(TAG, "doInBackground:adding... "+jObjMain.get(i));
                String company = jCountry.getString("name");
                String symbol = jCountry.getString("symbol");
                mainActivity.setStockNames(symbol,company);
                //databaseHandler.addStock(company,symbol);

            }
        }
        catch (Exception e){}
        Log.d(TAG, "onPostExecute: going to add to database");

    }

}
