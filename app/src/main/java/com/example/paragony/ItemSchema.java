package com.example.paragony;

import java.util.List;

public class ItemSchema {


    public String date;
    public String receipt_id;
    public String shop;
    public String item;
    public String category;
    public String currency;
    public Double  quantity = 0.0;
    public Double per_unit = 0.0;
    public Double price = 0.0;
    public Double tax = 0.0;
    public String tax_type;
    public String tags;
    public List<String> tagsList;


    public ItemSchema() {


    }

    public Double getPrice(){
        return this.price;

    }

    public Double getTax(){
        return this.tax;

    }
}





