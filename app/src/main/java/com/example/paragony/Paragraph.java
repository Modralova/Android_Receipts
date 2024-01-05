package com.example.paragony;

import android.graphics.Point;

public class Paragraph {

    private String text;

    private int framePoints[][];
    private Point[] points;


public void putData(String text, Point[] p) {

    this.text = text;


        this.points = p;


    }

    public void putLine(String text) {

        this.text = text;

        this.points = new Point[]{new Point(),new Point(),new Point(),new Point()};



    }

    public String getText() {

        return this.text;
    }

    public Point[] getCornerPoints() {


        return this.points;
    }
    public int[][] pointToInts(){

        return framePoints;
    }

}
