package com.example.paragony;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;


public class StoreOcr {

    Context context;

    public String fileName = "r_" + new Date().getTime() + ".json";

    public String customFilename;

    public Paragraph_schema[][] lines;

    public StoreOcr(Context context) {

        this.context = context;

    }

    public void setFilename(String filename) {


        this.customFilename = filename;

    }


    public void readLines(LinearLayout text_board) {

        this.lines = new Paragraph_schema[text_board.getChildCount()][];


        for (int i = 0; i < text_board.getChildCount(); i++) {

            LinearLayout frame = (LinearLayout) text_board.getChildAt(i);

            Paragraph_schema[] frameArray = new Paragraph_schema[frame.getChildCount()];

            for (int j = 0; j < frame.getChildCount(); j++) {


                ParagraphView paragraphView = (ParagraphView) frame.getChildAt(j);

                frameArray[j] = new Paragraph_schema(

                        paragraphView.getText().toString(),
                        paragraphView.getCategory(),
                        paragraphView.getTags(),
                        paragraphView.getPoints()
                );

            }


            this.lines[i] = frameArray;

        }


    }


    public void writeLinesToJson(LinearLayout text_board) {

        this.readLines(text_board);


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

        File appDir = new File(root + "/Paragony/");

        File jsonDir = new File(appDir + "/JSON/");

        if (!appDir.exists()) {

            boolean success = appDir.mkdirs();


            if (success) {

                System.out.println(String.format("Created: %s", appDir));

            } else {

                System.out.println(String.format("Cannot create: %s", appDir));
            }

        }

        if (!jsonDir.exists()) {

            boolean success = jsonDir.mkdirs();


            if (success) {

                System.out.println(String.format("Created: %s", jsonDir));

            } else {

                System.out.println(String.format("Cannot create: %s", jsonDir));
            }

        }


        String jsonName = customFilename;


        File file = new File(jsonDir, jsonName);

        if (file.exists()) {

            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_POSITIVE) {

                    System.out.println(String.format("POSITIVE: %s", file));

                    this.writer(file);


                }

                if (which == DialogInterface.BUTTON_NEGATIVE) {


                    File newFile = new File(jsonDir, this.fileName);

                    System.out.println(String.format("NEGATIVE_fn: %s", this.fileName));


                    System.out.println(String.format("NEGATIVE: %s", newFile));

                    this.writer(newFile);

                }

            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage(context.getString(R.string.ow_quote))
                    .setNegativeButton(context.getString(R.string.new_file), listener)
                    .setPositiveButton(context.getString(R.string.overwrite), listener)
                    .show();


        } else {


            this.writer(file);


        }

    }

    public Paragraph_schema[][] readFromJson(String directory) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

        File appDir = new File(root + "/Paragony/");

        File jsonDir = new File(appDir + "/JSON/");

        String jsonName = directory;

        Gson gson = new Gson();

        File file = new File(jsonDir, jsonName);


        Paragraph_schema[][] paragraphs = null;

        try {

            Reader reader = Files.newBufferedReader(Paths.get(file.toString()));

            paragraphs = gson.fromJson(reader, Paragraph_schema[][].class);


        } catch (Exception e) {

            e.printStackTrace();

        }

        return paragraphs;

    }



    private void writer(File file_name) {

        Gson gson = new Gson();


        try {

            Writer writer = new FileWriter(file_name);

            gson.toJson(this.lines, writer);
            writer.flush();
            writer.close();

            Toast.makeText(context, context.getString(R.string.json_success), Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            e.printStackTrace();


            Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG).show();

        }


    }


}
