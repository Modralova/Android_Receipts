package com.example.paragony;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Filter {


    public static Boolean isFirstSibling(List<Paragraph> frases, int index,int GRID) {

        Boolean isFirst = false;

        ListIterator<Paragraph> cornersIterator = frases.listIterator();

        int[] left = new int[1];
        Paragraph line = frases.get(index);
        left[0] = line.getCornerPoints()[0].x;

        if (fraseHasSibling(frases, frases.indexOf(line),GRID)) {

            while (cornersIterator.hasNext()) {

                if (left[0] < cornersIterator.next().getCornerPoints()[0].x) {

                    isFirst = true;

                } else {

                    isFirst = false;
                }
            }
        }

        return isFirst;
    }


    public static Boolean fraseHasSibling(List<Paragraph> frases, int index, int GRID) {

        Boolean hasOne = false;

        ListIterator<Paragraph> siblingsIterator = frases.listIterator();

        int[] bottom = new int[1];
        Paragraph line = frases.get(index);

        bottom[0] = line.getCornerPoints()[2].y;

        while (siblingsIterator.hasNext()) {

            if (Math.abs(bottom[0] - siblingsIterator.next().getCornerPoints()[2].y) < GRID) {

                hasOne = true;
                break;

            } else {

                hasOne = false;
            }

        }

        return hasOne;
    }

    public static Boolean tableAlreadyHasFrase(LinearLayout frame, String frase) {

        Boolean hasOne = false;

        List<String> paragraph_frases = new ArrayList<>();
        ListIterator<String> frasesIterator = paragraph_frases.listIterator();

        for (int l = 0; l < frame.getChildCount(); l++) {               // podsumuj wiersz

            ParagraphView pf = (ParagraphView) frame.getChildAt(l);
            paragraph_frases.add(pf.getText().toString());

        }

        while (frasesIterator.hasNext()) {

            if (frasesIterator.next() == frase) {

                hasOne = true;
                break;
            }
        }

        return hasOne;

    }


}
