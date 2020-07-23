
package com.example.stockwatch;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "Stocks";

    // DB Table Name
    private static final String TABLE_NAME = "StockSymbols";

    ///DB Columns
    private static final String COMPANY = "StockName";
    private static final String SYMBOL = "Symbol";


    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;


    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    HashMap<String,String> loadStocks() {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadCountries: START");
        HashMap<String,String> matching_stocks = new HashMap<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{COMPANY, SYMBOL}, // The columns to return
                null,
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String company = cursor.getString(0);
                String symbol = cursor.getString(1);
                matching_stocks.put(symbol,company);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadCountries: DONE");

        return matching_stocks;
    }

    void addStock(String company,String symbol) {
        ContentValues values = new ContentValues();
        Log.d(TAG, "addStock: " + 1);
        values.put(COMPANY,company);
        values.put(SYMBOL, symbol);

        long key = database.insert(TABLE_NAME, null, values);

    }

    void updateStock(Stock country) {
        ContentValues values = new ContentValues();
    }

    void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);
        int cnt = database.delete(TABLE_NAME, COMPANY + " = ?", new String[]{name});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Log.d(TAG, "dumpDbToLog: ");
            for (int i = 0; i < cursor.getCount(); i++) {
                String country = cursor.getString(0);
                String region = cursor.getString(1);
                String subRegion = cursor.getString(2);
                String capital = cursor.getString(3);
                int population = cursor.getInt(4);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", COMPANY + ":", country) +
                        String.format("%s %-18s", SYMBOL + ":", region) );
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "dumpDbToLog: ");
    }

    void shutDown() {
        database.close();
    }
}
