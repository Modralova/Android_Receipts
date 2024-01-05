package com.example.paragony;

import android.content.Context;
import android.widget.LinearLayout;


public class ItemWraper extends LinearLayout {


    private String itemName;

    public ItemWraper(Context context, String itemName) {

        super(context);

        this.itemName = itemName;

    }

    public ItemWraper(Context context) {
        super(context);
    }


    public String getItemName() {

        return this.itemName;
    }

}
