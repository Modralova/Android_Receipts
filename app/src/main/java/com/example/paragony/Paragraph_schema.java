package com.example.paragony;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Paragraph_schema implements Parcelable {

    public String textLine;
    public String category;
    public String[] tags;
    public List<String> tagsList;
    public int[][] points;
    int id;
    int index;


    Paragraph_schema(ParagraphView paragraphView) {

        this.textLine = paragraphView.textLine;
        this.category = paragraphView.category;
        this.tagsList = paragraphView.tagList;
        this.id = paragraphView.getId();




       setPointsArray(paragraphView.getPoints());

    }


    public Paragraph_schema(String textLine) {

        this.textLine = textLine;

    }

    public Paragraph_schema(String textLine,
                            String category,
                            String[] tagsArray) {

        this.textLine = textLine;
        this.category = category;

        this.tagsList = new ArrayList<>();
        this.tags = new String[0];

        if (tagsArray != null) {

            this.tags = new String[tagsArray.length];

            for (int i = 0; i < tagsArray.length; i++) {

                System.out.println(String.format("tag: %s", tagsArray[i]));

                this.tagsList.add(tagsArray[i]);

                this.tags[i] = tagsArray[i];

            }
        }
    }


    public Paragraph_schema(
            String textLine,
            String category,
            List<String> tagList,
            Point[] points
    ) {


        this.textLine = textLine;
        this.category = category;

        this.tags = new String[tagList.size()];

        this.tagsList = tagList;

        for (int i = 0; i < tagList.size(); i++) {

            tags[i] = tagList.get(i);
        }

        if (points != null) {

            this.points = new int[4][];

            for (int i = 0; i < 4; i++) {

                int[] coords = new int[2];

                coords[0] = points[i].x;
                coords[1] = points[i].y;

                this.points[i] = coords;

            }
        }
    }



    protected Paragraph_schema(Parcel in) {
        textLine = in.readString();
        category = in.readString();
        tags = in.createStringArray();
        tagsList = in.createStringArrayList();
    }

    public static final Creator<Paragraph_schema> CREATOR = new Creator<Paragraph_schema>() {
        @Override
        public Paragraph_schema createFromParcel(Parcel in) {
            return new Paragraph_schema(in);
        }

        @Override
        public Paragraph_schema[] newArray(int size) {
            return new Paragraph_schema[size];
        }
    };

    public Point[] getPoints() {

        Point[] points = null;

        if (this.points != null) {

            points = new Point[this.points.length];


            for (int i = 0; i < this.points.length; i++) {

                points[i] = new Point(this.points[i][0], this.points[i][1]);

            }
        }

        return points;
    }

    private void setPointsArray(Point[] points){

        if (points != null) {

            this.points = new int[4][];

            for (int i = 0; i < 4; i++) {

                int[] coords = new int[2];

                coords[0] = points[i].x;
                coords[1] = points[i].y;

                this.points[i] = coords;

            }
        }





    }

    public List<String> getTagList() {

        List<String> tags = new ArrayList<>();

        if(this.tags != null) {

            for (int i = 0; i < this.tags.length; i++) {

                tags.add(this.tags[i]);

            }
        }

        return tags;

    }

    public String[] getTagStringArray() {

        String[] tags = {};

        if(tagsList != null) {



            tags = new String[tagsList.size()];

            for (int i = 0; i < tagsList.size(); i++) {

                tags[i] = tagsList.get(i);

            }
        }

        return tags;
    }


    public void putTagStringArray(String[] tags) {

        this.tagsList = new ArrayList<>();


        for (int i = 0; i < tags.length; i++) {


            this.tagsList.add(tags[i]);

        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(textLine);
        dest.writeString(category);
        dest.writeStringArray(tags);
        dest.writeStringList(tagsList);

    }
}
