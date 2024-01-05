package com.example.paragony;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paragony.databinding.ActivityTaxSettingsBinding;

import java.util.List;

public class TaxManager extends AppCompatActivity {

    ActivityTaxSettingsBinding binding;

    ParagonyDatabaseTools databaseTools;

    String TAX_VALUE;


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        databaseTools = new ParagonyDatabaseTools(this);

        List<Integer> taxValues = databaseTools.getTaxValues();

        binding = ActivityTaxSettingsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.editA.setText(taxValues.get(0).toString());
        binding.editA.setOnFocusChangeListener(focusListener);
        binding.editA.addTextChangedListener(watcher);
        binding.editB.setText(taxValues.get(1).toString());
        binding.editB.setOnFocusChangeListener(focusListener);
        binding.editB.addTextChangedListener(watcher);
        binding.editC.setText(taxValues.get(2).toString());
        binding.editC.setOnFocusChangeListener(focusListener);
        binding.editC.addTextChangedListener(watcher);
        binding.editD.setText(taxValues.get(3).toString());
        binding.editD.setOnFocusChangeListener(focusListener);
        binding.editD.addTextChangedListener(watcher);
        binding.editE.setText(taxValues.get(4).toString());
        binding.editE.setOnFocusChangeListener(focusListener);
        binding.editE.addTextChangedListener(watcher);
        binding.editF.setText(taxValues.get(5).toString());
        binding.editF.setOnFocusChangeListener(focusListener);
        binding.editF.addTextChangedListener(watcher);
        binding.editG.setText(taxValues.get(6).toString());
        binding.editG.setOnFocusChangeListener(focusListener);
        binding.editG.addTextChangedListener(watcher);


    }

    View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            int ID = v.getId();

            if (ID == R.id.edit_A) TAX_VALUE = "taxA";
            if (ID == R.id.edit_B) TAX_VALUE = "taxB";
            if (ID == R.id.edit_C) TAX_VALUE = "taxC";
            if (ID == R.id.edit_D) TAX_VALUE = "taxD";
            if (ID == R.id.edit_E) TAX_VALUE = "taxE";
            if (ID == R.id.edit_F) TAX_VALUE = "taxF";
            if (ID == R.id.edit_G) TAX_VALUE = "taxG";


        }
    };


    TextWatcher watcher = new TextWatcher() {

        Boolean check = true;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString().matches("\\d+");
            if (check) {
                databaseTools.setTaxValue(Integer.parseInt(s.toString()), TAX_VALUE);
            } else {

                Toast.makeText(TaxManager.this, getString(R.string.numeric_check), Toast.LENGTH_SHORT).show();
            }
        }
    };


//    private void setEdit(EditText edit) {
//        edit.setOnFocusChangeListener(focusListener);
//        edit.addTextChangedListener(watcher);
//    }

}
