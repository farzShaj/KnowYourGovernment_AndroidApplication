package com.example.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncFinancialData extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsyncFinancialData";
    private DatabaseHandler databaseHandler;
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private Stock newStock;
    private static final String finURL = "https://cloud.iexapis.com/stable/stock/";
    private static final String apikey = "pk_283bb54092264f49ac8b44b514ff106f";

    AsyncFinancialData(MainActivity ma) {
        mainActivity = ma;
    }

    protected String doInBackground(String... params) {
        String urlToUse = finURL + params[0] + "/quote";
        Uri.Builder buildURL = Uri.parse(urlToUse).buildUpon();
        buildURL.appendQueryParameter("token", apikey);
        urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
            }
            Log.d(TAG, "doInBackgroundString: " + sb.toString());
        }
        catch (Exception e) {
            Log.e(TAG, "doInBackgroundError: "+e);
            return null;
        }
        return sb.toString();
    }

    protected void onPostExecute(String s) {
        try {
            JSONObject data = new JSONObject(s);
            double price=0,change=0,changePercent=0;
            Log.d(TAG, "AFD:"+data);
            String company = data.getString("companyName");
            String symbol = data.getString("symbol");
            if(data.get("latestPrice")!=null)
                price = data.getDouble("latestPrice");
            if(data.get("change")!=null)
                change = data.getDouble("change");
            if(data.get("changePercent")!=null)
                changePercent = data.getDouble("changePercent");
            newStock = new Stock(symbol, company, change, price, changePercent);
            mainActivity.setStock(newStock);
            DatabaseHandler databaseHandler=new DatabaseHandler(mainActivity);
            databaseHandler.addStock(company,symbol);
        } catch (Exception e) {
            Log.d(TAG, "AFD Error:"+e);
        }
    }
}
