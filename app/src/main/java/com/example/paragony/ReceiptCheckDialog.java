package com.example.paragony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.text.HtmlCompat;

import com.example.paragony.databinding.ReceiptCheckLayoutBinding;

import java.util.List;


public class ReceiptCheckDialog extends AppCompatDialogFragment {

    ReceiptCheckLayoutBinding binding;

    private static final String PARCELABLE = "stored_receipt_schema";
    ReceiptSchema schema;

    TextView receiptView;


    public ReceiptCheckDialog(){}

    public static ReceiptCheckDialog newInstance(ReceiptSchema schema) {

        ReceiptCheckDialog dialog = new ReceiptCheckDialog();

        Bundle args = new Bundle();

        args.putParcelable(PARCELABLE, schema);

        dialog.setArguments(args);

        return dialog;

    }




    public ReceiptCheckDialog(ReceiptSchema receiptSchema) {

        this.schema = receiptSchema;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        if(getArguments() != null){

            this.schema = getArguments().getParcelable(PARCELABLE);

        }

        binding = ReceiptCheckLayoutBinding.inflate(LayoutInflater.from(getActivity()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        receiptView = binding.receiptView;

        DataCheck check = new DataCheck(schema);

        if (check.check()) {

            List<ItemSchema> itemsList = schema.itemsList;

            String section_I = String.format(

                    getString(R.string.date) + ": %s\n" +
                            getString(R.string.shop) + ": %s\n" +
                            getString(R.string.address) + ": %s\n" +
                            getString(R.string.nip) + ": %s\n" +
                            getString(R.string.receipt_id) + ": %s\n" +
                            "\n" +
                            getString(R.string.items) + ":\n\n",
                    schema.date,
                    schema.shop,
                    schema.address,
                    schema.nip,
                    schema.receipt_id


            );


            String section_II = "";

            for (int i = 0; i < itemsList.size(); i++) {

                ItemSchema item = itemsList.get(i);

                section_II = section_II + String.format("%d. %s  %sx%s=%.2f%s", i + 1, item.item, item.quantity, item.per_unit, item.price, item.tax_type) + "\n";

            }

            String section_III = String.format(
                    "\n" +
                            getString(R.string.shopping_sum) + ": %.2f\n" +
                            getString(R.string.receipt_sum) + ": %.2f\n" +
                            "\n" +
                            getString(R.string.TAX) + ":\n" +
                            "A: %.2f\n" +
                            "B: %.2f\n" +
                            "C: %.2f\n" +
                            "D: %.2f\n" +
                            "E: %.2f\n" +
                            "F: %.2f\n" +
                            "G: %.2f\n" +
                            getString(R.string.tax_sum) + ": %.2f\n" +
                            getString(R.string.receipt_tax) +": %.2f\n\n" +
                            getString(R.string.currency) +": %s\n",
                    schema.total_sum,
                    schema.total,
                    schema.taxA,
                    schema.taxB,
                    schema.taxC,
                    schema.taxD,
                    schema.taxE,
                    schema.taxF,
                    schema.taxG,
                    schema.tax_sum,
                    schema.total_tax,
                    schema.currency

            );

            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_POSITIVE) {


                    ParagonyDatabaseTools paragonyDB;

                    paragonyDB = new ParagonyDatabaseTools(getContext());

                    paragonyDB.addReceipt(this.schema);

                    dismiss();

                }

                if (which == DialogInterface.BUTTON_NEGATIVE) {

                    System.out.println(getString(R.string.back));
                    dismiss();
                }
            };


            builder.setMessage(getString(R.string.data_preview))
                    .setNegativeButton(getString(R.string.back), listener)
                    .setPositiveButton(getString(R.string.save), listener);
            // .show();


            receiptView.setText(section_I + section_II + section_III);

        } else {


            DialogInterface.OnClickListener listener = (dialog, which) -> {

                if (which == DialogInterface.BUTTON_NEGATIVE) {

                    System.out.println(getString(R.string.back));
                    dismiss();

                }
            };


            builder.setMessage(getString(R.string.data_preview))
                    .setNegativeButton(getString(R.string.back), listener);


            receiptView.setText(check.message);


        }


        builder.setView(binding.getRoot());


        return builder.create();

    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setAttributes(params);

    }

    private class DataCheck {

        ReceiptSchema schema;
        String message = getString(R.string.required) + "\n\n";

        public DataCheck(ReceiptSchema schema) {

            this.schema = schema;

        }

        public Boolean check() {

            Boolean check = true;

            if (schema.date == null) {

                message = message + " - "+getString(R.string.date_check)+"\n";

                check = false;

            } else {

                check = schema.date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}( +)?[0-9]{1,2}:[0-9]{2}(:[0-9]{2})?");

                if (!check) {

                    message = message + " - "+getString(R.string.date_check)+"\n";

                }


            }

            if (schema.total == null) {

                message = message + " - "+getString(R.string.total_check)+"\n";

                check = false;

            }

            if (schema.total_tax == null) {

                message = message + " - "+getString(R.string.tax_check)+"\n";

                check = false;

            }


            if (schema.shop == null) {

                message = message + " - "+getString(R.string.shop_check)+"\n";

                check = false;

            }

            if (schema.itemsList.size() < 1) {

                message = message + " - "+getString(R.string.item_check)+"\n";

                check = false;

            } else {


                for (ItemSchema item : schema.itemsList) {


                    if (item.price == 0) {

                        message = message + " - " + item.item + " "+getString(R.string.item_price_check)+"\n";

                        check = false;

                    }

                    if (item.quantity == 0) {

                        message = message + " - " + item.item + " "+getString(R.string.item_quantity_check)+"\n";

                        check = false;

                    }

                    if (item.per_unit == 0) {

                        message = message + " - " + item.item + " "+getString(R.string.item_unit_check)+"\n";

                        check = false;

                    }

                    if (item.tags.equals("{\"tags\":[]}")) {

//                        message = message + " - "+  item.item + " "+getString(R.string.item_tag_check)+"\n";
                        String subMsg = "<b>"+item.item+"</b>";
                        message = message + " - "+HtmlCompat.fromHtml( subMsg,HtmlCompat.FROM_HTML_MODE_LEGACY) + " "+getString(R.string.item_tag_check)+"\n";
                        check = false;


                    }

                }
            }


            return check;
        }

    }
}
