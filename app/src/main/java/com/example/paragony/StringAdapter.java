package com.example.paragony;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StringAdapter extends ArrayAdapter<ItemWraper> {


    public StringAdapter(Context context, ArrayList<ItemWraper> items) {

        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // return super.getView(position, convertView, parent);

        return itemInitView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //  return super.getDropDownView(position, convertView, parent);
        return itemInitView(position, convertView, parent);
    }

    private View itemInitView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_wraper_row, parent, false);

        }

        TextView itemText = convertView.findViewById(R.id.item_view);

        ItemWraper itemWraper = getItem(position);

        if (itemWraper != null) {

            itemText.setText(itemWraper.getItemName());

        }

        return convertView;
    }


}
