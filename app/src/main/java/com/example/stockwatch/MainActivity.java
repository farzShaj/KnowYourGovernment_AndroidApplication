package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import java.lang.Comparable;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener,Comparable<String>{
    private RecyclerView recyclerView;
    private List<Stock> stocks=new ArrayList<Stock>();
    private HashMap<String,String> retrieved;
    private HashMap<String,String> stock_names;
    private HashMap<String,String> added_stocks;
    private SwipeRefreshLayout swiper;
    private DatabaseHandler databaseHandler;
    private StockAdapter stockAdapter;
    private MainActivity ma;
    private String name1;
    private static String stockURL="https://www.marketwatch.com/tools/quotes/lookup.asp?lookup=";
    private static final String TAG = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recycler);
        stockAdapter=new StockAdapter(stocks,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(stockAdapter);
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        getNames();
        showAddedStocks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_stock) {
            boolean net=doNetCheck();
            if(net==true) {
                if(stock_names.size()==0)
                    getNames();
                enterDialogBox();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void enterDialogBox(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        final EditText et = new EditText(this);
        et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
                String name=et.getText().toString();
                findStockName(name);
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void findStockName(String name){
        retrieved = new HashMap<>();
        String curr_symb, curr_name;
        Set<String> symbols = stock_names.keySet();
        Iterator name_iterator = symbols.iterator();
        while (name_iterator.hasNext()) {
            curr_symb = (String) name_iterator.next();
            curr_name = stock_names.get(curr_symb);
            if (curr_symb.contains(name) || curr_name.contains(name)) {
                retrieved.put(curr_symb, curr_name);
            }
        }
        if (retrieved.size() == 0) {
            noStockDialog(name);
        }
        else if (retrieved.size() == 1) {
            Set<String> retrieved_symbols = retrieved.keySet();
            Iterator i = retrieved_symbols.iterator();
            getDataAndAdd((String)i.next());
        }
        else {
            Log.d(TAG, "findStockName:going to show list ");
            listDialogBox(retrieved);
        }
    }

    protected void listDialogBox(HashMap<String,String> retrieved){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        int num=retrieved.size();
        final CharSequence[] sArray=new CharSequence[num];
        Set<String> keys=retrieved.keySet();
        Iterator riter=keys.iterator();
        final String symbols[]=new String[num];
        int i=0;
        while(riter.hasNext()) {
            String symbol=riter.next().toString();
            sArray[i]= symbol+" -> "+retrieved.get(symbol);
            i++;
            symbols[i-1]=symbol;
        }
        builder.setTitle("Make a selection");
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getDataAndAdd(symbols[which]);
            }
        });
        builder.setNegativeButton("NEVERMIND",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void setStock( Stock newStock) {
        stocks.add(newStock);
        Collections.sort(stocks, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
            name1=o1.getStockName();
            return name1.compareTo(o2.getStockName());
            }
        });
        stockAdapter.notifyDataSetChanged();
    }

    public void onClick(View v){
        int pos=recyclerView.getChildLayoutPosition(v);
        Intent i = new Intent(Intent.ACTION_VIEW);
        String currURL=new String(stockURL);
        currURL=stockURL.concat(stocks.get(pos).getStockName());
        i.setData(Uri.parse(currURL));
        startActivity(i);
    }

    public boolean onLongClick(View v){
        int pos=recyclerView.getChildLayoutPosition(v);
        Stock del_stock = stocks.get(pos);
        dialogBox(del_stock);
        return false;
    }

    public void getDataAndAdd(String symbol){
        Stock newstock;
        Iterator stocks_it=stocks.iterator();
        while(stocks_it.hasNext()){
            newstock=(Stock)stocks_it.next();
            if(newstock.getStockName().equals(symbol)){
                duplicateDialog(symbol);
                return;
            }
        }
        new AsyncFinancialData(this).execute(symbol);
    }

    public void dialogBox(final Stock del_stock){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol "+del_stock.getStockName()+"?");
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            databaseHandler.deleteStock(del_stock.getCompanyName());
            stocks.remove(del_stock);
            stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void setStockNames(String symbol,String name){
        stock_names.put(symbol,name);
    }

    public void refresh(){
        boolean net=doNetCheck();
        stocks.removeAll(stocks);
        if(net==true) {
            Stock next;
            String name;
            showAddedStocks();
        }
        swiper.setRefreshing(false);
    }

    public void getNames(){
        new AsyncLoaderTask(this).execute();
    }

    public void showAddedStocks(){
        boolean net=doNetCheck();
        databaseHandler=new DatabaseHandler(this);
        stock_names=new HashMap<>();
        added_stocks=new HashMap<>();
        added_stocks=databaseHandler.loadStocks();
        Set<String> added_symbs=added_stocks.keySet();
        String addednext;
        Iterator added=added_symbs.iterator();
        if(net==true){
            while(added.hasNext()) {
                addednext=(String)added.next();
                new AsyncFinancialData(this).execute(addednext);
            }
        }
        else{
            while(added.hasNext()) {
                addednext = (String) added.next();
                Stock nexts = new Stock(addednext, added_stocks.get(addednext), 0.0, 0.0, 0.0);
                setStock(nexts);
            }
        }
        stockAdapter.notifyDataSetChanged();
    }

    @Override
    public int compareTo(String s1) {
        Log.d(TAG, "compareTo: "+name1);
        return name1.compareTo(s1);
    }
    public void duplicateDialog(String name){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol "+name+" is already displayed");
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        else {
            noNetworkDialog();
            return false;
        }
    }

    public void noNetworkDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void noStockDialog(String name){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("Symbol Not Found: "+name+"");
        builder.setMessage("Data for stock symbol");
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
