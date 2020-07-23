package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockEntry> {
    private List<Stock> stocks;
    private MainActivity mainActivity;
    StockAdapter(List<Stock> stocks,MainActivity mainActivity1)
    {
        this.stocks=stocks;
        mainActivity=mainActivity1;
    }
    public StockEntry onCreateViewHolder(final ViewGroup parent, int viewtype){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_entries,parent,false);
        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);
        return new StockEntry(view);
    }
    public void onBindViewHolder(StockEntry stockEntry, int pos){
        Stock stock=stocks.get(pos);
        stockEntry.stock_name.setText(stock.getStockName());
        //stockEntry.stock.setText(String.valueOf(stock.getStock()));
        stockEntry.stock.setText(String.format("%.2f",Double.valueOf(stock.getStock())));
        stockEntry.company_name.setText(stock.getCompanyName());
        //stockEntry.change.setText(String.valueOf(stock.getChange())+"("+String.valueOf(stock.getChangePercent())+"%)");
        stockEntry.change.setText(String.format("%.2f",Double.valueOf(stock.getChange()))+" ("+String.format("%.2f",Double.valueOf(stock.getChangePercent()))+"%)");

        if(stock.getChange()<0.0){
            stockEntry.change_indicator.setImageResource(R.drawable.down);
            stockEntry.stock_name.setTextColor(Color.RED);
            stockEntry.stock.setTextColor(Color.RED);
            stockEntry.change.setTextColor(Color.RED);
            stockEntry.company_name.setTextColor(Color.RED);
        }
        else if(stock.getChange()>0.0)
        {
            stockEntry.change_indicator.setImageResource(R.drawable.up);
            stockEntry.stock_name.setTextColor(Color.GREEN);
            stockEntry.stock.setTextColor(Color.GREEN);
            stockEntry.change.setTextColor(Color.GREEN);
            stockEntry.company_name.setTextColor(Color.GREEN);
        }
        else{
            stockEntry.stock_name.setTextColor(Color.WHITE);
            stockEntry.stock.setTextColor(Color.WHITE);
            stockEntry.change.setTextColor(Color.WHITE);
            stockEntry.company_name.setTextColor(Color.WHITE);
        }
    }
    public int getItemCount(){
        return stocks.size();
    }

}
