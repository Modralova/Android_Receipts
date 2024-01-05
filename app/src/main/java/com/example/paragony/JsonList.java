package com.example.paragony;

import java.io.File;
import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.paragony.databinding.JsonListLayoutBinding;


public class JsonList extends Activity {

    JsonListLayoutBinding binding;


    private ListView list;
    private ArrayAdapter<String> adapter;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        binding = JsonListLayoutBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        list = (ListView) binding.jsonList;

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File jsonDir = new File(root + "/Paragony/" + "/JSON/");

        File[] files = jsonDir.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
        }

        ArrayList<String> jsons = new ArrayList<>();


        for (File file : files) {

            jsons.add(file.getName());

        }

        adapter = new ArrayAdapter<String>(this, R.layout.json_list_row, jsons);

        list.setAdapter(adapter);


        list.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(JsonList.this, MainActivity.class);
            Intent extras = getIntent();

            Bundle bundle = new Bundle();

            transmiter(extras, bundle);

            TextView dir = (TextView) view;

            fileName = dir.getText().toString();


            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_NEGATIVE) {

                    bundle.putString("directory", fileName);
                    bundle.putBoolean("bool", true);

                    intent.putExtras(bundle);

                    startActivity(intent);



                }

                if (which == DialogInterface.BUTTON_POSITIVE) {

                    bundle.putString("directory", fileName + "NEW");
                    bundle.putBoolean("bool", true);

                    intent.putExtras(bundle);

                    startActivity(intent);







                }

            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.fromJson))
                    .setNegativeButton(getString(R.string.add), listener)
                    .setPositiveButton(getString(R.string.asNew), listener)
                    .show();


        });
    }

    public void transmiter(Intent extras, Bundle transmit) {

        int boardLength = extras.getIntExtra("boardLength", 0);
        int longestLine = extras.getIntExtra("longestLine", 1);

        transmit.putInt("boardLength", boardLength);
        transmit.putInt("longestLine", longestLine);


        if (boardLength > 0) {

            for (int i = 0; i < boardLength; i++) {


                for (int j = 0; j < longestLine; j++) {

                    transmit.putString(String.format("l=%d e=%d i=textLine", i, j), extras.getStringExtra(String.format("l=%d e=%d i=textLine", i, j)));
                    transmit.putString(String.format("l=%d e=%d i=category", i, j), extras.getStringExtra(String.format("l=%d e=%d i=category", i, j)));
                    transmit.putStringArray(String.format("l=%d e=%d i=tags", i, j), extras.getStringArrayExtra(String.format("l=%d e=%d i=tags", i, j)));

                }
            }
        }
    }
}
