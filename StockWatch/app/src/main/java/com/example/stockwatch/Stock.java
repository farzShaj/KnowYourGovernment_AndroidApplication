package com.example.stockwatch;

public class Stock {
    private String stock_name;
    private String company_name;
    private double change;
    private double stock_price;
    private double change_percent;
    Stock(String stock_name,String company_name,double change,double stock_price,double change_percent) {
        this.stock_name=stock_name;
        this.company_name=company_name;
        this.change=change;
        this.stock_price=stock_price;
        this.change_percent=change_percent;
    }

    public String getStockName(){
      return stock_name;
    }
    public String getCompanyName(){
      return company_name;
    }
    public double getChange(){
        return change;
    }
    public double getStock(){
        return stock_price;
    }
    public double getChangePercent(){
        return change_percent;
    }
}
