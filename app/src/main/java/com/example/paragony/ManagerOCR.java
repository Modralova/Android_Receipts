package com.example.paragony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.paragony.databinding.ActivityManagerOcrBinding;

public class ManagerOCR extends AppCompatActivity {

    public int row_height;

    ActivityManagerOcrBinding binding;

    ParagonyDatabaseTools databaseTools;

    Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseTools = new ParagonyDatabaseTools(this);
        intent =  new Intent(ManagerOCR.this, MainActivity.class);

        binding = ActivityManagerOcrBinding.inflate(getLayoutInflater());

        this.row_height = databaseTools.getRowHeight();

        System.out.println(String.format("RH: %d", this.row_height));

        setContentView(binding.getRoot());


        binding.ocrBlockHeight.setText(String.format("%d", this.row_height));

        binding.hUp.setOnClickListener(listener);
        binding.hDown.setOnClickListener(listener);
        binding.hOk.setOnClickListener(listener);


    }

    View.OnClickListener listener = v -> {

        int id = v.getId();
        int h = Integer.parseInt(binding.ocrBlockHeight.getText().toString());
        bundle = new Bundle();

        if (id == R.id.h_up) {
            h++;
        }


        if (id == R.id.h_down) {
            h--;
        }

        databaseTools.setRowHeight(h);
        bundle.putString("GRID", String.format("%d", h));
        intent.putExtras(bundle);
        binding.ocrBlockHeight.setText(String.format("%d", h));

        if (id == R.id.h_ok) {

            startActivity(intent);

        }
    };
}