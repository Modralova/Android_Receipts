package com.example.paragony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.paragony.databinding.SaveDialogLayoutBinding;

public class WaitingRoomPushDialog extends AppCompatDialogFragment {

    SaveDialogLayoutBinding binding;
    public String fileName;
    StoreOcr storeOcr;
    LinearLayout textClipboard;
    Context context;

    WaitingRoomPushDialog.WaitingRoomPushListener listener;

    public WaitingRoomPushDialog(Context context, LinearLayout textClipboard, String fileName) {


        System.out.println(String.format("FILENAME: %s",fileName));

        this.textClipboard = textClipboard;
        this.fileName = fileName;
        this.context = context;

        storeOcr = new StoreOcr(context);

        if (fileName == null) {

            storeOcr.setFilename(storeOcr.fileName);

        } else {

            storeOcr.setFilename(fileName);

        }

    }

    public interface WaitingRoomPushListener {

        void applyData(String newFileName);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (WaitingRoomPushDialog.WaitingRoomPushListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "WaitingRoomPushDialog");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        binding = SaveDialogLayoutBinding.inflate(LayoutInflater.from(getActivity()));

        EditText filenameEdit = binding.filenameEdit;

        filenameEdit.setText(storeOcr.customFilename);

        View close = binding.closeEdit;

        close.setOnClickListener(v -> dismiss());


        binding.saveFile.setOnClickListener(v ->
                {

                    storeOcr.writeLinesToJson(textClipboard);

                    listener.applyData(storeOcr.customFilename);

                    dismiss();

                }

        );


        filenameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                storeOcr.setFilename(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

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


}
