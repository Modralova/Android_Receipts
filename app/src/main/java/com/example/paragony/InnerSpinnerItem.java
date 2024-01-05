package com.example.paragony;

import android.content.Context;
import android.widget.LinearLayout;

public class InnerSpinnerItem extends LinearLayout {

     private String itemName;


    public InnerSpinnerItem(Context context, String itemName) {

        super(context);
        this.itemName = itemName;

    }

    public InnerSpinnerItem(Context context) {
        super(context);
    }

    public String getItemName(){

        return this.itemName;
    }

}
