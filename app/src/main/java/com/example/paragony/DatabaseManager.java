package com.example.paragony;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


import com.example.paragony.databinding.DatabaseManagerBinding;


public class DatabaseManager extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    @NonNull


    DatabaseManagerBinding binding;
    ParagonyDatabaseTools paragonyDatabaseTools;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dbFile;

        dbFile = this.getDatabasePath("Paragony.db");
        String f = dbFile.toString();
        System.out.println(String.format("path:%s", f));


        binding = DatabaseManagerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());



        binding.addTagEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.addTagEdit.setText("");
            }
        });



        binding.dropDatabase.setOnClickListener(dbListener);
        binding.dumpDb.setOnClickListener(dbListener);


        ArrayAdapter<CharSequence> adapter;

        adapter = new ArrayAdapter(this, R.layout.adapter_textview) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.item_view)).setText("");
                    ((TextView) v.findViewById(R.id.item_view)).setHint(getItem(getCount()).toString());
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }

        };

        adapter.setDropDownViewResource(R.layout.adapter_textview);

     //   adapter.addAll(getResources().getStringArray(R.array.db_datatypes));


    }

    public void showTagMenu(View v) {

        PopupMenu tagMenu = new PopupMenu(this, v);
        tagMenu.setOnMenuItemClickListener(this);
        tagMenu.inflate(R.menu.tag_table_edit_menu);
        tagMenu.show();


    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if (item.getItemId() == R.id.add_tag) {

            saveTag();

            return true;

        } else if (item.getItemId() == R.id.remove_tag) {

            removeTag();

            return true;
        } else {

            return false;
        }
    }

    View.OnClickListener dbListener = v -> {

        int id = v.getId();

        if (id == R.id.drop_database)  dropDatabase();

        if (id == R.id.dump_db)  dumpDatabase();


    };



    private void saveTag() {

        Toast.makeText(this, "tag", Toast.LENGTH_SHORT).show();

        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

        String newTag = binding.addTagEdit.getText().toString();


        System.out.println(String.format("tag:%s", newTag));

        if (newTag.length()!=0 || !newTag.equals("dodaj tag")) {

            paragonyDatabaseTools.addNewTag(newTag);

            binding.addTagEdit.setText("");
        } else {

            Toast.makeText(this, "wpisz wartość taga", Toast.LENGTH_SHORT).show();

        }
    }

    private void removeTag() {

        Toast.makeText(this, "tag ", Toast.LENGTH_SHORT).show();

        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

        String tagToRemove = binding.addTagEdit.getText().toString();


        if (tagToRemove.length()!=0 || !tagToRemove.equals("add new tag")) {

            if (paragonyDatabaseTools.removeTag(tagToRemove)) {

                Toast.makeText(this, "tag usunięty", Toast.LENGTH_SHORT).show();
            }
            binding.addTagEdit.setText("");
        } else {

            Toast.makeText(this, "wpisz wartość taga", Toast.LENGTH_SHORT).show();

        }


    }

    private void dumpDatabase() {

        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

        paragonyDatabaseTools.dumpDB(this);


    }


    private void dropDatabase() {

        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

        paragonyDatabaseTools.dropDatabase(this);

        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

    }



}