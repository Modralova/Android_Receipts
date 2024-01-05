package com.example.paragony;

import android.content.Context;
import android.graphics.Typeface;


import java.util.List;

public class TagFrame extends androidx.appcompat.widget.AppCompatTextView {
    private String tag;
    private List<String> tagList;

    public TagFrame(Context context) {
        super(context);
    }

    public TagFrame(Context context, String tag) {
        super(context);

        this.tag = tag;

        setText(this.tag);
        setTypeface(getTypeface(), Typeface.ITALIC);
        setTextSize(15);

    }

    public List<String> getTags() {

        return this.tagList;
    }

    public void setTag(String tag) {

        this.tagList.add(tag);

    }

    public void removeTag(String tag) {

        this.tagList.remove(tagList.indexOf(tag));

    }


    public void setTags(List<String> tags) {

        this.tagList = tags;

    }
}