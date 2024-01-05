package com.example.paragony;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.example.paragony.databinding.ParagraphEditLayoutBinding;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;


public class ParagraphEditDialog

        extends AppCompatDialogFragment {

    private static final String PARCELABLE = "stored_schema";

    ParagonyDatabaseTools paragonyDatabaseTools;

    ParagraphEditLayoutBinding binding;

    ParagraphEditDialogListener listener;


    public Button close, crop, remove, dbSettings;
    public TextView category;
    public EditText paragraphEdit;
    public Spinner categorySpinner, tagSpinner;
    public LinearLayout tagClipboard, frame;

    public ParagraphView paragraph;


    public interface ParagraphEditDialogListener {

        void applyData(String cropped, LinearLayout parent, int index);

    }

    public ParagraphEditDialog() {
    }

    public static ParagraphEditDialog newInstance(ParagraphView paragraph) {

        ParagraphEditDialog dialog = new ParagraphEditDialog(paragraph);

        Paragraph_schema schemaToStore = new Paragraph_schema(paragraph);

        Bundle args = new Bundle();

        args.putParcelable(PARCELABLE, schemaToStore);

        dialog.setArguments(args);

        return dialog;

    }


    public ParagraphEditDialog(ParagraphView paragraph) {

        this.paragraph = paragraph == null ? new ParagraphView(getContext()) : paragraph;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ParagraphEditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "ParagraphEditDialog");
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() != null) {

            Paragraph_schema retrievedSchema = getArguments().getParcelable(PARCELABLE);
            paragraph = getActivity().findViewById(retrievedSchema.id);

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                paragraph.setText(s.toString());
                paragraph.textLine = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        binding = ParagraphEditLayoutBinding.inflate(LayoutInflater.from(getActivity()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        dbSettings = binding.dbSettings;
        close = binding.closeEdit;
        crop = binding.crop;
        paragraphEdit = binding.editWindow;
        categorySpinner = binding.categorySpinner;
        tagSpinner = binding.tagSpinner;
        category = binding.category;
        tagClipboard = binding.tagClipboard;
        remove = binding.textRemove;


        if (getArguments() != null) {

            Paragraph_schema retriveredSchema = getArguments().getParcelable(PARCELABLE);


            if (paragraph == null) {


                paragraph = new ParagraphView(getContext());


            }


            paragraph.putData(
                    retriveredSchema.textLine,
                    retriveredSchema.getPoints(),
                    retriveredSchema.category,
                    retriveredSchema.tagsList,
                    retriveredSchema.id
            );


            paragraphEdit.addTextChangedListener(watcher);

        }


        paragraphEdit.setText(paragraph.getText().toString());
        close.setOnClickListener(v -> dismiss()
        );

        crop.setOnClickListener(v -> {

            String toCrop = paragraphEdit.getText().toString();

            int marker = paragraphEdit.getSelectionStart();

            String cropped = toCrop.substring(marker, toCrop.length());

            paragraphEdit.setText(toCrop.substring(0, marker));

            LinearLayout frame = (LinearLayout) paragraph.getParent();

            int index = frame.indexOfChild(paragraph);


            listener.applyData(cropped, frame, index);


            dismiss();

        });

        View.OnClickListener onClicklistener = v -> {


            int start = paragraphEdit.getSelectionStart();


        };

        paragraphEdit.setOnClickListener(onClicklistener);


        paragraphEdit.addTextChangedListener(watcher);

        remove.setOnClickListener(v -> {
            LinearLayout parent = (LinearLayout) paragraph.getParent();

            LinearLayout oldParent = (LinearLayout) parent.getParent();

            parent.removeView(paragraph);

            if (parent.getChildCount() < 1) {

                oldParent.removeView(parent);

            }

            this.dismiss();
        });

        dbSettings.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), DatabaseManager.class);
            startActivity(intent);

        });


        paragonyDatabaseTools = new ParagonyDatabaseTools(getActivity());


        List<String> categoryList;

        ArrayList<ItemWraper> categoryArrayList = new ArrayList<>();

        List<String[]> categoryNames = Arrays.asList(new String[][]
                {{"address", getString(R.string.address)},
                        {"currency", getString(R.string.currency)},
                        {"date", getString(R.string.date)},
                        {"item", getString(R.string.item)},
                        {"nip", getString(R.string.nip)},
                        {"per_unit", getString(R.string.per_unit)},
                        {"price", getString(R.string.price)},
                        {"quantity", getString(R.string.quantity)},
                        {"receipt_id", getString(R.string.receipt_id)},
                        {"shop", getString(R.string.shop)},
                        {"total", getString(R.string.total)},
                        {"total_tax", getString(R.string.total_tax)},
                        {"", ""}}
        );

        final String TABLE_NAME = "tags";
        categoryList = paragonyDatabaseTools.getCtegories(TABLE_NAME);

        categoryArrayList.add(new ItemWraper(getActivity(), getString(R.string.choose)));

        String catName = "";

        for (String category : categoryList) {

            for (String[] cat : categoryNames) {

                if (category.matches(cat[0])) {

                    catName = cat[1];

                    break;

                }
            }

            categoryArrayList.add(new ItemWraper(getActivity(), catName));

        }

        categoryArrayList.add(new ItemWraper(getActivity(), ""));


        StringAdapter categoryAdapter = new StringAdapter(getActivity(), categoryArrayList);

        categorySpinner.setAdapter(categoryAdapter);   //categorySpinner

        //TAGS


        List<String> tagList;
        tagList = paragonyDatabaseTools.getTags();
        ArrayList<ItemWraper> tagArrayList = new ArrayList<>();

        tagArrayList.add(new ItemWraper(getActivity(), getString(R.string.choose)));


        for (String tag : tagList) {

            tagArrayList.add(new ItemWraper(getActivity(), tag));

        }


        StringAdapter tagAdapter = new StringAdapter(getActivity(), tagArrayList);

        tagSpinner.setAdapter(tagAdapter);   //tagSpinner


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ItemWraper chosenItem = (ItemWraper) parent.getItemAtPosition(position);

                String catValue = chosenItem.getItemName();

                if (catValue != getString(R.string.choose)) {


                    paragraph.setCategory(catValue);

                    category.setText(paragraph.getCategory());


                } else {

                    category.setText(paragraph.getCategory());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//CHOOSE
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ItemWraper chosenItem = (ItemWraper) parent.getItemAtPosition(position);

                String tagValue = chosenItem.getItemName();


                if (tagValue != getString(R.string.choose)) {


                    paragraph.setNewTag(tagValue);


                    tagClipboard.removeAllViews();

                    List<String> tags = paragraph.getTags();

                    for (String tag : tags) {

                        setTagFrame(paragraph, tag);
                    }

                } else {

                    List<String> tags = paragraph.getTags();

                    for (String tag : tags) {

                        setTagFrame(paragraph, tag);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setView(binding.getRoot());

        return builder.create();


    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setAttributes(params);

    }

    public LinearLayout createFrame(int id) {

        this.frame = getDialog().findViewById(id);

        return this.frame;

    }

    private void setTagFrame(ParagraphView paragraph, String tagValue) {

        View tagBox = View.inflate(getActivity(), R.layout.tag_frame_layout, null);


        int tagBox_Id = View.generateViewId();
        tagBox.setId(tagBox_Id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        tagBox.setBackgroundResource(R.drawable.tag_background);
        tagClipboard.addView(tagBox);


        TagFrame tagFrame = new TagFrame(getActivity(), tagValue);
        int tagFrame_Id = View.generateViewId();
        tagFrame.setId(tagFrame_Id);

        LinearLayout frame = createFrame(tagBox_Id);
        frame.addView(tagFrame, 0);

        ImageView removeTagX = new ImageView(getActivity());
        removeTagX.setImageResource(R.drawable.ic_baseline_close_24);

        removeTagX.setOnClickListener(v -> {

            LinearLayout thisTagFrame = (LinearLayout) v.getParent();
            TextView tagContainer = (TextView) frame.getChildAt(0);
            String tagToRemove = tagContainer.getText().toString();
            paragraph.removeTag(tagToRemove);
            tagClipboard.removeView(thisTagFrame);

        });

        frame.addView(removeTagX, 1);

    }
}
