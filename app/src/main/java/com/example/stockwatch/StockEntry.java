package com.example.stockwatch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockEntry extends RecyclerView.ViewHolder {
    public TextView stock_name;
    public TextView stock;
    public TextView change;
    public ImageView change_indicator;
    public TextView company_name;

    StockEntry(View view){
        super(view);
        stock_name=view.findViewById(R.id.symb);
        stock=view.findViewById(R.id.stock);
        company_name=view.findViewById(R.id.company);
        change=view.findViewById(R.id.change);
        change_indicator=view.findViewById(R.id.change_indicator);
    }

}

