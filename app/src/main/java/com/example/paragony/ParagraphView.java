package com.example.paragony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ParagraphView extends androidx.appcompat.widget.AppCompatTextView {

    public String textLine = "";
    public List<String> tagList = new ArrayList<>();
    public String category = "";
    public Point[] points;


    private int backgroundColor;
    private int textColor;


    public ParagraphView(Context context) {

        super(context);
        setParagraphView();
    }

    public ParagraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setParagraphView();

    }

    public ParagraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setParagraphView();
    }








    private void setParagraphView() {


        setBackgroundResource(R.drawable.shaddow_example);

        setElevation(10f);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        setTypeface(getTypeface(), Typeface.NORMAL);
        setTextColor(Color.BLACK);
        setPadding(10, 0, 5, 10);
        this.setBackgroundColor();


        if (this.textLine != null)
            setText(this.textLine);

    }

    public String getTitleText() {

        return this.textLine;
    }

    public void setTitleText(String text) {

        this.textLine = text;

        setParagraphView();
    }


    public Point[] getCornerPoints() {

        return this.points;

    }


    public void putData(String text, Point[] points) {


        this.textLine = text;
        this.points = points;
        setParagraphView();

    }

    public void putData(String text, Point[] points, String category, List<String> tagList) {


        this.textLine = text;
        this.setText(text);
        this.points = points;
        this.category = category;
        this.tagList = tagList;
        setParagraphView();

    }

    public void putData(String text, Point[] points, String category, List<String> tagList,  int id) {


        this.textLine = text;
        this.setText(text);
        this.points = points;
        this.category = category;
        this.tagList = tagList;
        this.setId(id);
        setParagraphView();

    }

    public void setCategory(String category) {

        this.category = category;

        setParagraphView();


    }

    public String getCategory() {

        return this.category != null ? this.category : "";
    }


    public List<String> getTags() {

        return this.tagList;
    }

    public void setNewTag(String tag) {

        if (tagList.indexOf(tag) < 0) {

            this.tagList.add(tag);
        }


    }

    public Point[] emptyPointsArray(){

        return new Point[] {new Point(),new Point(),new Point(),new Point()};

    }



    public void removeTag(String tag) {

        if (tagList.indexOf(tag) > -1) {
            this.tagList.remove(tagList.indexOf(tag));
        }


    }

    public void setTags(List<String> tags) {

        this.tagList = tags;

    }






    public Point[] getPoints() {

        return this.points;

    }

    public void setPoints(Point[] points) {

        this.points = new Point[4];

        this.points = points;

    }

    private void setBackgroundColor() {

        LayerDrawable background = (LayerDrawable) this.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background
                .findDrawableByLayerId(R.id.pV_background);

        if (this.category != null) {

            if (this.category.equals(getResources().getString(R.string.date))) {



                gradientDrawable.setColor(getResources().getColor(R.color.DATE,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.shop))) {



                gradientDrawable.setColor(getResources().getColor(R.color.SHOP,null));
                this.setTextColor(Color.WHITE);

            }



            if (this.category.equals(getResources().getString(R.string.address))) {




                gradientDrawable.setColor(getResources().getColor(R.color.ADDRESS,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.nip))) {



                gradientDrawable.setColor(getResources().getColor(R.color.NIP,null));
                this.setTextColor(Color.WHITE);
            }

            if (this.category.equals(getResources().getString(R.string.receipt_id))) {



                gradientDrawable.setColor(getResources().getColor(R.color.R_ID,null));
                this.setTextColor(Color.WHITE);
            }

            if (this.category.equals(getResources().getString(R.string.item))) {



                gradientDrawable.setColor(getResources().getColor(R.color.ITEM,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.quantity))) {



                gradientDrawable.setColor(getResources().getColor(R.color.QUANTITY,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.per_unit))) {



                gradientDrawable.setColor(getResources().getColor(R.color.PER_UNIT,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.price))) {



                gradientDrawable.setColor(getResources().getColor(R.color.PRICE,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.currency))) {



                gradientDrawable.setColor(getResources().getColor(R.color.CURRENCY,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.total))) {



                gradientDrawable.setColor(getResources().getColor(R.color.TOTAL,null));
                this.setTextColor(Color.WHITE);

            }

            if (this.category.equals(getResources().getString(R.string.total_tax))) {



                gradientDrawable.setColor(getResources().getColor(R.color.TAX,null));
                this.setTextColor(Color.WHITE);

            }

            if(this.category.equals("")){


                gradientDrawable.setColor(Color.WHITE);


            }


        }else
        {

            gradientDrawable.setColor(Color.WHITE);

        }

    }



    private enum CColor {

        ADDRESS(0xFFC887),
        DATE(0xCC5400),
        NIP(0xf16b3e),
        R_ID(0Xf13b36),
        SHOP(0x245069),
        ITEM(0x003c44),
        QUANTITY(0x195962),
        PER_UNIT(0x252932),
        PRICE(0x2980b9),
        TOTAL(0xe74c3c),
        TAX(0x3a5169),
        CURRENCY(0x3498db);

        int color;

        private CColor(int setColor) {
            color = setColor;
        }
    }
}